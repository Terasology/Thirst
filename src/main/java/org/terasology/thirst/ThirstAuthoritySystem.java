/*
 * Copyright 2019 MovingBlocks
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
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.prefab.PrefabManager;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.AliveCharacterComponent;
import org.terasology.engine.logic.characters.CharacterMoveInputEvent;
import org.terasology.engine.logic.common.ActivateEvent;
import org.terasology.engine.logic.delay.DelayManager;
import org.terasology.engine.logic.delay.PeriodicActionTriggeredEvent;
import org.terasology.module.health.events.ActivateRegenEvent;
import org.terasology.module.health.events.DeactivateRegenEvent;
import org.terasology.module.health.events.DoDamageEvent;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.engine.logic.inventory.ItemComponent;
import org.terasology.engine.logic.players.event.OnPlayerRespawnedEvent;
import org.terasology.engine.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.WorldComponent;
import org.terasology.fluid.component.FluidContainerItemComponent;
import org.terasology.fluid.system.FluidUtils;
import org.terasology.thirst.component.DrinkComponent;
import org.terasology.thirst.component.ThirstComponent;
import org.terasology.thirst.event.AffectThirstEvent;
import org.terasology.thirst.event.DrinkConsumedEvent;

import static org.terasology.module.health.systems.RegenAuthoritySystem.BASE_REGEN;

/**
 * This authority system handles drink consumption by various entities.
 */
@RegisterSystem(value = RegisterMode.AUTHORITY)
public class ThirstAuthoritySystem extends BaseComponentSystem {
    public static final String THIRST_DAMAGE_ACTION_ID = "Thirst Damage";

    private static final Logger logger = LoggerFactory.getLogger(ThirstAuthoritySystem.class);

    @In
    private EntityManager entityManager;
    @In
    private InventoryManager inventoryManager;
    @In
    private PrefabManager prefabManager;
    @In
    private Time time;
    @In
    private DelayManager delayManager;

    private boolean destroyDrink;

    /**
     * The interval (in milliseconds) at which healthDecreaseAmount (thirstComponent) is applied to the component.
     */
    private int healthDecreaseInterval = 60000;

    public void postBegin() {
        boolean processedOnce = false;
        for (EntityRef entity : entityManager.getEntitiesWith(WorldComponent.class)) {
            if (!processedOnce) {
                delayManager.addPeriodicAction(entity, THIRST_DAMAGE_ACTION_ID, 0, healthDecreaseInterval);
                processedOnce = true;
            } else {
                logger.warn("More than one entity with WorldComponent found");
            }
        }
    }

    /**
     * Deals a unit of thirst damage to the character.
     */
    @ReceiveEvent
    public void onPeriodicActionTriggered(PeriodicActionTriggeredEvent event, EntityRef unusedEntity) {
        if (event.getActionId().equals(THIRST_DAMAGE_ACTION_ID)) {
            for (EntityRef entity : entityManager.getEntitiesWith(ThirstComponent.class, AliveCharacterComponent.class)) {
                ThirstComponent thirst = entity.getComponent(ThirstComponent.class);
                thirst.lastCalculatedWater = Math.max(0,
                        thirst.lastCalculatedWater - (healthDecreaseInterval * thirst.waterDecayPerSecond) / 1000);
                thirst.lastCalculationTime = time.getGameTimeInMs();
                entity.saveComponent(thirst);

                // Check to see if health should be decreased
                if ((ThirstUtils.getThirstForEntity(entity) < thirst.healthLossThreshold)) {
                    Prefab thirstDamagePrefab = prefabManager.getPrefab("thirst:thirstDamage");
                    entity.send(new DoDamageEvent(thirst.healthDecreaseAmount, thirstDamagePrefab));
                    entity.saveComponent(thirst);
                }
            }
        }
    }

    /**
     * Cancels natural regeneration for an entity if its thirst level is lower than the healthLossThreshold.
     *
     * @param event  The ActivateRegenEvent, called before an entity is about to be regenerated.
     * @param entity The entity which is being regenerated.
     * @param thirst The ThirstComponent object, containing settings for Thirst.
     */
    @ReceiveEvent
    public void onHealthRegen(ActivateRegenEvent event, EntityRef entity, ThirstComponent thirst) {
        if (ThirstUtils.getThirstForEntity(entity) < thirst.healthLossThreshold && event.id.equals(BASE_REGEN)) {
            entity.send(new DeactivateRegenEvent());
        }
    }

    /**
     * Initialize thirst attributes for a spawned player. Called when a player is spawned.
     *
     * @param event  the event corresponding to the spawning of the player
     * @param player a reference to the player entity
     * @param thirst the player's thirst component (to be initialized)
     */
    @ReceiveEvent
    public void onPlayerSpawn(OnPlayerSpawnedEvent event, EntityRef player, ThirstComponent thirst) {
        resetThirst(player, thirst);
    }

    /**
     * Initialize thirst attributes for a respawned player. Called when a player is respawned.
     *
     * @param event  the event corresponding to the respawning of the player
     * @param player a reference to the player entity
     * @param thirst the player's thirst component (to be initialized)
     */
    @ReceiveEvent
    public void onPlayerRespawn(OnPlayerRespawnedEvent event, EntityRef player, ThirstComponent thirst) {
        resetThirst(player, thirst);
    }

    private void resetThirst(EntityRef player, ThirstComponent thirst) {
        thirst.lastCalculatedWater = thirst.maxWaterCapacity;
        thirst.lastCalculationTime = time.getGameTimeInMs();
        player.saveComponent(thirst);
    }

    /**
     * Applies the drink's filling attribute to the instigator of the ActionEvent (the entity consuming the drink).
     *
     * @param event the event corresponding to the interaction with the drink
     * @param item  the item that the player is drinking
     * @param drink the drink component associated with the item being consumed
     */
    @ReceiveEvent(priority= EventPriority.PRIORITY_LOW)
    public void drinkConsumed(ActivateEvent event, EntityRef item, DrinkComponent drink) {
        float filling = drink.filling;
        EntityRef instigator = event.getInstigator();
        ThirstComponent thirst = instigator.getComponent(ThirstComponent.class);
        if (thirst != null) {
            thirst.lastCalculatedWater = Math.min(thirst.maxWaterCapacity, ThirstUtils.getThirstForEntity(instigator) + filling);
            thirst.lastCalculationTime = time.getGameTimeInMs();
            instigator.saveComponent(thirst);
            item.send(new DrinkConsumedEvent(event));

            if (destroyDrink) {
                event.consume();
                destroyDrink = false;
            }
        }
    }

    /**
     * Deals with events happening after drink consumption, like removing water from the vessel
     *
     * @param event the event corresponding to the consumption of a drink
     * @param item  the item that the player is drinking
     */
    @ReceiveEvent(components = ItemComponent.class, priority = EventPriority.PRIORITY_TRIVIAL)
    public void usedItem(DrinkConsumedEvent event, EntityRef item) {
        if (item.hasComponent(FluidContainerItemComponent.class)) {
            EntityRef owner = item.getOwner();
            final EntityRef removedItem = inventoryManager.removeItem(owner, event.getInstigator(), item, false, 1);
            if (removedItem != null) {
                FluidUtils.setFluidForContainerItem(removedItem, null);
                if (!inventoryManager.giveItem(owner, event.getInstigator(), removedItem)) {
                    removedItem.destroy();
                }
            }
        } else {
            ItemComponent itemComp = item.getComponent(ItemComponent.class);
            if (itemComp.consumedOnUse) {
                destroyDrink = false;

                if (itemComp.baseDamage != Integer.MIN_VALUE) {
                    itemComp.baseDamage = Integer.MIN_VALUE;
                } else {
                    destroyDrink = true;
                    itemComp.baseDamage = 1;
                    inventoryManager.removeItem(event.getInstigator(), event.getInstigator(), item, true, 1);
                }
            }
        }
    }

    /**
     * Updates the thirst attribute of the character upon movement, so that moving causes players to become thirsty.
     *
     * @param event     the event associated with the movement of the character
     * @param character the character that has moved
     * @param thirst    the thirst component associated with the character
     */
    @ReceiveEvent(components = {ThirstComponent.class})
    public void characterMoved(CharacterMoveInputEvent event, EntityRef character, ThirstComponent thirst) {
        float expectedDecay = event.isRunning() ? thirst.sprintDecayPerSecond : thirst.normalDecayPerSecond;
        // Send event to allow for other systems to modify thirst decay.
        AffectThirstEvent affectThirstEvent = new AffectThirstEvent(expectedDecay);
        character.send(affectThirstEvent);
        expectedDecay = affectThirstEvent.getResultValue();
        if (expectedDecay != thirst.waterDecayPerSecond) {
            // Recalculate current thirst and apply new decay
            thirst.lastCalculatedWater = ThirstUtils.getThirstForEntity(character);
            thirst.lastCalculationTime = time.getGameTimeInMs();
            thirst.waterDecayPerSecond = expectedDecay;
            character.saveComponent(thirst);
        }
    }
}
