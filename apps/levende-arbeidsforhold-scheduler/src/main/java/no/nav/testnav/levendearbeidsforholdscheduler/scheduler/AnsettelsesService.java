package no.nav.testnav.levendearbeidsforholdscheduler.scheduler;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.levendearbeidsforholdscheduler.consumer.AnsettelseConsumer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnsettelsesService {

    private final AnsettelseConsumer ansettelseConsumer;

    public void hent(){
        ansettelseConsumer.hentFraAnsettelse();
    }
}
