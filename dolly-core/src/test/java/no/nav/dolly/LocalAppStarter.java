package no.nav.dolly;

public class LocalAppStarter {

    public static void main(String[] args) {

        System.setProperty("spring.profiles.active", "h2");

        ApplicationStarter.main(args);

    }
}
