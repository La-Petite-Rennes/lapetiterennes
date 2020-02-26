package fr.lpr.membership.web.rest.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ExportRequest {

	private String format;

	private AdhesionState adhesionState;

	private Map<String, Boolean> properties;

	public enum AdhesionState {
		all, expired, expiring, valid, recently_expired
	}

}
