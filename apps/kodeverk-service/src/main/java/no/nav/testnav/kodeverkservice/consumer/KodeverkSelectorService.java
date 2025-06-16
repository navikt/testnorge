package no.nav.testnav.kodeverkservice.consumer;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.kodeverkservice.service.KodeverkService;
import no.nav.testnav.kodeverkservice.utility.TemaHistarkUtility;
import no.nav.testnav.kodeverkservice.utility.VergemaalFylkesembeterUtility;
import no.nav.testnav.kodeverkservice.utility.YrkesklassifiseringUtility;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkAdjustedDTO;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static no.nav.testnav.kodeverkservice.utility.CommonKeysAndUtils.TEMAHISTARK;
import static no.nav.testnav.kodeverkservice.utility.CommonKeysAndUtils.VERGEMAAL_FYLKESEMBETER;
import static no.nav.testnav.kodeverkservice.utility.CommonKeysAndUtils.YRKESKLASSIFISERING;

@Service
@RequiredArgsConstructor
public class KodeverkSelectorService {

    private final KodeverkService kodeverkService;

    public Mono<KodeverkDTO> getKodeverkMap(String kodeverk) {

        return switch (kodeverk) {
            case TEMAHISTARK -> Mono.just(TemaHistarkUtility.getKodeverk());
            case YRKESKLASSIFISERING -> Mono.just(YrkesklassifiseringUtility.getKodeverk());
            case VERGEMAAL_FYLKESEMBETER -> Mono.just(VergemaalFylkesembeterUtility.getKodeverk());
            default -> kodeverkService.getKodeverkMap(kodeverk);
        };
    }

    public Mono<KodeverkAdjustedDTO> getKodeverkByName(String kodeverkNavn) {

        return switch (kodeverkNavn) {
            case TEMAHISTARK -> Mono.just(TemaHistarkUtility.getKodeverkAdjusted());
            case YRKESKLASSIFISERING -> Mono.just(YrkesklassifiseringUtility.getKodeverkAdjusted());
            case VERGEMAAL_FYLKESEMBETER -> Mono.just(VergemaalFylkesembeterUtility.getKodeverkAdjusted());
            default -> kodeverkService.getKodeverkByName(kodeverkNavn);
        };
    }
}
