package main.logic.combat;

import java.util.List;

import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.actor.SkillRank;
import main.entity.actor.SkillType;
import main.entity.item.InventorySelectionKey;
import main.entity.item.Item;
import main.entity.item.ItemSource;
import main.entity.item.ItemTrait;
import main.entity.item.ItemType;
import main.entity.zone.Zone;
import main.logic.AwarenessStatus;
import main.logic.RPGlib;
import main.logic.AI.FacingCalculator;
import main.presentation.message.FormattedMessageBuilder;
import main.presentation.message.MessageBuffer;

public class CombatAttackCalculator extends AbstractCombatAttackCalculator
{
	private static final int CRITICAL_HIT_NOISE = 0;
	private static final int HIT_ON_NATURAL_ARMOR_NOISE = 2;
	private static final int HIT_ON_WORN_ARMOR_NOISE = 4;
	private static final int HIT_ON_UPGRADED_ARMOR_NOISE = 6;
	private static final int HIT_ON_SHIELD_NOISE = 8;

	private static final int PARTIAL_DAMAGE_RECEIVED_NOISE = 2;
	private static final int FULL_DAMAGE_RECEIVED_NOISE = 4;
	
	private static CombatAttackCalculator instance = null;
	
	private CombatAttackCalculator() {}
	
	public static CombatAttackCalculator getInstance()
	{
		if (instance == null)
			instance = new CombatAttackCalculator();
		
		return instance;
	}
	
	@Override
	public void handleAttack(Actor attacker, Item attackingWeapon, Actor defender)
	{
		//for all events, right now an action cost of 0 is being set because it's not used anyway - a constant duration is returned regardless of attack count
		
		Actor player = data.getPlayer();
		if (attacker == player)
			data.setTarget(defender);
		if (defender == player)
			data.setTarget(attacker);
		
		boolean canSeeSource = engine.canPlayerSeeActor(attacker);
		boolean canSeeTarget = engine.canPlayerSeeActor(defender);
		boolean isCriticalHit = isCriticalHit(attacker, defender);
		
		//calculate base damage
		int rawDamage = RPGlib.roll(attackingWeapon.getDamage(), isCriticalHit);		
		
		//check shields (ignore if no armaments with CR, ignore if attacker is unseen, ignore if attacker is beside, diagonally behind, or behind)
		if (defenderCanParry(attacker, defender))
		{
			List<Item> parryTargets = defender.getShields();
			Item parryingItem = parryTargets.get(RPGlib.randInt(0, parryTargets.size() - 1));
			
			//parrying item was able to completely block the damage
			if (RPGlib.percentage(parryingItem.getCR()))
			{
				if (!parryingItem.isUpgraded())
					sendDeflectMessage(attacker, defender, canSeeSource, canSeeTarget);
				
				damageArmorAndWeapon(attacker, defender, attackingWeapon, parryingItem, rawDamage, canSeeSource, canSeeTarget);
				return;
			}
		}
		
		Item armor = null;
		
		if (defenderGetsBenefitOfArmor(attacker, defender))
		{
			List<Item> armorTargets = defender.getArmor();
			armor = armorTargets.get(RPGlib.randInt(0, armorTargets.size() - 1));		//Note that this usually means EITHER that actor's natural AR, or the AR of their armor.
																						//This is probably fine, because an actor with natural armor would be wearing armor over unprotected areas.
		}
		
		//damage defender
		damageDefender(attacker, defender, attackingWeapon, armor, rawDamage, canSeeSource, canSeeTarget);
		
		if (isCriticalHit && engine.canPlayerSeeActor(attacker))
			MessageBuffer.addMessage("It was a critical hit!");
		
		//deal with armor damage after actor damage so the messages are in the right order
		if (armor != null)
			damageArmorAndWeapon(attacker, defender, attackingWeapon, armor, rawDamage, canSeeSource, canSeeTarget);
		
		//check for defender death
		if (defender.getCurHp() <= 0)	//valid check because the data layer has already received and applied the damage
		{
			if (canSeeTarget)
				MessageBuffer.addMessage(new FormattedMessageBuilder("@1the is killed!").setSource(defender).setSourceVisibility(canSeeTarget).format());
			sendEventToObservers(InternalEvent.deathInternalEvent(data.getActorIndex(defender)));
			
			awardKillExperience(attacker, defender);
		}
		else
		{
			updateDefenderToFaceAttacker(attacker, defender);	//TODO: this might not happen if the attack comes from an unseen source (even backstabbing)
		}
	}

	public boolean isCriticalHit(Actor attacker, Actor defender)
	{
		SkillRank stealth = attacker.getSkillRank(SkillType.STEALTH);
		
		switch (stealth)
		{
		case UNKNOWN:
			return false;
		case UNSKILLED:
		case NOVICE:
		case ADEPT:		//TODO: update this if if I give novice/adept a different critical hit benefit
			return !canDefenderSeeAttacker(defender, attacker);
		case EXPERT:
			return !canDefenderSeeAttacker(defender, attacker) || FacingCalculator.getInstance().isActorBehindTarget(attacker, defender);
		case MASTER:
			return !canDefenderSeeAttacker(defender, attacker) || FacingCalculator.getInstance().isActorDiagonallyBehindTarget(attacker, defender);
		default:
			return false;
		}
	}

	private void damageDefender(Actor attacker, Actor defender, Item attackingWeapon, Item armor, int rawDamage, boolean canSeeSource, boolean canSeeTarget)
	{
		//if the thing that's blocking the attack is upgraded, there's nothing to do here - the defender takes no damage, and the noise and special message happen when damaging the armor
		if (armor != null && armor.isUpgraded())
			return;
		
		int damageReduction = 0;
				
		if (armor != null)
			damageReduction = armor.getARAdjustedForCondition();
		
		if (attackingWeapon.hasTrait(ItemTrait.BLUNT))
			damageReduction /= 2;
		
		int damageToDefender = rawDamage - damageReduction;
		String attackMessage = "";
		
		if (damageToDefender <= 0)
		{
			attackMessage = "@1the fail%1s to hurt @2the.";
			sendEventToObservers(InternalEvent.waitInternalEvent(data.getActorIndex(attacker), 0));
		}
		else if (attackingWeapon.isUpgraded())
		{
			attackMessage = "@1the deliver%1s a brutal blow to @2the.";
			sendEventToObservers(InternalEvent.attackInternalEvent(data.getActorIndex(attacker), data.getActorIndex(defender), damageToDefender, 0));
			sendEventToObservers(InternalEvent.downgradeItemInternalEvent(data.getActorIndex(attacker), new InventorySelectionKey(ItemSource.EQUIPMENT, attacker.getIndexOfEquippedItem(attackingWeapon))));
		}
		else
		{
			String hit = "hit";
			
			if (attackingWeapon.getType() == ItemType.VIRTUAL_ITEM)	//if it's virtual, it's a natural weapon attack (claw, bite, etc.)
				hit = attackingWeapon.getName();
			
			attackMessage = "@1the " + hit + "%1s @2the.";
			sendEventToObservers(InternalEvent.attackInternalEvent(data.getActorIndex(attacker), data.getActorIndex(defender), damageToDefender, 0));
		}
		
		if (damageToDefender == rawDamage)
		{
			sendEventToObservers(InternalEvent.makeNoiseEvent(data.getActorIndex(attacker), CRITICAL_HIT_NOISE));
			sendEventToObservers(InternalEvent.makeNoiseEvent(data.getActorIndex(defender), FULL_DAMAGE_RECEIVED_NOISE));
		}
		else if (damageToDefender > 0)
		{
			sendEventToObservers(InternalEvent.makeNoiseEvent(data.getActorIndex(attacker), HIT_ON_WORN_ARMOR_NOISE));
			sendEventToObservers(InternalEvent.makeNoiseEvent(data.getActorIndex(defender), PARTIAL_DAMAGE_RECEIVED_NOISE));
		}
		
		MessageBuffer.addMessage(new FormattedMessageBuilder(attackMessage).setSource(attacker).setTarget(defender).setSourceVisibility(canSeeSource).setTargetVisibility(canSeeTarget).format());
	}

	private void sendDeflectMessage(Actor attacker, Actor defender, boolean canSeeSource, boolean canSeeTarget)
	{
		String attackMessage = "@2the deflect%2s @1thes attack.";
		sendEventToObservers(InternalEvent.makeNoiseEvent(data.getActorIndex(attacker), HIT_ON_SHIELD_NOISE));
		sendEventToObservers(InternalEvent.waitInternalEvent(data.getActorIndex(attacker), 0));
		MessageBuffer.addMessage(new FormattedMessageBuilder(attackMessage).setSource(attacker).setTarget(defender).setSourceVisibility(canSeeSource).setTargetVisibility(canSeeTarget).format());
	}

	private void damageArmorAndWeapon(Actor attacker, Actor defender, Item attackingWeapon, Item armor, int rawDamage, boolean canSeeSource, boolean canSeeTarget)
	{
		//armor is upgraded; no damage to armor, weapon, or actor
		if (armor.isUpgraded())
		{
			String attackMessage = "@1thes attack glances off a makeshift plate on @2thes armor, sending it flying.";
			sendEventToObservers(InternalEvent.waitInternalEvent(data.getActorIndex(attacker), 0));
			sendEventToObservers(InternalEvent.makeNoiseEvent(data.getActorIndex(attacker), HIT_ON_UPGRADED_ARMOR_NOISE));
			sendEventToObservers(InternalEvent.downgradeItemInternalEvent(data.getActorIndex(defender), new InventorySelectionKey(ItemSource.EQUIPMENT, defender.getIndexOfEquippedItem(armor))));
			MessageBuffer.addMessage(new FormattedMessageBuilder(attackMessage).setSource(attacker).setTarget(defender).setSourceVisibility(canSeeSource).setTargetVisibility(canSeeTarget).format());
			return;
		}
		
		//determine damage to armor
		double damageToArmorMultiplier = 0;
		
		if (attackingWeapon.hasTrait(ItemTrait.SHARP))
			damageToArmorMultiplier = .5;
		else if (attackingWeapon.hasTrait(ItemTrait.BLUNT) || attackingWeapon.getType() == ItemType.VIRTUAL_ITEM)	//natural attacks are untyped, but should still damage armor
			damageToArmorMultiplier = 1;
		
		int damageToArmor = (int)(rawDamage * damageToArmorMultiplier);
		damageToArmor -= getItemDR(armor, defender);
		
		//no damage to armor (strong armor material, untyped weapon, or sharp weapon with a roll of 1)
		if (damageToArmor <= 0)
			return;
		
		int damageToWeapon = Math.min(armor.getARAdjustedForCondition(), damageToArmor) - getItemDR(attackingWeapon, attacker);
		
		if (attackingWeapon.getType() == ItemType.VIRTUAL_ITEM)		//for now, natural weapons can't be damaged
			damageToWeapon = 0;
		
		if (armor.getType() != ItemType.VIRTUAL_ITEM)	//for now, natural armor can't be damaged, but it can still damage weapons
		{
			sendEventToObservers(InternalEvent.changeInventoryItemHpInternalEvent(data.getActorIndex(defender), new InventorySelectionKey(ItemSource.EQUIPMENT, defender.getIndexOfEquippedItem(armor)), damageToArmor * -1));
		
			//manage armor destruction and degradation
			double armorConditionBeforeAttack = armor.getConditionModifer();
			
			boolean armorDestroyed = (armor.getCurHp() <= 0 ? true : false);
			
			if (armorDestroyed)
			{
				sendEventToObservers(InternalEvent.deleteHeldItemInternalEvent(data.getActorIndex(defender), defender.getIndexOfEquippedItem(armor), 1));
				sendEventToObservers(InternalEvent.createItemOnGroundInternalEvent(ItemType.SCRAP, data.getCurrentZone().getCoordsOfActor(defender), 1));
			}
			
			String armorEffect = getDescriptionOfCondition(armorDestroyed, armor.getConditionModifer(), armorConditionBeforeAttack);
			
			if (armorEffect != null)
				MessageBuffer.addMessage(new FormattedMessageBuilder("@2his " + armor.getName() + " is " + armorEffect).setTarget(defender).setSourceVisibility(canSeeSource).setTargetVisibility(canSeeTarget).format());
		}
		
		//manage weapon destruction and degradation
		if (damageToWeapon <= 0)
			return;
		
		double weaponConditionBeforeAttack = attackingWeapon.getConditionModifer();
		
		sendEventToObservers(InternalEvent.changeInventoryItemHpInternalEvent(data.getActorIndex(attacker), new InventorySelectionKey(ItemSource.EQUIPMENT, attacker.getIndexOfEquippedItem(attackingWeapon)), damageToWeapon * -1));
		
		boolean weaponDestroyed = (attackingWeapon.getCurHp() <= 0 ? true : false);
		
		if (weaponDestroyed)
		{
			sendEventToObservers(InternalEvent.deleteHeldItemInternalEvent(data.getActorIndex(attacker), attacker.getIndexOfEquippedItem(attackingWeapon), 1));
			sendEventToObservers(InternalEvent.createItemOnGroundInternalEvent(ItemType.SCRAP, data.getCurrentZone().getCoordsOfActor(attacker), 1));
		}
		
		String weaponEffect = getDescriptionOfCondition(weaponDestroyed, attackingWeapon.getConditionModifer(), weaponConditionBeforeAttack);
		
		if (weaponEffect != null)
			MessageBuffer.addMessage(new FormattedMessageBuilder("@1his " + attackingWeapon.getName() + " is " + weaponEffect).setSource(attacker).setSourceVisibility(canSeeSource).setTargetVisibility(canSeeTarget).format());
	}

	private boolean defenderCanParry(Actor attacker, Actor defender)
	{
		if (defender.getShields().isEmpty())
			return false;
		
		if (!canDefenderSeeAttacker(defender, attacker))
			return false;
		
		if (FacingCalculator.getInstance().isActorBehindTarget(attacker, defender))
			return false;
		
		if (FacingCalculator.getInstance().isActorDiagonallyBehindTarget(attacker, defender))
			return false;
		
		if (FacingCalculator.getInstance().isActorBesideTarget(attacker, defender))
			return false;
		
		return true;
	}

	private boolean defenderGetsBenefitOfArmor(Actor attacker, Actor defender)
	{
		if (defender.getArmor().isEmpty())
			return false;
		
		if (!canDefenderSeeAttacker(defender, attacker))
			return false;
		
		return true;
	}
	
	private boolean canDefenderSeeAttacker(Actor defender, Actor attacker)
	{
		Zone currentZone = data.getCurrentZone();
		AwarenessStatus status = new AwarenessStatus(currentZone, defender, currentZone.getCoordsOfActor(attacker));
		return status.isTargetActorVisible();
	}
}
