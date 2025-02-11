package no.nav.testnav.kodeverkservice.consumer;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.kodeverkservice.service.KodeverkService;
import no.nav.testnav.kodeverkservice.utility.YrkesklassifiseringUtility;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkAdjustedDTO;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KodeverkFilterService {

    private static final String YRKESKLASSIFISERING = "Yrkesklassifisering";
    private final KodeverkService kodeverkService;

    public Mono<KodeverkDTO> getKodeverkMap(String kodeverk) {

        if (!YRKESKLASSIFISERING.equals(kodeverk)) {
            return kodeverkService.getKodeverkMap(kodeverk);
        } else {
            return Mono.just(YrkesklassifiseringUtility.getKodeverk());
        }
    }

    public Mono<KodeverkAdjustedDTO> getKodeverkByName(String kodeverkNavn) {

        if (!YRKESKLASSIFISERING.equals(kodeverkNavn)) {
            return kodeverkService.getKodeverkByName(kodeverkNavn);
        } else {
            return Mono.just(YrkesklassifiseringUtility.getKodeverkAdjusted());
        }
    }
}
