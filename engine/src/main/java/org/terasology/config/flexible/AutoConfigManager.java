// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.config.flexible;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.context.Context;
import org.terasology.engine.SimpleUri;
import org.terasology.engine.module.ModuleManager;
import org.terasology.engine.paths.PathManager;
import org.terasology.module.ModuleEnvironment;
import org.terasology.persistence.serializers.Serializer;
import org.terasology.reflection.TypeInfo;
import org.terasology.registry.InjectionHelper;
import org.terasology.utilities.ReflectionUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.Set;

public class AutoConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(AutoConfigManager.class);

    private final Set<AutoConfig> loadedConfigs = Sets.newHashSet();
    private final Serializer<?> serializer;

    public AutoConfigManager(Serializer<?> serializer) {
        this.serializer = serializer;
    }

    public void loadConfigsIn(Context context) {
        ModuleEnvironment environment = context.get(ModuleManager.class).getEnvironment();

        for (Class<? extends AutoConfig> configClass : environment.getSubtypesOf(AutoConfig.class)) {
            if (context.get(configClass) != null) {
                // We've already initialized this config before
                continue;
            }

            SimpleUri configId = ReflectionUtil.getFullyQualifiedSimpleUriFor(configClass, environment);
            loadConfig(configClass, configId, context);
        }
    }

    private <T extends AutoConfig> void loadConfig(Class<T> clazz, SimpleUri id, Context context) {
        Optional<T> optionalConfig = InjectionHelper.safeCreateWithConstructorInjection(clazz, context);

        if (!optionalConfig.isPresent()) {
            logger.error("Unable to instantiate config {}", id);
            return;
        }

        T config = optionalConfig.get();
        config.setId(id);

        loadedConfigs.add(config);
        context.put(clazz, config);

        loadSettingsFromDisk(clazz, config);
    }

    private <T extends AutoConfig> void loadSettingsFromDisk(Class<T> configClass, T config) {

        Path configPath = getConfigPath(config.getId());

        if (!Files.exists(configPath)) {
            return;
        }
        try (InputStream inputStream = Files.newInputStream(configPath, StandardOpenOption.READ)) {
            T loadedConfig = (T) serializer.deserialize(TypeInfo.of(configClass), inputStream).get();
            mergeConfig(configClass, loadedConfig, config);
        } catch (IOException e) {
            logger.error("Error while loading config {} from disk", config.getId(), e);
        }
    }

    private <T extends AutoConfig> void mergeConfig(Class<T> configClass, T loadedConfig, T config) {
        Set<Field> fields = AutoConfig.getSettingFieldsIn(configClass);
        for (Field field : fields) {
            try {
                Object value = ((Setting) field.get(loadedConfig)).get();
                ((Setting) field.get(config)).set(value);
            } catch (IllegalAccessException e) {
                // ignore `AutoConfig.getSettingFieldIn` returns PUBLIC fields
            }
        }
    }

    public void saveConfigsToDisk() {
        // TODO: Come up with uniform mechanism to save configs;
        //  currently hardcoded Config is saved right after it is modified
        for (AutoConfig loadedConfig : loadedConfigs) {
            saveConfigToDisk(loadedConfig);
        }
    }

    private void saveConfigToDisk(AutoConfig config) {
        // TODO: Save when screen for config closed
        Path configPath = getConfigPath(config.getId());
        try (OutputStream output = Files.newOutputStream(configPath, StandardOpenOption.CREATE)) {
            serializer.serialize(config, TypeInfo.of(AutoConfig.class), output);
        } catch (IOException e) {
            logger.error("Error while saving config {} to disk", config.getId(), e);
        }
    }

    private Path getConfigPath(SimpleUri configId) {
        Path filePath = PathManager.getInstance()
                .getConfigsPath()
                .resolve(configId.getModuleName().toString())
                .resolve(configId.getObjectName().toString() + ".cfg");

        // This call ensures that the entire directory structure (like configs/engine/) exists.
        ensureDirectoryExists(filePath);
        return filePath;
    }

    private void ensureDirectoryExists(Path filePath) {
        try {
            Files.createDirectories(filePath.getParent());
        } catch (Exception e) {
            throw new RuntimeException("Cannot create directory for flexibleConfig " + filePath.getFileName() + "!");
        }
    }
}
