package fr.lpr.membership.domain.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

	private static DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");

	@Override
	public LocalDate unmarshal(String date) throws Exception {
		return formatter.parseLocalDate(date);
	}

	@Override
	public String marshal(LocalDate date) throws Exception {
		return formatter.print(date);
	}

}