// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.engine.logic.ai;

import org.joml.Vector3f;
import org.terasology.engine.entitySystem.Component;

public final class HierarchicalAIComponent implements Component {

    //how often updates are progressed, handle whit care
    public int updateFrequency;
    public long lastProgressedUpdateAt;

    public Vector3f movementTarget = new Vector3f();

    public long lastChangeOfDirectionAt;
    public long lastChangeOfMovementAt;
    public long lastChangeOfidlingtAt;
    public long lastChangeOfDangerAt;

    //how long ai move
    public int moveUpdateTime = 600;
    // how long ai move to one direction
    public int directionUpdateTime = 300;
    // how long ai idles
    public int idlingUpdateTime = 500;
    // how often danger direction is checked
    public int dangerUpdateTime = 100;

    public boolean dieIfPlayerFar = true;
    public int dieDistance = 2000;

    //define type of AI
    public boolean hunter;
    public boolean aggressive;
    public boolean wild;
    public boolean flying;

    //AI properties
    // if flying maximum altitude
    public int maxAltitude = 200;
    //AI moves more whit higher values
    public int hectic = 2;
    //AI runs more straight lines whit higher values
    public int straightLined = 2;
    //accurate how accurate AI kills you, values from 0 to up. Do not give negative values something will turn oposite
    public float forgiving = 5f;

    //how well this AI finds player when hunter
    public int playerSense = 30;
    //how close AI comes when hunter
    public int playerdistance = 3;
    //does damage if nearer that this when aggressive
    public int attackDistance = 1;
    //runs if player nearer than this when wild
    public int runDistance = 30;
    //start attack instead running when wild
    public int panicDistance = 10;

    //doing something
    public boolean inDanger;


    //TODO remove this when fight system is ready!!!
    public int damage = 50;
    public int damageFrequency = 500;
}
