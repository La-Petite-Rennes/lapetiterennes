package fr.lpr.membership.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
public class PersistentToken implements Serializable {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy");

	private static final int MAX_USER_AGENT_LEN = 255;

	@Id
	private String series;

	@JsonIgnore
	@NotNull
	@Column(name = "token_value", nullable = false)
	private String tokenValue;

	@JsonIgnore
	@Column(name = "token_date")
	private LocalDate tokenDate;

	// an IPV6 address max length is 39 characters
	@Size(max = 39)
	@Column(name = "ip_address", length = 39)
	private String ipAddress;

	@Column(name = "user_agent")
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final PersistentToken that = (PersistentToken) o;

        return series.equals(that.series);
    }

	@Override
	public int hashCode() {
		return series.hashCode();
	}

	@Override
	public String toString() {
		return "PersistentToken{" + "series='" + series + '\'' + ", tokenValue='" + tokenValue + '\'' + ", tokenDate=" + tokenDate + ", ipAddress='"
				+ ipAddress + '\'' + ", userAgent='" + userAgent + '\'' + "}";
	}
}
