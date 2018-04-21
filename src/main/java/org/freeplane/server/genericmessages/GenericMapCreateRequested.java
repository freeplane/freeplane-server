package org.freeplane.server.genericmessages;

import org.freeplane.collaboration.event.messages.Credentials;
import org.freeplane.collaboration.event.messages.Event;
import org.freeplane.collaboration.event.messages.MapDescription;
import org.freeplane.collaboration.event.messages.MessageId;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Immutable
@JsonSerialize(as = ImmutableGenericMapCreateRequested.class)
@JsonDeserialize(as = ImmutableGenericMapCreateRequested.class)
public interface GenericMapCreateRequested extends Event, GenericMessage {
	@Override @Default
	default MessageId messageId() {return MessageId.random();}
	@Parameter Credentials credentials();
	@Parameter MapDescription description();
}

