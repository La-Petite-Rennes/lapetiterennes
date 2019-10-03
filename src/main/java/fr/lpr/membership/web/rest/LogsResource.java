package fr.lpr.membership.web.rest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.codahale.metrics.annotation.Timed;
import fr.lpr.membership.web.rest.dto.LoggerDTO;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for view and managing Log Level at runtime.
 */
@RestController
@RequestMapping("/api/logs")
public class LogsResource {

	@GetMapping
	@Timed
	public List<LoggerDTO> getList() {
		final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		return context.getLoggerList().stream().map(LoggerDTO::new).collect(Collectors.toList());

	}

	@PutMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Timed
	public void changeLevel(@RequestBody LoggerDTO jsonLogger) {
		final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		context.getLogger(jsonLogger.getName()).setLevel(Level.valueOf(jsonLogger.getLevel()));
	}
}
