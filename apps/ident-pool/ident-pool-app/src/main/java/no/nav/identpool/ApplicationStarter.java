package no.nav.identpool;

import org.springframework.boot.SpringApplication;

import no.nav.identpool.config.ApplicationConfig;
import no.nav.identpool.config.ScheduleConfig;

public class ApplicationStarter {
    public static void main(String[] arguments) {
        Class<?>[] configClass = new Class[] { ApplicationConfig.class, ScheduleConfig.class };
        SpringApplication.run(configClass, arguments);
    }
}
