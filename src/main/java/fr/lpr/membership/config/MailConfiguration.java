package fr.lpr.membership.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@Slf4j
public class MailConfiguration implements EnvironmentAware {

    private static final String ENV_SPRING_MAIL = "mail.";
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final String PROP_HOST = ENV_SPRING_MAIL + "host";
    private static final String DEFAULT_PROP_HOST = "localhost";
    private static final String PROP_PORT = ENV_SPRING_MAIL + "port";
    private static final String PROP_USER = ENV_SPRING_MAIL + "username";
    private static final String PROP_PASSWORD = ENV_SPRING_MAIL + "password";
    private static final String PROP_PROTO = ENV_SPRING_MAIL + "protocol";
    private static final String PROP_TLS = ENV_SPRING_MAIL + "tls";
    private static final String PROP_AUTH = ENV_SPRING_MAIL + "auth";
    private static final String PROP_SMTP_AUTH = "mail.smtp.auth";
    private static final String PROP_STARTTLS = "mail.smtp.starttls.enable";
    private static final String PROP_TRANSPORT_PROTO = "mail.transport.protocol";

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public JavaMailSenderImpl javaMailSender() {
        log.debug("Configuring mail server");
        String host = environment.getProperty(PROP_HOST, DEFAULT_PROP_HOST);
        int port = environment.getProperty(PROP_PORT, Integer.class, 0);
        String user = environment.getProperty(PROP_USER);
        String password = environment.getProperty(PROP_PASSWORD);
        String protocol = environment.getProperty(PROP_PROTO);
        Boolean tls = environment.getProperty(PROP_TLS, Boolean.class, false);
        Boolean auth = environment.getProperty(PROP_AUTH, Boolean.class, false);

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        if (!host.isEmpty()) {
            sender.setHost(host);
        } else {
            log.warn("Warning! Your SMTP server is not configured. We will try to use one on localhost.");
            log.debug("Did you configure your SMTP settings in your application.yml?");
            sender.setHost(DEFAULT_HOST);
        }
        sender.setPort(port);
        sender.setUsername(user);
        sender.setPassword(password);

        Properties sendProperties = new Properties();
        sendProperties.setProperty(PROP_SMTP_AUTH, auth.toString());
        sendProperties.setProperty(PROP_STARTTLS, tls.toString());
        sendProperties.setProperty(PROP_TRANSPORT_PROTO, protocol);
        sender.setJavaMailProperties(sendProperties);
        return sender;
    }
}
