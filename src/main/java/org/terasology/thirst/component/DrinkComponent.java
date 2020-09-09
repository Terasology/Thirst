// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.thirst.component;

import org.terasology.engine.entitySystem.Component;

/**
 * A component used for storing information about a drink. Specifically, it contains a float specifying how much water
 * the drink adds to an entity's water capacity.
 */
public class DrinkComponent implements Component {
    /**
     * The amount of water the drink adds to an entity's water capacity
     */
    public float filling;
}
