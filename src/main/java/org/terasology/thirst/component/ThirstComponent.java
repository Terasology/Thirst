// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.thirst.component;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;

/**
 * This component stores attributes of an entity that relate to its thirst.
 */
public class ThirstComponent implements Component {
    //General Thirst Settings
    /**
     * The maximum amount of Water an entity can "contain". The minimum is 0.
     */
    @Replicate
    public float maxWaterCapacity = 100;

    /**
     * The value of the entity's water capacity when it was last calculated
     */
    @Replicate
    public float lastCalculatedWater;

    /**
     * The game time when the entity's water capacity was last calculated
     */
    @Replicate
    public long lastCalculationTime;

    /**
     * The decay of thirst under normal movement condition
     */
    @Replicate
    public float normalDecayPerSecond = 0.05f;

    /**
     * The decay of thirst under sprint movement conditions
     */
    @Replicate
    public float sprintDecayPerSecond = 0.2f;

    /**
     * Current decay of thirst
     */
    @Replicate
    public float waterDecayPerSecond = normalDecayPerSecond;

    /**
     * The water capacity below which sprinting is disabled
     */
    @Replicate
    public float sprintLossThreshold = 50;

    //Health loss settings
    /**
     * The entity will begin to lose health if their thirst capacity is < this threshold. Set to 0, if you do not want
     * the entity to lose health.
     */
    @Replicate
    public float healthLossThreshold = 1;

    /**
     * The amount of health decreased at every healthDecreaseInterval
     */
    @Replicate
    public int healthDecreaseAmount = 5;

}
