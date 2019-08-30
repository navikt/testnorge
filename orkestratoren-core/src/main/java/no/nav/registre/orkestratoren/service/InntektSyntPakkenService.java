package no.nav.registre.orkestratoren.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.rs.InntektSyntConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;

@Service
@Slf4j
public class InntektSyntPakkenService {

    @Autowired
    private InntektSyntConsumer inntektSyntConsumer;

    public String genererInntektsmeldinger(SyntetiserInntektsmeldingRequest syntetiserInntektsmeldingRequest) {
        return inntektSyntConsumer.startSyntetisering(syntetiserInntektsmeldingRequest);
    }
}
