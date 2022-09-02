package main.data.file;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

public abstract class JsonFileUtils<T>
{
	private static final Logger LOGGER = LogManager.getLogger(JsonFileUtils.class);
	
	private ObjectMapper objectMapper = new ObjectMapper();

	public List<T> loadFromFile(File inputFile)
	{
		List<T> elements = null;

		try
		{
			elements = jsonArrayToList(inputFile);
		} catch (JsonParseException e)
		{
			LOGGER.error("JsonParseException thrown while reading file " + inputFile.getName() + "; exception was " + e.getMessage());
		} catch (JsonMappingException e)
		{
			LOGGER.error("JsonMappingException thrown while reading file " + inputFile.getName() + "; exception was " + e.getMessage());
		} catch (IOException e)
		{
			LOGGER.error("IOException thrown while reading file " + inputFile.getName() + "; exception was " + e.getMessage());
		}

		return elements;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<T> jsonArrayToList(File inputFile) throws IOException
	{
		Class<T> elementClass = ((Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
		CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, elementClass);
		
		return objectMapper.readValue(inputFile, listType);
	}

	public boolean saveToFile(List<?> elements, File outputFile)
	{
		try
		{
			objectMapper.writeValue(outputFile, elements);
		} catch (JsonGenerationException e)
		{
			LOGGER.error("JsonGenerationException thrown while writing file " + outputFile.getName() + "; exception was " + e.getMessage());
			return false;
		} catch (JsonMappingException e)
		{
			LOGGER.error("JsonMappingException thrown while writing file " + outputFile.getName() + "; exception was " + e.getMessage());
			return false;
		} catch (IOException e)
		{
			LOGGER.error("IOException thrown while writing file " + outputFile.getName() + "; exception was " + e.getMessage());
			return false;
		}

		return true;
	}
}
