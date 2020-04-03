package no.nav.dolly.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

import no.nav.dolly.domain.resultset.inntektsmeldingstub.AarsakTilUtsettelseType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.AarsakVedEndringType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.BegrunnelseForReduksjonEllerIkkeUtbetaltType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.NaturalytelseType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.YtelseType;

@Service
public class InntektsmeldingEnumService {

    public enum EnumTypes {

        AARSAK_TIL_UTSETTELSE_TYPE,
        AARSAK_VED_ENDRING_TYPE,
        BEGRUNNELSE_TYPE,
        NATURALYTELSE_TYPE,
        YTELSE_TYPE
    }

    public List<String> getEnumType(EnumTypes enumType) {

        switch (enumType) {

        case AARSAK_TIL_UTSETTELSE_TYPE:
            return Stream.of(AarsakTilUtsettelseType.values()).map(Enum::name).collect(Collectors.toList());

        case AARSAK_VED_ENDRING_TYPE:
            return Stream.of(AarsakVedEndringType.values()).map(Enum::name).collect(Collectors.toList());

        case BEGRUNNELSE_TYPE:
            return Stream.of(BegrunnelseForReduksjonEllerIkkeUtbetaltType.values()).map(Enum::name).collect(Collectors.toList());

        case NATURALYTELSE_TYPE:
            return Stream.of(NaturalytelseType.values()).map(Enum::name).collect(Collectors.toList());

        case YTELSE_TYPE:
            return Stream.of(YtelseType.values()).map(Enum::name).collect(Collectors.toList());

        default:
            return Collections.emptyList();
        }
    }
}
