package no.nav.dolly.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "providers")
public class ProvidersProps {

    private Tpsf tpsf = new Tpsf();
    private SigrunStub sigrunStub = new SigrunStub();
    private KrrStub krrStub = new KrrStub();
    private UdiStub udiStub = new UdiStub();
    private Kodeverk kodeverk = new Kodeverk();
    private Norg2 norg2 = new Norg2();
    private Fasit fasit = new Fasit();
    private ArenaForvalter arenaForvalter = new ArenaForvalter();
    private PdlForvalter pdlForvalter = new PdlForvalter();
    private PersonOppslag personOppslag = new PersonOppslag();
    private Syntdata syntdata = new Syntdata();
    private Instdata instdata = new Instdata();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tpsf {
        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SigrunStub {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KrrStub {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UdiStub {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Kodeverk {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Norg2 {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fasit {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArenaForvalter {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PdlForvalter {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonOppslag {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Syntdata {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Instdata {

        private String url;
    }
}
