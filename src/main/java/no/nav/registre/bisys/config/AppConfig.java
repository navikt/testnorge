package no.nav.registre.bisys.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.bisys.consumer.rs.BisysSyntetisererenConsumer;
import no.nav.registre.bisys.consumer.rs.request.BidragsmeldingAugments;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;
import no.nav.registre.bisys.consumer.ui.vedtak.BisysUiFatteVedtakConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Configuration
public class AppConfig {

    public static final String STANDARD_DATE_FORMAT_BISYS = "dd.MM.yyyy";
    public static final String STANDARD_DATE_FORMAT_TESTNORGEBISYS_REQUEST = "yyyy-MM-dd";

    // Will be set to true for BMs household (forskudd)
    private final static boolean barnRegistrertPaaAdresse = false;

    @Value("${ANDEL_FORSORGING}")
    private String andelForsorging;

    @Value("${BISYS_URL}")
    private String bisysUrl;

    @Value("${ENHET:4802}")
    private int enhet;

    @Value("${BESLAARSAK_KODE}")
    private String beslaarsakKode;

    @Value("${GEBYR_BESLAARSAK_KODE_FRITATT_IKKE_SOKT}")
    boolean gebyrBeslAarsakKodeFritattIkkeSokt;

    @Value("${INNTEKT_BM_EGNE_OPPLYSNINGER:0}")
    private int inntektBmEgneOpplysninger;

    @Value("${INNTEKT_BP_EGNE_OPPLYSNINGER:0}")
    private int inntektBpEgneOpplysninger;

    @Value("${KODE_UNNT_FORSK}")
    private String kodeUnntForsk;

    @Value("${SAKSBEHANDLER_ROLLE}")
    private String rolleSaksbehandler;

    @Value("${SAMVARSKLASSE}")
    private String samvarsklasse;

    @Value("${SIVILSTAND_BM}")
    private String sivilstandBm;

    @Value("${SYNTBISYS_PASSWORD}")
    private String saksbehandlerPwd;

    @Value("${SYNTBISYS_USERNAME}")
    private String saksbehandlerUid;

    @Value("${FRADRAG:0}")
    private int sartilskuddFradrag;

    @Value("${GODKJENT_BELOP:0}")
    private int sartilskuddGodkjentBelop;

    @Value("${KRAVBELOP:0}")
    private int sartilskuddKravbelop;

    @Value("${SKATTEKLASSE:1}")
    private int skatteklasse;

    @Value("${syntrest.rest.api.url}")
    private String syntrestServerUrl;

    @Value("${testnorge-hodejegeren.rest-api.url}")
    private String hodejegerenUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public BisysSyntetisererenConsumer bisysSyntetisererenConsumer() {
        return new BisysSyntetisererenConsumer(syntrestServerUrl);
    }

    @Bean
    public BisysUiSupport bisysUiNavigationSupport() {
        return new BisysUiSupport(saksbehandlerUid, saksbehandlerPwd, bisysUrl,
                rolleSaksbehandler, enhet);
    }

    @Bean
    public BidragsmeldingAugments bisysRequestAugments() {

        String gebyrBeslAarsakKode = gebyrBeslAarsakKodeFritattIkkeSokt
                ? BisysUiFatteVedtakConsumer.KODE_BESL_AARSAK_FRITATT_IKKE_SOKT
                : BisysUiFatteVedtakConsumer.KODE_BESL_AARSAK_ILAGT_IKKE_SOKT;

        return BidragsmeldingAugments.builder()
                .andelForsorging(andelForsorging)
                .barnRegistrertPaaAdresse(barnRegistrertPaaAdresse)
                .beslaarsakKode(beslaarsakKode)
                .gebyrBeslAarsakKode(gebyrBeslAarsakKode)
                .inntektBmEgneOpplysninger(inntektBmEgneOpplysninger)
                .inntektBpEgneOpplysninger(inntektBpEgneOpplysninger)
                .kodeUnntForsk(kodeUnntForsk)
                .samvarsklasse(samvarsklasse)
                .sartilskuddKravbelop(sartilskuddKravbelop)
                .sartilskuddGodkjentBelop(sartilskuddGodkjentBelop)
                .sartilskuddFradrag(sartilskuddFradrag)
                .skatteklasse(skatteklasse)
                .sivilstandBm(sivilstandBm)
                .build();
    }

    @Bean
    @DependsOn("restTemplate")
    public HodejegerenConsumer hodejegerenConsumer() {
        return new HodejegerenConsumer(hodejegerenUrl, restTemplate());
    }
}
