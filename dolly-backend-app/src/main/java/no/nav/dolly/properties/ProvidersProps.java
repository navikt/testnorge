package no.nav.dolly.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
    private Instdata instdata = new Instdata();
    private Aaregdata aaregdata = new Aaregdata();
    private Inntektstub inntektstub = new Inntektstub();
    private PdlPerson pdlPerson = new PdlPerson();
    private FasteDatasett fasteDatasett = new FasteDatasett();
    private Pensjonforvalter pensjonforvalter = new Pensjonforvalter();
    private Inntektsmelding inntektsmelding = new Inntektsmelding();
    private IdentPool identPool = new IdentPool();
    private Brregstub brregstub = new Brregstub();
    private Aktoerregister aktoerregister = new Aktoerregister();
    private Dokarkiv dokarkiv = new Dokarkiv();
    private SyntSykemelding syntSykemelding = new SyntSykemelding();
    private SkjermingsRegister skjermingsRegister = new SkjermingsRegister();
    private DetaljertSykemelding detaljertSykemelding = new DetaljertSykemelding();
    private Helsepersonell helsepersonell = new Helsepersonell();
    private Joark joark = new Joark();
    private OrganisasjonForvalter organisasjonForvalter = new OrganisasjonForvalter();

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
    public static class Instdata {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Aaregdata {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Inntektstub {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PdlPerson {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FasteDatasett {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pensjonforvalter {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Inntektsmelding {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdentPool {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Brregstub {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Aktoerregister {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dokarkiv {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SyntSykemelding {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkjermingsRegister {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetaljertSykemelding {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Helsepersonell {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Joark {

        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganisasjonForvalter {

        private String url;
    }
}
