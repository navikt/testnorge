package no.nav.dolly.bestilling.instdata;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.repository.BestillingRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstKdiHendelseService {

    private final BestillingRepository bestillingRepository;


}
