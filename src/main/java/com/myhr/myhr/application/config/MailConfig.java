package com.myhr.myhr.application.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender(Environment env) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();

        String host = env.getProperty("spring.mail.host", "smtp.gmail.com");
        int port = Integer.parseInt(env.getProperty("spring.mail.port", "587"));


        String username = env.getProperty(
                "spring.mail.username",
                System.getenv().getOrDefault("GMAIL_USER", "")
        );
        String password = env.getProperty(
                "spring.mail.password",
                System.getenv().getOrDefault("GMAIL_APP_PASSWORD", "")
        );

        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(password);
        sender.setDefaultEncoding(StandardCharsets.UTF_8.name());


        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", env.getProperty("spring.mail.properties.mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", env.getProperty("spring.mail.properties.mail.smtp.starttls.enable", "true"));

        props.put("mail.smtp.starttls.required", env.getProperty("spring.mail.properties.mail.smtp.starttls.required", "true"));


        props.put("mail.smtp.connectiontimeout", env.getProperty("spring.mail.properties.mail.smtp.connectiontimeout", "10000"));
        props.put("mail.smtp.timeout",          env.getProperty("spring.mail.properties.mail.smtp.timeout",          "10000"));
        props.put("mail.smtp.writetimeout",     env.getProperty("spring.mail.properties.mail.smtp.writetimeout",     "10000"));


        props.put("mail.smtp.ssl.trust", env.getProperty("spring.mail.properties.mail.smtp.ssl.trust", "smtp.gmail.com"));


        props.put("mail.debug", env.getProperty("spring.mail.properties.mail.debug", "false"));

        return sender;
    }
}
