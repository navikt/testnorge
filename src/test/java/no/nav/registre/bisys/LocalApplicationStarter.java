package no.nav.registre.bisys;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

@SpringBootApplication
@ComponentScan(excludeFilters = { @Filter(type = ASSIGNABLE_TYPE, value = ApplicationStarter.class) })
public class LocalApplicationStarter {

    public static final String TEST = "test";

    public static void main(String[] args) {

        String profile = args.length < 1 ? TEST : args[0];

        new SpringApplicationBuilder()
                .sources(LocalApplicationStarter.class)
                .profiles(profile)
                .run(args);
    }
}
