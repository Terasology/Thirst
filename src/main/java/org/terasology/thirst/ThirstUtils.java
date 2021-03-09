/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
     * @return       the current thirst of the entity
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
