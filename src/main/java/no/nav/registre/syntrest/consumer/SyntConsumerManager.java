package no.nav.registre.syntrest.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import no.nav.registre.syntrest.utils.SyntAppNames;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ScheduledExecutorService;

@Component
@DependsOn("appConfig")
@Slf4j
public class SyntConsumerManager {

    private final SyntConsumer aaregConsumer;
    private final SyntConsumer bisysConsumer;
    private final SyntConsumer meldekortConsumer;
    private final SyntConsumer aapConsumer;
    private final SyntConsumer instConsumer;
    private final SyntConsumer inntektConsumer;
    private final SyntConsumer medlConsumer;
    private final SyntConsumer poppConsumer;
    private final SyntConsumer tpConsumer;
    private final SyntConsumer tpsConsumer;
    private final SyntConsumer navConsumer;
    private final SyntConsumer samConsumer;

    public SyntConsumerManager(ApplicationManager applicationManager, RestTemplate restTemplate, ScheduledExecutorService scheduledExecutorService) {

        aaregConsumer = new SyntConsumer(applicationManager, restTemplate, scheduledExecutorService, SyntAppNames.AAREG);
        bisysConsumer = new SyntConsumer(applicationManager, restTemplate, scheduledExecutorService, SyntAppNames.BISYS);
        meldekortConsumer = new SyntConsumer(applicationManager, restTemplate, scheduledExecutorService, SyntAppNames.MELDEKORT);
        aapConsumer = new SyntConsumer(applicationManager, restTemplate, scheduledExecutorService, SyntAppNames.AAP);
        instConsumer = new SyntConsumer(applicationManager, restTemplate, scheduledExecutorService, SyntAppNames.INST);
        inntektConsumer = new SyntConsumer(applicationManager, restTemplate, scheduledExecutorService, SyntAppNames.INNTEKT);
        medlConsumer = new SyntConsumer(applicationManager, restTemplate, scheduledExecutorService, SyntAppNames.MEDL);
        poppConsumer = new SyntConsumer(applicationManager, restTemplate, scheduledExecutorService, SyntAppNames.POPP);
        tpConsumer = new SyntConsumer(applicationManager, restTemplate, scheduledExecutorService, SyntAppNames.TP);
        tpsConsumer = new SyntConsumer(applicationManager, restTemplate, scheduledExecutorService, SyntAppNames.TPS);
        navConsumer = new SyntConsumer(applicationManager, restTemplate, scheduledExecutorService, SyntAppNames.NAV);
        samConsumer = new SyntConsumer(applicationManager, restTemplate, scheduledExecutorService, SyntAppNames.SAM);
    }

    public SyntConsumer get(SyntAppNames type) {

        switch (type) {
            case AAREG:
                return aaregConsumer;
            case BISYS:
                return bisysConsumer;
            case MELDEKORT:
                return meldekortConsumer;
            case AAP:
                return aapConsumer;
            case INST:
                return instConsumer;
            case INNTEKT:
                return inntektConsumer;
            case MEDL:
                return medlConsumer;
            case POPP:
                return poppConsumer;
            case TP:
                return tpConsumer;
            case TPS:
                return tpsConsumer;
            case NAV:
                return navConsumer;
            case SAM:
                return samConsumer;
            default:
                log.error("No bean of type {} is defined. Returning null.", type);
                return null;
        }

    }
}
