package org.freeplane.server.genericmessages;

import org.freeplane.collaboration.event.messages.Message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "messageType")
@JsonSubTypes({
	@Type(value = ImmutableGenericMapUpdateRequested.class, name = "MapUpdateRequested"),
	@Type(value = ImmutableGenericMapCreateRequested.class, name = "MapCreateRequested"),
	})
public interface GenericMessage extends Message{
}
