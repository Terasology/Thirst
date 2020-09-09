// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
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
