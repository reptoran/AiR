package main.logic.combat;

import java.util.List;

import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.item.InventorySelectionKey;
import main.entity.item.Item;
import main.entity.item.ItemSource;
import main.entity.item.ItemType;
import main.logic.RPGlib;
import main.presentation.message.FormattedMessageBuilder;
import main.presentation.message.MessageBuffer;

public class ClassicCombatAttackCalculator extends AbstractCombatAttackCalculator
{
	private static ClassicCombatAttackCalculator instance = null;
	
	private ClassicCombatAttackCalculator() {}
	
	public static ClassicCombatAttackCalculator getInstance()
	{
		if (instance == null)
			instance = new ClassicCombatAttackCalculator();
		
		return instance;
	}
	
	@Override
	public void handleAttack(Actor attacker, Item attackingWeapon, Actor defender)
	{
		Actor player = data.getPlayer();
		if (attacker == player)
			data.setTarget(defender);
		if (defender == player)
			data.setTarget(attacker);
		
		boolean canSeeSource = engine.canPlayerSeeActor(attacker);
		boolean canSeeTarget = engine.canPlayerSeeActor(defender);
		
		AttackDetails attackDetails = new AttackDetails();
		
		int damageToDefender = calculateAttackAndReturnDamageToDefender(attacker, defender, attackingWeapon, canSeeSource, canSeeTarget, attackDetails);
		String attackMessage = "";
		String armorDestroyedMessage = attackDetails.getArmorDestroyedMessage();
		String weaponDestroyedMessage = attackDetails.getWeaponDestroyedMessage();
		Item armor = attackDetails.getArmorThatAbsorbedAttack();
		
		//TODO: right now an action cost of 0 is being set on the event because it's not used anyway - a constant duration is returned regardless of attack count
		if (armor != null && armor.isUpgraded())
		{
			attackMessage = "@1thes attack glances off a makeshift plate on @2thes armor, sending it flying.";
			sendEventToObservers(InternalEvent.waitInternalEvent(data.getActorIndex(attacker), 0));
			sendEventToObservers(InternalEvent.downgradeItemInternalEvent(data.getActorIndex(defender), new InventorySelectionKey(ItemSource.EQUIPMENT, defender.getIndexOfEquippedItem(armor))));
		}
		else if (damageToDefender < 0)
		{
			attackMessage = "@2the deflect%2s @1thes attack.";
			sendEventToObservers(InternalEvent.waitInternalEvent(data.getActorIndex(attacker), 0));
		}
		else if (damageToDefender == 0)
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
		
		MessageBuffer.addMessage(new FormattedMessageBuilder(attackMessage).setSource(attacker).setTarget(defender).setSourceVisibility(canSeeSource).setTargetVisibility(canSeeTarget).format());
		
		if (armorDestroyedMessage != null)
			MessageBuffer.addMessage(armorDestroyedMessage);
		
		if (weaponDestroyedMessage != null)
			MessageBuffer.addMessage(weaponDestroyedMessage);
		
		if (defender.getCurHp() <= 0)	//valid check because the data layer has already received and applied the damage
		{
			if (canSeeTarget)
				MessageBuffer.addMessage(new FormattedMessageBuilder("@1the is killed!").setSource(defender).setSourceVisibility(canSeeTarget).format());
			data.receiveInternalEvent(InternalEvent.deathInternalEvent(data.getActorIndex(defender)));
			
			awardKillExperience(attacker, defender);
		}
		else
		{
			updateDefenderToFaceAttacker(attacker, defender);	//TODO: this might not happen if the attack comes from an unseen source (even backstabbing)
		}
	}

	//TODO: right now, there's only one armor piece (maximum), but later, with multiple pieces of armor, something might determine which piece gets hit
	// 		this is because armor takes damage from attacks, and it seems foolish to damage all pieces of armor with each attack
	private Item getArmorForAttackedBodyPart(Actor defender)
	{
		List<Item> defenderArmor = defender.getArmor();
		return defenderArmor.get(RPGlib.randInt(0, defenderArmor.size() - 1));
	}
	
	private Item getShield(Actor defender)
	{
		List<Item> defenderShields = defender.getShields();
		
		if (defenderShields.isEmpty())
			return null;
		
		Item shield = defenderShields.get(RPGlib.randInt(0, defenderShields.size() - 1));
		
		if (RPGlib.percentage(shield.getCR()))
			return null;
		
		return shield;
	}

	//TODO: remember to adjust effective AR based on condition
	//	note that the armor still takes damage as though it had full AR, provided there's enough damage for it
	//		attack of 1 against armor with 2/4 AR deals 1 to armor and 0 to defender
	//		attack of 2 against armor with 2/4 AR deals 2 to armor and 0 to defender
	//		attack of 3 against armor with 2/4 AR deals 3 to armor and 1 to defender
	//		attack of 4 against armor with 2/4 AR deals 4 to armor and 2 to defender
	//		attack of 5 against armor with 2/4 AR deals 4 to armor and 3 to defender
	private int calculateAttackAndReturnDamageToDefender(Actor attacker, Actor defender, Item weapon, boolean canSeeSource, boolean canSeeTarget, AttackDetails attackDetails)
	{
		boolean attackDeflected = false;
		String damageString = weapon.getDamage();
		Item shield = getShield(defender);
		Item armor;
		
		if (shield != null)		//TODO: at present, shields ALWAYS block all damage, making them an armor upgrade on steroids
		{
			attackDeflected = true;
			armor = shield;
		}
		else
		{
			armor = getArmorForAttackedBodyPart(defender);
		}
		
		attackDetails.setArmorThatAbsorbedAttack(armor);
				
		int rawDamage = RPGlib.roll(damageString);
		int damageToArmor = 0;
		int damageToWeapon = 0;
		int damageToDefender = 0;
		
		//TODO: maybe this is considered a critical hit?
		if (RPGlib.percentage(armor.getCR()))
			return rawDamage;
		
		double armorConditionBeforeAttack = armor.getConditionModifer();
		double weaponConditionBeforeAttack = weapon.getConditionModifer();
		
		damageToArmor = rawDamage > armor.getAR() ? armor.getAR() : rawDamage;
//		damageToWeapon = damageToArmor - weapon.getDR();
		damageToWeapon = damageToArmor - getItemDR(weapon, attacker);
		damageToDefender = rawDamage - damageToArmor;
//		damageToArmor -= armor.getDR();
		damageToArmor -= getItemDR(armor, defender);
		
		if (damageToArmor < 0 || armor.isUpgraded())
			damageToArmor = 0;
		
		if (damageToWeapon < 0 || weapon.isUpgraded())
			damageToWeapon = 0;
		
		if (damageToArmor > armor.getCurHp())
		{
			damageToDefender += (damageToArmor - armor.getCurHp());
			damageToArmor = armor.getCurHp();
		}
		
		if (damageToWeapon > weapon.getCurHp())
			damageToWeapon = weapon.getCurHp();
		
		if (damageToArmor > 0)
		{
			sendEventToObservers(InternalEvent.changeInventoryItemHpInternalEvent(data.getActorIndex(defender), new InventorySelectionKey(ItemSource.EQUIPMENT, defender.getIndexOfEquippedItem(armor)), damageToArmor * -1));
			
			boolean armorDestroyed = (armor.getCurHp() <= 0 ? true : false);
			
			if (armorDestroyed)
			{
				sendEventToObservers(InternalEvent.deleteHeldItemInternalEvent(data.getActorIndex(defender), defender.getIndexOfEquippedItem(armor), 1));
				sendEventToObservers(InternalEvent.createItemOnGroundInternalEvent(ItemType.METAL_SHARD, data.getCurrentZone().getCoordsOfActor(defender), 1));
			}
			
			String effect = getDescriptionOfCondition(armorDestroyed, armor.getConditionModifer(), armorConditionBeforeAttack);
			
			if (effect != null)
				attackDetails.setArmorDestroyedMessage(new FormattedMessageBuilder("@2his " + armor.getName() + " is " + effect).setTarget(defender).setSourceVisibility(canSeeSource).setTargetVisibility(canSeeTarget).format());
		}
		
		if (damageToWeapon > 0)
		{
			sendEventToObservers(InternalEvent.changeInventoryItemHpInternalEvent(data.getActorIndex(attacker), new InventorySelectionKey(ItemSource.EQUIPMENT, attacker.getIndexOfEquippedItem(weapon)), damageToWeapon * -1));
			
			boolean weaponDestroyed = (weapon.getCurHp() <= 0 ? true : false);
			
			if (weaponDestroyed)
			{
				sendEventToObservers(InternalEvent.deleteHeldItemInternalEvent(data.getActorIndex(attacker), attacker.getIndexOfEquippedItem(weapon), 1));
				sendEventToObservers(InternalEvent.createItemOnGroundInternalEvent(ItemType.METAL_SHARD, data.getCurrentZone().getCoordsOfActor(attacker), 1));
			}
			
			String effect = getDescriptionOfCondition(weaponDestroyed, weapon.getConditionModifer(), weaponConditionBeforeAttack);
			
			if (effect != null)
				attackDetails.setWeaponDestroyedMessage(new FormattedMessageBuilder("@1his " + weapon.getName() + " is " + effect).setSource(attacker).setSourceVisibility(canSeeSource).setTargetVisibility(canSeeTarget).format());
		}
		
		if (attackDeflected)
			return -1;
			
		return damageToDefender;
	}
}
