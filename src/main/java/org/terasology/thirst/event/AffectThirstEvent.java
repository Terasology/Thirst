// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.thirst.event;

import org.terasology.engine.entitySystem.event.AbstractValueModifiableEvent;

/**
 * This event is sent out by the {@link org.terasology.thirst.ThirstAuthoritySystem} to allow for other systems to
 * modify thirst decay.
 */
public class AffectThirstEvent extends AbstractValueModifiableEvent {
    public AffectThirstEvent(float baseValue) {
        super(baseValue);
    }
}
