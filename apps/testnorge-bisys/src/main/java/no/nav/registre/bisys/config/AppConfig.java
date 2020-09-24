package no.nav.registre.bisys.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.bisys.consumer.rs.BisysSyntetisererenConsumer;
import no.nav.registre.bisys.consumer.rs.request.BidragsmeldingAugments;
import no.nav.registre.bisys.consumer.ui.BisysUiConsumer;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;
import no.nav.registre.bisys.consumer.ui.vedtak.BisysUiFatteVedtakConsumer;
import no.nav.registre.bisys.service.SyntetiseringService;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;

@Configuration
@Import(ApplicationCoreConfig.class)
public class AppConfig {

    // Will be set to true for BMs household (forskudd)
    private final static boolean barnRegistrertPaaAdresse = false;

    @Value("${ANDEL_FORSORGING}")
    private String andelForsorging;

    @Value("${BISYS_URL}")
    private String bisysUrl;

    @Value("${BESLAARSAK_KODE}")
    private String beslaarsakKode;

    @Value("${ENHET:4802}")
    private int enhet;

    @Value("${GEBYR_BESLAARSAK_KODE_FRITATT_IKKE_SOKT}")
    private boolean gebyrBeslAarsakKodeFritattIkkeSokt;

    @Value("${INNTEKT_BM_EGNE_OPPLYSNINGER:0}")
    private int inntektBmEgneOpplysninger;

    @Value("${INNTEKT_BP_EGNE_OPPLYSNINGER:0}")
    private int inntektBpEgneOpplysninger;

    @Value("${KODE_UNNT_FORSK:}")
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

    @Value("${SYNTREST_REST_API}")
    private String syntrestServerUrl;

    @Value("${HODEJEGEREN_REST_API}")
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
                .skatteklasse(skatteklasse)
                .sivilstandBm(sivilstandBm)
                .build();
    }

    @Bean
    @DependsOn("restTemplate")
    public HodejegerenConsumer hodejegerenConsumer() {
        return new HodejegerenConsumer(hodejegerenUrl, restTemplate());
    }

    @Bean
    public SyntetiseringService syntetiseringService(
            @Autowired BisysSyntetisererenConsumer bisysSyntetisererenConsumer,
            @Autowired BisysUiConsumer bisysUiConsumer,
            @Value("${USE_HISTORICAL_MOTTATTDATO}") boolean useHistoricalMottattdato) {

        return SyntetiseringService.builder()
                .hodejegerenConsumer(hodejegerenConsumer())
                .bisysSyntetisererenConsumer(bisysSyntetisererenConsumer)
                .bisysUiConsumer(bisysUiConsumer)
                .useHistoricalMottattdato(useHistoricalMottattdato).build();
    }
}
