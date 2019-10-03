package fr.lpr.membership.web.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ExportRequest {

	private String format;

	private AdhesionState adhesionState;

	private Map<String, Boolean> properties;

	public enum AdhesionState {
		all, expired, expiring, valid, recently_expired
	}
}
