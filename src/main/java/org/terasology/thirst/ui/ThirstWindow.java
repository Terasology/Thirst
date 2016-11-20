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
package org.terasology.thirst.ui;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.rendering.nui.widgets.UILoadBar;
import org.terasology.thirst.ThirstUtils;
import org.terasology.thirst.component.ThirstComponent;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
public class ThirstWindow extends CoreHudWidget {
    @Override
    public void initialise() {
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
