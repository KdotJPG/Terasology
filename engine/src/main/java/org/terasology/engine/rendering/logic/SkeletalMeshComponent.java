// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.engine.rendering.logic;

import com.google.common.collect.Lists;
import org.joml.Vector3f;
import org.terasology.engine.entitySystem.Owns;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.rendering.assets.animation.MeshAnimation;
import org.terasology.engine.rendering.assets.material.Material;
import org.terasology.engine.rendering.assets.skeletalmesh.SkeletalMesh;
import org.terasology.engine.network.Replicate;
import org.terasology.nui.Color;
import org.terasology.nui.properties.Range;
import org.terasology.engine.world.block.ForceBlockActive;

import java.util.List;
import java.util.Map;

@ForceBlockActive
public class SkeletalMeshComponent implements VisualComponent {
    public SkeletalMesh mesh;
    public Material material;

    /**
     * Should not be set manually. Stores the data of the selected animation variation.
     */
    @Replicate
    public MeshAnimation animation;

    /**
     * If true, an animation from {@link #animationPool} will be played when the current animation is done.
     */
    @Replicate
    public boolean loop;

    /**
     * When the current animation is done and loop is true then a random animation will be picked from this pool of
     * animations.
     */
    @Replicate
    public List<MeshAnimation> animationPool = Lists.newArrayList();

    public float animationRate = 1.0f;
    @Range(min = -2.5f, max = 2.5f)
    public float heightOffset;

    @Owns
    public Map<String, EntityRef> boneEntities;
    public EntityRef rootBone = EntityRef.NULL;
    public float animationTime;

    public Vector3f scale = new Vector3f(1, 1, 1);
    public Vector3f translate = new Vector3f();

    @Replicate
    public Color color = Color.WHITE;
}
