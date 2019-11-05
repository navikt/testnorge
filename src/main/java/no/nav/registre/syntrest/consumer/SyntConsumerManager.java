package no.nav.registre.syntrest.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import no.nav.registre.syntrest.utils.SyntAppNames;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
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
    private final SyntConsumer frikortConsumer;
    private final SyntConsumer eiaConsumer;

    public SyntConsumerManager(ApplicationManager applicationManager, RestTemplate restTemplate) {

        aaregConsumer = new SyntConsumer(applicationManager, restTemplate, SyntAppNames.AAREG);
        bisysConsumer = new SyntConsumer(applicationManager, restTemplate, SyntAppNames.BISYS);
        meldekortConsumer = new SyntConsumer(applicationManager, restTemplate, SyntAppNames.MELDEKORT);
        aapConsumer = new SyntConsumer(applicationManager, restTemplate, SyntAppNames.AAP);
        instConsumer = new SyntConsumer(applicationManager, restTemplate, SyntAppNames.INST);
        inntektConsumer = new SyntConsumer(applicationManager, restTemplate, SyntAppNames.INNTEKT);
        medlConsumer = new SyntConsumer(applicationManager, restTemplate, SyntAppNames.MEDL);
        poppConsumer = new SyntConsumer(applicationManager, restTemplate, SyntAppNames.POPP);
        tpConsumer = new SyntConsumer(applicationManager, restTemplate, SyntAppNames.TP);
        tpsConsumer = new SyntConsumer(applicationManager, restTemplate, SyntAppNames.TPS);
        navConsumer = new SyntConsumer(applicationManager, restTemplate, SyntAppNames.NAV);
        samConsumer = new SyntConsumer(applicationManager, restTemplate, SyntAppNames.SAM);
        frikortConsumer = new SyntConsumer(applicationManager, restTemplate, SyntAppNames.FRIKORT);
        eiaConsumer = new SyntConsumer(applicationManager, restTemplate, SyntAppNames.EIA);
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
            case FRIKORT:
                return frikortConsumer;
            case EIA:
                return eiaConsumer;
            default:
                log.error("No bean of type {} is defined. Returning null.", type);
                return null;
        }

    }
}
