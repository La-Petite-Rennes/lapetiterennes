package fr.lpr.membership.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Persistent tokens are used by Spring Security to automatically log in users.
 *
 * @see fr.lpr.membership.security.CustomPersistentRememberMeServices
 */
@Entity
@Table(name = "JHI_PERSISTENT_TOKEN")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
public class PersistentToken implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy");

	private static final int MAX_USER_AGENT_LEN = 255;

	@Id
	private String series;

	@JsonIgnore
	@NotNull
	@Column(nullable = false)
	private String tokenValue;

	@JsonIgnore
	private LocalDate tokenDate;

	// an IPV6 address max length is 39 characters
	@Size(min = 0, max = 39)
	@Column(length = 39)
	private String ipAddress;

	private String userAgent;

	@JsonIgnore
	@ManyToOne
	private User user;

	@JsonGetter
	public String getFormattedTokenDate() {
		return DATE_TIME_FORMATTER.format(this.tokenDate);
	}

	public void setUserAgent(String userAgent) {
		if (userAgent.length() >= MAX_USER_AGENT_LEN) {
			this.userAgent = userAgent.substring(0, MAX_USER_AGENT_LEN - 1);
		} else {
			this.userAgent = userAgent;
		}
	}
}
