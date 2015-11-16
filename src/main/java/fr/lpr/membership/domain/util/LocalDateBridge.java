package fr.lpr.membership.domain.util;

import org.hibernate.search.bridge.builtin.StringBridge;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LocalDateBridge extends StringBridge {

	private static DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

	@Override
	public Object stringToObject(String stringValue) {
		return FORMATTER.parseLocalDate(stringValue);
	}

	@Override
	public String objectToString(Object object) {
		if (object == null) {
			return null;
		}

		return FORMATTER.print((LocalDate) object);
	}

}