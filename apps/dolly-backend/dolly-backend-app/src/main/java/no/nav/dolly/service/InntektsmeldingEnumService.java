package no.nav.dolly.service;

import no.nav.dolly.domain.resultset.inntektsmeldingstub.AarsakTilInnsendingType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.AarsakTilUtsettelseType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.AarsakVedEndringType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.BegrunnelseForReduksjonEllerIkkeUtbetaltType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.NaturalytelseType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.YtelseType;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class InntektsmeldingEnumService {

    public enum EnumTypes {

        AARSAK_TIL_INNSENDING_TYPE,
        AARSAK_TIL_UTSETTELSE_TYPE,
        AARSAK_VED_ENDRING_TYPE,
        BEGRUNNELSE_TYPE,
        NATURALYTELSE_TYPE,
        YTELSE_TYPE
    }

    public List<String> getEnumType(EnumTypes enumType) {

        return switch (enumType) {
            case AARSAK_TIL_INNSENDING_TYPE -> Stream.of(AarsakTilInnsendingType.values()).map(Enum::name).collect(Collectors.toList());
            case AARSAK_TIL_UTSETTELSE_TYPE -> Stream.of(AarsakTilUtsettelseType.values()).map(Enum::name).collect(Collectors.toList());
            case AARSAK_VED_ENDRING_TYPE -> Stream.of(AarsakVedEndringType.values()).map(Enum::name).collect(Collectors.toList());
            case BEGRUNNELSE_TYPE -> Stream.of(BegrunnelseForReduksjonEllerIkkeUtbetaltType.values()).map(Enum::name).collect(Collectors.toList());
            case NATURALYTELSE_TYPE -> Stream.of(NaturalytelseType.values()).map(Enum::name).collect(Collectors.toList());
            case YTELSE_TYPE -> Stream.of(YtelseType.values()).map(Enum::name).collect(Collectors.toList());
            default -> Collections.emptyList();
        };
    }
}
