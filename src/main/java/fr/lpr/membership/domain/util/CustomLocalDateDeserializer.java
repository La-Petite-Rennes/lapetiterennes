package fr.lpr.membership.domain.util;

import java.io.IOException;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CustomLocalDateDeserializer extends JsonDeserializer<LocalDate> {

	private static DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");

	@Override
	public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		final JsonToken t = jp.getCurrentToken();
		if (t == JsonToken.VALUE_STRING) {
			final String str = jp.getText().trim();
			return formatter.parseLocalDate(str);
		}
		if (t == JsonToken.VALUE_NUMBER_INT) {
			return new LocalDate(jp.getLongValue());
		}
		throw ctxt.mappingException(handledType());
	}
}
