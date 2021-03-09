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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;

/**
 * Client system that handles how the Thirst HUD widget component is displayed.
 */
@RegisterSystem(RegisterMode.CLIENT)
public class ThirstClientSystem extends BaseComponentSystem {
    /**
     * The logger for debugging to the log files.
     */
    private static final Logger logger = LoggerFactory.getLogger(ThirstAuthoritySystem.class);

    /**
     * An instance of the UI manager
     */
    @In
    private NUIManager nuiManager;

    @In
    private Time time;

    /**
     * Add the Thirst HUD component to the game's HUD at launch time.
     */
    @Override
    public void preBegin() {
        nuiManager.getHUD().addHUDElement("Thirst:Thirst");
    }
}
