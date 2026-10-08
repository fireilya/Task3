package ru.Perminov_Ilya.NauJava.AppConfiguration;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainConfig {
    @Value("${app.name}")
    private String appName;
    @Value("${app.version}")
    private String appVersion;

    @PostConstruct
    private void tellAboutYourself()
    {
        System.out.println("Initializing...");
        System.out.println("App " + appName + " version " + appVersion + " ready for work");
    }
}
