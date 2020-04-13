/*
 * Copyright 2017 MovingBlocks
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

import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.logic.console.commandSystem.annotations.Sender;
import org.terasology.logic.permission.PermissionManager;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.thirst.component.ThirstComponent;

@RegisterSystem
@Share(ThirstCommands.class)
public class ThirstCommands extends BaseComponentSystem {

    @In
    private Time time;

    /**
     * A command for testing the thirst level for an entity.
     *
     * @param client The entity who is checking it's thirst level.
     * @return Returns a message for the client informing them about their water level if they have one.
     */
    @Command(shortDescription = "Checks your thirst/water level.", runOnServer = true, requiredPermission = PermissionManager.CHEAT_PERMISSION)
    public String showThirst(@Sender EntityRef client) {
        EntityRef character = client.getComponent(ClientComponent.class).character;
        if (character.hasComponent(ThirstComponent.class)) {
            ThirstComponent thirst = character.getComponent(ThirstComponent.class);
            return "Current Water Level: " + ThirstUtils.getThirstForEntity(character) + "/" + thirst.maxWaterCapacity;
        } else {
            return "You don't have a thirst level.";
        }
    }

    /**
     * A command for modifying your thirst level.
     *
     * @param newWater The new thirst level for the client. This has to be above 0 and below the max water capacity.
     * @param client   The client which is changing it's thirst level.
     * @return Returns a message for the client telling him about their new thirst level if they have one.
     */
    @Command(shortDescription = "Sets your current thirst level.", runOnServer = true, requiredPermission = PermissionManager.CHEAT_PERMISSION)
    public String setThirst(@CommandParam(value = "WaterLevel") float newWater, @Sender EntityRef client) {
        EntityRef character = client.getComponent(ClientComponent.class).character;
        if (!character.hasComponent(ThirstComponent.class)) {
            return "You don't have a thirst level.";
        }
        ThirstComponent thirst = character.getComponent(ThirstComponent.class);
        if (newWater < 0) {
            thirst.lastCalculatedWater = 0;
            thirst.lastCalculationTime = time.getGameTimeInMs();
            character.saveComponent(thirst);
            return "Water level cannot be below 0. Setting to 0.";
        }
        if (newWater > thirst.maxWaterCapacity) {
            thirst.lastCalculatedWater = thirst.maxWaterCapacity;
            thirst.lastCalculationTime = time.getGameTimeInMs();
            character.saveComponent(thirst);
            return "Water level cannot be above Max Water Capacity. Setting to Max(" + thirst.maxWaterCapacity + ")";
        }
        thirst.lastCalculatedWater = newWater;
        thirst.lastCalculationTime = time.getGameTimeInMs();
        character.saveComponent(thirst);
        return "Water level successfully set to: " + newWater;
    }

    /**
     * A command for changing your maximum water level.
     *
     * @param newMax The new maximum water level. Has to be above 0.
     * @param client The client which is changing it's water level.
     * @return Returns a message for the client telling him whether the command was successful.
     */
    @Command(shortDescription = "Sets your max water level.", runOnServer = true, requiredPermission = PermissionManager.CHEAT_PERMISSION)
    public String setMaxThirst(@CommandParam(value = "MaxWaterLevel") float newMax, @Sender EntityRef client) {
        EntityRef character = client.getComponent(ClientComponent.class).character;
        if (!character.hasComponent(ThirstComponent.class)) {
            return "You don't have a thirst level.";
        }
        ThirstComponent thirst = character.getComponent(ThirstComponent.class);
        if (newMax <= 0) {
            thirst.maxWaterCapacity = 100;
            character.saveComponent(thirst);
            return "Max Water Level cannot be below or equal to 0. Setting to default (100)";
        }
        thirst.maxWaterCapacity = newMax;
        character.saveComponent(thirst);
        return "Max Water Level successfully set to: " + newMax;
    }
}
