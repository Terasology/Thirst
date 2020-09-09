// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.thirst;

import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.thirst.component.ThirstComponent;

/**
 * This class contains utility methods for the Thirst module. Specifically, it includes a method that returns the thirst
 * of a given entity.
 */
public final class ThirstUtils {

    private ThirstUtils() {
    }

    /**
     * Returns the current thirst of a given entity.
     *
     * @param entity the entity whose thirst value needs to be returned
     * @return the current thirst of the entity
     */
    public static float getThirstForEntity(EntityRef entity) {
        ThirstComponent thirst = entity.getComponent(ThirstComponent.class);
        if (thirst == null) {
            return 0;
        }

        long gameTime = CoreRegistry.get(Time.class).getGameTimeInMs();
        float waterDecay = thirst.waterDecayPerSecond * (gameTime - thirst.lastCalculationTime) / 1000f;
        return Math.max(0, thirst.lastCalculatedWater - waterDecay);
    }
}
