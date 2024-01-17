package main.data;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import main.data.event.InternalEvent;
import main.entity.actor.Actor;
import main.entity.actor.ActorFactory;
import main.entity.actor.ActorType;
import main.entity.actor.SkillType;

public class PlayerAdvancementManager implements SaveableDataManager
{
	private int experience;
	private int characterLevel;
	private int skillPoints;
	private Set<SkillType> classSkills;
	
	private static PlayerAdvancementManager instance = null;
	
	private PlayerAdvancementManager()
	{
		experience = 0;
		characterLevel = 1;
		skillPoints = 0;
		classSkills = new HashSet<SkillType>();
	}
	
	public static PlayerAdvancementManager getInstance()
	{
		if (instance == null)
			instance = new PlayerAdvancementManager();
		
		return instance;
	}
	
	public void startNewPlayerAdvancement(ActorType actor)
	{
		Actor defaultActor = ActorFactory.generateNewActor(actor);
		Set<SkillType> actorSkills = defaultActor.getSkills();
		classSkills.clear();
		
		for (SkillType skill : actorSkills)
		{
			classSkills.add(skill);
		}
	}
	
	public int getCostToTrainSkill(Actor actor, SkillType skill)
	{
		if (!actor.isPlayer())
			return -1;
		
		if (!actor.hasSkill(skill, 1))
			return -1;
		
		int advancementCost = actor.getSkillLevel(skill);
		
		if (classSkills.contains(skill))
			advancementCost -= 1;
		
		return advancementCost;
	}
	
	public boolean trainSkill(Actor actor, SkillType skill)
	{
		int cost = getCostToTrainSkill(actor, skill);
		
		if (cost < 0)
			return false;
		
		if (cost >= skillPoints)
			return false;
		
		skillPoints -= cost;
		actor.gainSkillLevel(skill);
		return true;
	}
	
	public void gainXP(int amount)
	{
		int threshold = levelThreshold();
		experience += amount;
		
		if (experience >= threshold)
		{
			characterLevel++;
			skillPoints++;
			experience -= threshold;
			
			DataAccessor.getInstance().sendInternalEvent(InternalEvent.advancePlayerLevelInternalEvent(characterLevel));
		}
	}
	
	public String getXpString()
	{
		return experience + "/" + levelThreshold();
	}
	
	public int getExperience()
	{
		return experience;
	}
	
	public int getCharacterLevel()
	{
		return characterLevel;
	}
	
	public int getSkillPoints()
	{
		return skillPoints;
	}
	
	private int levelThreshold()
	{
		return 100 * characterLevel;
	}
	
	@Override
	public String saveState()
	{
		String saveString = String.valueOf(experience) + DELIMITER
				+ String.valueOf(characterLevel) + DELIMITER
				+ String.valueOf(skillPoints) + DELIMITER;
		
		for (SkillType classSkill : classSkills)
		{
			saveString = saveString + classSkill.name() + DELIMITER;
		}
		
		return saveString.substring(0, saveString.length() - 1);
	}

	@Override
	public void loadState(String saveString)
	{
		classSkills.clear();
		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(saveString).useDelimiter(DELIMITER);
		
		experience = Integer.parseInt(scanner.next());
		characterLevel = Integer.parseInt(scanner.next());
		skillPoints = Integer.parseInt(scanner.next());
		
		while (scanner.hasNext())
		{
			classSkills.add(SkillType.fromString(scanner.next()));
		}
		
		scanner.close();
	}

}
