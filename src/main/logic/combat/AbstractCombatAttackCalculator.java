package main.logic.combat;

import main.data.Data;
import main.data.PlayerAdvancementManager;
import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.actor.ActorTrait;
import main.entity.item.Item;
import main.logic.Direction;
import main.logic.Engine;
import main.logic.AI.FacingUpdater;
import main.presentation.Logger;

public abstract class AbstractCombatAttackCalculator
{
	protected Data data = null;
	protected Engine engine = null;
	
	public void initialize(Data newData, Engine newEngine)
	{
		this.data = newData;
		this.engine = newEngine;
	}
	
	public abstract void handleAttack(Actor attacker, Item attackingWeapon, Actor defender);
	
	protected void sendEventToObservers(InternalEvent event)
	{
		engine.sendEventToObservers(event);
	}

	protected void updateDefenderToFaceAttacker(Actor attacker, Actor defender)
	{
		Direction newFacing = FacingUpdater.getInstance().getDirectionOfTargetActor(data.getCurrentZone(), defender, attacker);
		
		if (defender.getFacing() != newFacing)
			sendEventToObservers(InternalEvent.changeActorFacingEvent(data.getActorIndex(defender), newFacing));
	}

	protected void awardKillExperience(Actor attacker, Actor defender)
	{
		if (data.getPlayer() != attacker)
			return;
		
		int xpToAward = defender.getType().getMinDepth();
		
		if (xpToAward <= 0)
			xpToAward = 1;
		
		PlayerAdvancementManager.getInstance().gainXP(xpToAward);
	}
	
	protected String getDescriptionOfCondition(boolean destroyed, double currentCondition, double conditionBeforeAttack)
	{
		if (destroyed)
			return "destroyed!";
		else if (currentCondition <= .25 && conditionBeforeAttack > .25)
			return "heavily damaged.";	//"cracked.";
		else if (currentCondition <= .5 && conditionBeforeAttack > .5)
			return "moderately damaged.";	//"warped.";
		else if (currentCondition <= .75 && conditionBeforeAttack > .75)
			return "slightly damaged.";	//"dented.";
		
		return null;
	}
	
	protected int getItemDR(Item item, Actor itemOwner)
	{
		int DR = item.getDR();
		
		if (itemOwner.hasTrait(ActorTrait.DURABLE_EQ))
			DR = (int)(item.getDR() * 1.5);
		
		Logger.debug("Actor " + itemOwner.getName() + " is wielding " + item.getName() + "; base DR is " + item.getDR() + " and effective DR is " + DR + ".");
		
		return DR;
	}
	
	protected class AttackDetails
	{
		private String armorDestroyedMessage = null;
		private String weaponDestroyedMessage = null;
		private Item armorThatAbsorbedAttack = null;
		
		public AttackDetails() {}

		public String getArmorDestroyedMessage()
		{
			return armorDestroyedMessage;
		}

		public void setArmorDestroyedMessage(String armorDestroyedMessage)
		{
			this.armorDestroyedMessage = armorDestroyedMessage;
		}

		public String getWeaponDestroyedMessage()
		{
			return weaponDestroyedMessage;
		}

		public void setWeaponDestroyedMessage(String weaponDestroyedMessage)
		{
			this.weaponDestroyedMessage = weaponDestroyedMessage;
		}
		
		public Item getArmorThatAbsorbedAttack()
		{
			return armorThatAbsorbedAttack;
		}
		
		public void setArmorThatAbsorbedAttack(Item armor)
		{
			armorThatAbsorbedAttack = armor;
		}
	}
}
