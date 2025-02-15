// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.engine.rendering.logic;

import org.terasology.engine.rendering.assets.material.Material;
import org.terasology.engine.network.Replicate;
import org.terasology.nui.Color;
import org.terasology.engine.rendering.assets.mesh.Mesh;
import org.terasology.engine.world.block.ForceBlockActive;

@ForceBlockActive
public final class MeshComponent implements VisualComponent {

    @Replicate
    public Mesh mesh;
    @Replicate
    public Material material;

    // TODO: This should be a setting on the material
    @Replicate
    public boolean translucent;

    // Use this for the mesh to light itself.  Useful for held lights where the point light is inside the mesh.
    @Replicate
    public float selfLuminance;

    public boolean hideFromOwner;

    // TODO: Some sort of Texture + Shader type?
    //public String material;

    // This should be elsewhere I think, probably in the material
    @Replicate
    public Color color = Color.WHITE;

}
