// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.thirst.ui;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.widgets.UILoadBar;
import org.terasology.thirst.ThirstUtils;
import org.terasology.thirst.component.ThirstComponent;

/**
 * This HUD widget will display information relating to the player's thirst, specifically, how thirsty the player is.
 *
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
public class ThirstWindow extends CoreHudWidget {

    /**
     * Initialize the HUD widget and the UILoadBar that will display the player's thirst.
     */
    @Override
    public void initialise() {
        // Initialize a UILoadBar to display the player's thirst.
        UILoadBar thirst = find("thirst", UILoadBar.class);
        thirst.bindVisible(new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                EntityRef character = CoreRegistry.get(LocalPlayer.class).getCharacterEntity();
                return character != null && character.hasComponent(ThirstComponent.class);
            }
        });
        thirst.bindValue(
                new Binding<Float>() {
                    @Override
                    public Float get() {
                        EntityRef character = CoreRegistry.get(LocalPlayer.class).getCharacterEntity();
                        if (character == null || !character.hasComponent(ThirstComponent.class)) {
                            return 0.0f;
                        }

                        ThirstComponent thirst = character.getComponent(ThirstComponent.class);
                        return ThirstUtils.getThirstForEntity(character) / thirst.maxWaterCapacity;
                    }

                    @Override
                    public void set(Float value) {
                    }
                });
    }
}
