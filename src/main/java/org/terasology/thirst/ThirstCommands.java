// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.thirst;

import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.console.commandSystem.annotations.Command;
import org.terasology.engine.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.engine.logic.console.commandSystem.annotations.Sender;
import org.terasology.engine.logic.permission.PermissionManager;
import org.terasology.engine.network.ClientComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
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
    @Command(shortDescription = "Checks your thirst/water level.", runOnServer = true, requiredPermission =
            PermissionManager.CHEAT_PERMISSION)
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
     * @param newWater The new thirst level for the client. This has to be above 0 and below the max water
     *         capacity.
     * @param client The client which is changing it's thirst level.
     * @return Returns a message for the client telling him about their new thirst level if they have one.
     */
    @Command(shortDescription = "Sets your current thirst level.", runOnServer = true, requiredPermission =
            PermissionManager.CHEAT_PERMISSION)
    public String setThirst(@Sender EntityRef client, @CommandParam(value = "WaterLevel") float newWater) {
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
    @Command(shortDescription = "Sets your max water level.", runOnServer = true, requiredPermission =
            PermissionManager.CHEAT_PERMISSION)
    public String setMaxThirst(@Sender EntityRef client, @CommandParam(value = "MaxWaterLevel") float newMax) {
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
