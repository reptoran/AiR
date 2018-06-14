package main.presentation.message;

import main.entity.actor.Actor;
import main.entity.actor.ActorBuilder;
import main.entity.actor.ActorType;
import main.entity.actor.GenderType;

public class FormattedMessageBuilder
{
	private static final String UNSEEN_ACTOR_NAME = "something";
	
	private String unformattedMessage;
	private Actor source = null;
	private Actor target = null;
	
	public FormattedMessageBuilder(String unformattedMessage)
	{
		this.unformattedMessage = unformattedMessage;
	}
	
	public FormattedMessageBuilder setSource(Actor source)
	{
		this.source = source.clone();	//just so that nothing fishy happens to the actor between building this and formatting the message
		return this;
	}
	
	public FormattedMessageBuilder setTarget(Actor target)
	{
		this.target = target.clone();	//as above
		return this;
	}
	
	public FormattedMessageBuilder setSourceVisibility(boolean isVisible)
	{
		if (isVisible)
			return this;
		
		source = ActorBuilder.generateActor(ActorType.NO_TYPE).setName(UNSEEN_ACTOR_NAME).setUnique(true).build();
		return this;
	}
	
	public FormattedMessageBuilder setTargetVisibility(boolean isVisible)
	{
		if (isVisible)
			return this;
		
		target = ActorBuilder.generateActor(ActorType.NO_TYPE).setName(UNSEEN_ACTOR_NAME).setUnique(true).build();
		return this;
	}
	
	public String format()
	{
		if ((source != null && UNSEEN_ACTOR_NAME.equals(source.getName())) && (target != null && UNSEEN_ACTOR_NAME.equals(target.getName())))
			return "";	//don't build a message if neither actor is visible
		
		String formattedMessage = unformattedMessage;
		
		if (source != null)
		{
			formattedMessage = formattedMessage.replace("@1the", formatThe(source));
			formattedMessage = formattedMessage.replace("@1he", formatHe(source));
			formattedMessage = formattedMessage.replace("@1him", formatHim(source));
			formattedMessage = formattedMessage.replace("@1his", formatHis(source));
			formattedMessage = formattedMessage.replace("@1is", formatIs(source));
			formattedMessage = formattedMessage.replace("@1", formatName(source));
		}
		
		if (target != null)
		{
			formattedMessage = formattedMessage.replace("@2the", formatThe(target));
			formattedMessage = formattedMessage.replace("@2he", formatHe(target));
			formattedMessage = formattedMessage.replace("@2him", formatHim(target));
			formattedMessage = formattedMessage.replace("@2his", formatHis(target));
			formattedMessage = formattedMessage.replace("@2is", formatIs(target));
			formattedMessage = formattedMessage.replace("@2", formatName(target));
		}
			
		formattedMessage = formatLetters(formattedMessage);
		return capitalizeFirstCharacter(formattedMessage);
	}
	
	private String formatLetters(String text)
	{
		int startIndex = 0;
		
		while (true)
		{
			startIndex = text.indexOf('%', startIndex);
			if (startIndex == -1)
				break;
			
			String textToReplace = text.substring(startIndex, startIndex + 3);
			char letter = textToReplace.charAt(2);
			char actor = textToReplace.charAt(1);
			String formattedLetter = "";
			
			if (actor == '1')
				formattedLetter = formatLetter(letter, source);
			if (actor == '2')
				formattedLetter = formatLetter(letter, target);
			
			text = text.replace(textToReplace, formattedLetter);
		}
		
		return text;
	}
	
	private String formatLetter(char letter, Actor actor)
	{
		if (actor.getGender() == GenderType.PLAYER)
			return "";
		
		return "" + letter;
	}

	private String formatThe(Actor actor)
	{
		if (actor.getGender() == GenderType.PLAYER)
			return "you";
		
		if (actor.isUnique())
			return actor.getName();
		
		return "the " + actor.getName();
	}
	
	private String formatHe(Actor actor)
	{
		switch (actor.getGender())
		{
		case FEMALE:
			return "she";
		case MALE:
			return "he";
		case NONE:
			return "it";
		case PLAYER:
			return "you";
		default:
			throw new IllegalStateException("Gender not recognized: " + actor.getGender());
		}
	}
	
	private String formatHim(Actor actor)
	{
		switch (actor.getGender())
		{
		case FEMALE:
			return "her";
		case MALE:
			return "him";
		case NONE:
			return "it";
		case PLAYER:
			return "you";
		default:
			throw new IllegalStateException("Gender not recognized: " + actor.getGender());
		}
	}
	
	private String formatHis(Actor actor)
	{
		switch (actor.getGender())
		{
		case FEMALE:
			return "her";
		case MALE:
			return "his";
		case NONE:
			return "its";
		case PLAYER:
			return "your";
		default:
			throw new IllegalStateException("Gender not recognized: " + actor.getGender());
		}
	}
	
	private String formatIs(Actor actor)
	{
		if (actor.getGender() == GenderType.PLAYER)
			return "are";
		
		return "is";
	}
	
	private String formatName(Actor actor)
	{
		if (actor.getGender() == GenderType.PLAYER)
			return "you";
		
		return actor.getName();
	}

	private String capitalizeFirstCharacter(String message)
	{
		String firstChar = "" + message.charAt(0);
		String uppercaseFirstChar = firstChar.toUpperCase();
		return uppercaseFirstChar + message.substring(1);
	}
}
