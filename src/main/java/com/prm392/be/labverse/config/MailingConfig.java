package com.prm392.be.labverse.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MailingProperties.class)
public class MailingConfig {

}