package org.freeplane.server.rest.parameters;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize
@JsonDeserialize
interface Update {
	enum ContentType {
		CHILDREN, TEXT, NOTE, DETAILS, ATTRIBUTES, ICONS, OTHER_CONTENT, CLONES
	}
	
	String nodeId();
	ContentType contentType();
	String content();
	
}
