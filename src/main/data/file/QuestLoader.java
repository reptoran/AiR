package main.data.file;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import main.entity.CompareOperator;
import main.entity.actor.ActorType;
import main.entity.event.TriggerFactory;
import main.entity.item.ItemType;
import main.entity.quest.Quest;
import main.entity.quest.QuestBuilder;
import main.entity.quest.QuestJsonFileUtils;
import main.entity.quest.QuestNode;
import main.entity.quest.QuestNodeBuilder;
import main.entity.quest.QuestNodeStatus;
import main.entity.requirement.RequirementFactory;
import main.entity.tile.TileType;

public class QuestLoader extends FileHandler
{
private static QuestLoader instance = null;
	
	private QuestLoader() {}
	
	public static QuestLoader getInstance()
	{
		if (instance == null)
			instance = new QuestLoader();
		
		return instance;
	}
	
	public List<Quest> loadAllQuests()
	{
		List<Quest> loadedQuests = new ArrayList<Quest>();
		
		//TODO: unzip a .dat file that's just an archive of everything, then search the created folder
		
		File folder = new File(getDataPath());

		for (File file : folder.listFiles())
		{
			if (!getFileExtension(file).equals(getExtension()))
				continue;
			
			List<Quest> questsInFile = QuestJsonFileUtils.getInstance().loadFromFile(file);
			loadedQuests.addAll(questsInFile);
		}
		
		return loadedQuests;
	}
	
	public List<Quest> defineQuests()
	{
		//Something to remember when defining quest nodes: A node with NO objectives can only be completed by an external trigger, like a chat event or 
		//another quest node.  A node WITH objectives can self-complete immediately after it's activated, since its requirements are checked upon activation.
		//Note that nodes can also be completed externally even if their requirements aren't met.
		
		List<Quest> quests = new ArrayList<Quest>();
		
		Quest fungusFinder = new QuestBuilder().setName("Fungus Finder").setTag("FUNGUS").build();
		//TODO: this start should actually be triggered by a certain conversation element (and can't be completed any other way)
		//		but there should also be another one that enables the conversation path to begin with
		//		node0 - made active by completing the "what can you tell me about the rifts?" chat with the elder
		//				activates node1
		//		node1 = made active by completing either branch of the "do you know where i can find some fungus" chat with the elder
		//				similar to "fungusStart" below, but it also makes the quest known and doesn't begin as active
		QuestNode fungusStart = new QuestNodeBuilder().setTag("START").setDescription("activating quest")
				.addTrigger(TriggerFactory.setQuestNodeStatus("FUNGUS", "BASEMENT", QuestNodeStatus.COMPLETE))
				.build();
		QuestNode fungusCreateBasement = new QuestNodeBuilder().setTag("BASEMENT").setDescription("generating stairs to basement")
				.addTrigger(TriggerFactory.setZoneTile("TOWN", new Point(6, 32), TileType.STAIRS_DOWN))
				.addTrigger(TriggerFactory.addZoneKey("TOWN", new Point(6, 32), "TH_BASEMENT"))
				.addTrigger(TriggerFactory.setQuestNodeStatus("FUNGUS", "DESCEND", QuestNodeStatus.ACTIVE))
				.build();
		QuestNode fungusDescend = new QuestNodeBuilder().setTag("DESCEND").setDescription("Enter the town hall basement.")
				.setObjective(RequirementFactory.playerEntersZone("TH_BASEMENT"))
				.addTrigger(TriggerFactory.setQuestNodeStatus("FUNGUS", "COLLECT", QuestNodeStatus.ACTIVE))
				.build();
		QuestNode fungusCollect = new QuestNodeBuilder().setTag("COLLECT").setDescription("Collect some medicinal fungi.")
				.setObjective(RequirementFactory.actorHasItem(ActorType.PLAYER, ItemType.HERB, CompareOperator.GREATER_THAN, 0))
				.addTrigger(TriggerFactory.setQuestNodeStatus("FUNGUS", "RETURN", QuestNodeStatus.ACTIVE))
				.build();
		QuestNode fungusReturn = new QuestNodeBuilder().setTag("RETURN").setDescription("Return to the surface.")
				.setObjective(RequirementFactory.playerEntersZone("TOWN"))
				.addTrigger(TriggerFactory.completeQuest("FUNGUS"))
				.build();
		fungusFinder.addStartNode(fungusStart);
		fungusFinder.addNode(fungusCreateBasement);
		fungusFinder.addNode(fungusDescend);
		fungusFinder.addNode(fungusCollect);
		fungusFinder.addNode(fungusReturn);
		quests.add(fungusFinder);
		
		Quest riskyResearch = new QuestBuilder().setName("Risky Research").setTag("RISKYR").build();
		QuestNode researchStart = new QuestNodeBuilder().setTag("START").setDescription("activating quest")
				.addTrigger(TriggerFactory.setQuestNodeStatus("RISKYR", "CLEAR_GOBLINS", QuestNodeStatus.ACTIVE))
				.addTrigger(TriggerFactory.setQuestNodeStatus("RISKYR", "CLEAR_GOBLIN_CARVERS", QuestNodeStatus.ACTIVE))
				.build();
		
		//TODO: I don't think these will have any explicit triggers; rather, the archeo's dialogue will simply update based on the nodes being completed,
		//		and THAT'S what will kick off the next node.  It would be nice to have a "return to the archeo" node, but since nodes don't have requirements,
		//		only triggers, I think that's impossible.
		QuestNode researchClearLevelOfGoblins = new QuestNodeBuilder().setTag("CLEAR_GOBLINS").setDescription("Kill all goblins in the area.")
				.setObjective(RequirementFactory.actorCountInZone("TOWN", ActorType.HUMAN, CompareOperator.EQUAL, 0))
				.build();
		QuestNode researchClearLevelOfGoblinCarvers = new QuestNodeBuilder().setTag("CLEAR_GOBLIN_CARVERS").setDescription("Kill all goblin carvers in the area.")
				.setObjective(RequirementFactory.actorCountInZone("TOWN", ActorType.GOBLIN_CARVER, CompareOperator.EQUAL, 0))	//UNDER_GAL
				.build();
		QuestNode researchElder = new QuestNodeBuilder().setTag("ELDER").setDescription("Ask the village elder if she knows anything about an ancient race.")
				.build();
		
		riskyResearch.addStartNode(researchStart);
		riskyResearch.addNode(researchClearLevelOfGoblins);
		riskyResearch.addNode(researchClearLevelOfGoblinCarvers);
		riskyResearch.addNode(researchElder);
		quests.add(riskyResearch);
		
		return quests;
	}
	
	@Override
	protected String getExtension()
	{
		return "qst";
	}

	@Override
	protected String getDataPath()
	{
		return ROOT_PATH + "data" + File.separator + "quests" + File.separator;
	}
}
