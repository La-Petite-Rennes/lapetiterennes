package fr.lpr.membership.config.apidoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;

import fr.lpr.membership.config.Constants;

/**
 * Swagger configuration.
 *
 * Warning! When having a lot of REST endpoints, Swagger can become a performance issue. In that case, you can use a specific Spring profile for this class, so
 * that only front-end developers have access to the Swagger view.
 */
@Configuration
@EnableSwagger
@Profile("!" + Constants.SPRING_PROFILE_FAST)
public class SwaggerConfiguration implements EnvironmentAware {

	private final Logger log = LoggerFactory.getLogger(SwaggerConfiguration.class);

	public static final String DEFAULT_INCLUDE_PATTERN = "/api/.*";

	private RelaxedPropertyResolver propertyResolver;

	@Override
	public void setEnvironment(Environment environment) {
		this.propertyResolver = new RelaxedPropertyResolver(environment, "swagger.");
	}

	/**
	 * Swagger Spring MVC configuration.
	 * 
	 * @param springSwaggerConfig
	 *            the swagger config
	 * @return the Swagger Spring MVC plugin
	 */
	@Bean
	public SwaggerSpringMvcPlugin swaggerSpringMvcPlugin(SpringSwaggerConfig springSwaggerConfig) {
		log.debug("Starting Swagger");
		final StopWatch watch = new StopWatch();
		watch.start();
		final SwaggerSpringMvcPlugin swaggerSpringMvcPlugin = new SwaggerSpringMvcPlugin(springSwaggerConfig).apiInfo(apiInfo())
				.genericModelSubstitutes(ResponseEntity.class).includePatterns(DEFAULT_INCLUDE_PATTERN);

		swaggerSpringMvcPlugin.build();
		watch.stop();
		log.debug("Started Swagger in {} ms", watch.getTotalTimeMillis());
		return swaggerSpringMvcPlugin;
	}

	/**
	 * API Info as it appears on the swagger-ui page.
	 * 
	 * @return API info
	 */
	private ApiInfo apiInfo() {
		return new ApiInfo(propertyResolver.getProperty("title"), propertyResolver.getProperty("description"),
				propertyResolver.getProperty("termsOfServiceUrl"), propertyResolver.getProperty("contact"), propertyResolver.getProperty("license"),
				propertyResolver.getProperty("licenseUrl"));
	}
}
