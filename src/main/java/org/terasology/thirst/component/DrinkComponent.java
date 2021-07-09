// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.thirst.component;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * A component used for storing information about a drink. Specifically, it contains a float specifying how much water
 * the drink adds to an entity's water capacity.
 */
public class DrinkComponent implements Component<DrinkComponent> {
    /** The amount of water the drink adds to an entity's water capacity */
    public float filling;

    @Override
    public void copy(DrinkComponent other) {
        this.filling = other.filling;
    }
}
