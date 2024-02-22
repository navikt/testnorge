package no.nav.registre.testnav.inntektsmeldingservice.controller;

import no.nav.testnav.libs.dto.dokarkiv.v1.RsJoarkMetadata;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.RsInntektsmeldingRequest;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public record InntektsmeldingRequest(
        String miljoe,
        String arbeidstakerFnr,
        RsJoarkMetadata joarkMetadata,
        List<RsInntektsmeldingRequest> inntekter
) {

    public List<RsInntektsmeldingRequest> inntekter() {
        return (isNull(inntekter)) ? new ArrayList<>(0) : inntekter;
    }

}
