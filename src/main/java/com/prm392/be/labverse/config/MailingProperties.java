package com.prm392.be.labverse.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "mailing")
public class MailingProperties {
    private String from;
    private String replyTo;
    private String defaultLocale = "vi";
    private Brand brand = new Brand();

    @Getter @Setter
    public static class Brand {
        private String name;
        private String logoUrl;
        private String primaryColor = "#16a34a";
    }
}