package no.nav.dolly.properties;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "providers")
public class ProvidersProps {

    private Tpsf tpsf = new Tpsf();
    private Sigrun sigrun = new Sigrun();
    private Kodeverk kodeverk = new Kodeverk();

    @Getter
    @Setter
    public class Tpsf {
        private String url;
    }

    @Getter
    @Setter
    public class Sigrun {
        String url;
    }

    @Getter
    @Setter
    public class Kodeverk {
        String url;
    }
}
