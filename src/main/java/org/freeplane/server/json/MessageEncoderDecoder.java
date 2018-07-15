package org.freeplane.server.json;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.freeplane.server.genericmessages.ImmutableGenericMapUpdateRequested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

@Component
public class MessageEncoderDecoder {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@PostConstruct
	public void init(){
		objectMapper.registerSubtypes(new NamedType(ImmutableGenericMapUpdateRequested.class, "MapUpdateRequested"));
	}

	public <T> T deserialize(String text, Class<T> targetClass)
	{
		try {
			return objectMapper.readValue(text, targetClass);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String serialize(Object object)
	{
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}
