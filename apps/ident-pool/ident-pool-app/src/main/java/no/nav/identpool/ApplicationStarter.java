package no.nav.identpool;

import org.springframework.boot.SpringApplication;

public class ApplicationStarter {
    public static void main(String[] arguments) {
        Class<?>[] configClass = new Class[] { ApplicationConfig.class, ScheduleConfig.class };
        SpringApplication.run(configClass, arguments);
    }
}
