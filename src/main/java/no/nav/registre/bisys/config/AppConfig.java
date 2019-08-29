package no.nav.registre.bisys.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import no.nav.registre.bisys.consumer.rs.BisysSyntetisererenConsumer;
import no.nav.registre.bisys.consumer.rs.HodejegerenConsumer;
import no.nav.registre.bisys.consumer.rs.request.BisysRequestAugments;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;
import no.nav.registre.bisys.consumer.ui.modules.BisysUiFatteVedtakConsumer;

@Configuration
public class AppConfig {

    public static final String STANDARD_DATE_FORMAT_BISYS = "dd.MM.yyyy";
    public static final String STANDARD_DATE_FORMAT_TESTNORGEBISYS_REQUEST = "yyyy-MM-dd";

    // Will be set to true for BMs household (forskudd)
    private final static boolean boforholdBarnRegistrertPaaAdresse = false;

    @Value("${syntrest.rest.api.url}")
    String syntrestServerUrl;

    @Value("${SYNTBISYS_USERNAME}")
    String saksbehandlerUid;

    @Value("${SYNTBISYS_PASSWORD}")
    String saksbehandlerPwd;

    @Value("${BISYS_URL}")
    String bisysUrl;

    @Value("${SAKSBEHANDLER_ROLLE}")
    String rolleSaksbehandler;

    @Value("${ENHET}")
    String enhet;

    @Value("${testnorge-hodejegeren.rest-api.url}")
    String hodejegerenServerUrl;

    @Value("${BOFORHOLD_ANDEL_FORSORGING}")
    String boforholdAndelForsorging;

    @Value("${BIDRAGSBEREGNING_KODE_VIRK_AARSAK}")
    String bidragsberegningKodeVirkAarsak;

    @Value("${BIDRAGSBEREGNING_SAMVARSKLASSE}")
    String bidragsberegningSamvarsklasse;

    @Value("${FATTE_VEDTAK_GEBYR_BESLAARSAK_KODE_FRITATT_IKKE_SOKT}")
    boolean fatteVedtakGebyrBeslAarsakKodeFritattIkkeSokt;

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
                rolleSaksbehandler, Integer.parseInt(enhet));
    }

    @Bean
    public BisysRequestAugments bisysRequestAugments() {

        String fatteVedtakGebyrBeslAarsakKode = fatteVedtakGebyrBeslAarsakKodeFritattIkkeSokt
                ? BisysUiFatteVedtakConsumer.KODE_BESL_AARSAK_FRITATT_IKKE_SOKT
                : BisysUiFatteVedtakConsumer.KODE_BESL_AARSAK_ILAGT_IKKE_SOKT;

        return new BisysRequestAugments(boforholdAndelForsorging, boforholdBarnRegistrertPaaAdresse,
                bidragsberegningKodeVirkAarsak, bidragsberegningSamvarsklasse,
                fatteVedtakGebyrBeslAarsakKode);
    }

    @Bean
    public HodejegerenConsumer hodejegerenConsumer() {
        return new HodejegerenConsumer(hodejegerenServerUrl);
    }
}
