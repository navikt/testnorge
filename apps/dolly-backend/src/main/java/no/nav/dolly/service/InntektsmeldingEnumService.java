package no.nav.dolly.service;

import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakBeregnetInntektEndringKoder;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakInnsendingKoder;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakUtsettelseKoder;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.BegrunnelseIngenEllerRedusertUtbetalingKoder;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.NaturalytelseKoder;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.YtelseKoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class InntektsmeldingEnumService {

    public List<String> getEnumType(EnumTypes enumType) {

        return switch (enumType) {
            case AARSAK_TIL_INNSENDING_TYPE -> Stream.of(AarsakInnsendingKoder.values())
                    .map(AarsakInnsendingKoder::name)
                    .collect(Collectors.toList());
            case AARSAK_TIL_UTSETTELSE_TYPE -> Stream.of(AarsakUtsettelseKoder.values())
                    .map(AarsakUtsettelseKoder::name)
                    .collect(Collectors.toList());
            case AARSAK_VED_ENDRING_TYPE -> Stream.of(AarsakBeregnetInntektEndringKoder.values())
                    .map(AarsakBeregnetInntektEndringKoder::name)
                    .collect(Collectors.toList());
            case BEGRUNNELSE_TYPE -> Stream.of(BegrunnelseIngenEllerRedusertUtbetalingKoder.values())
                    .map(BegrunnelseIngenEllerRedusertUtbetalingKoder::name)
                    .collect(Collectors.toList());
            case NATURALYTELSE_TYPE -> Stream.of(NaturalytelseKoder.values())
                    .map(NaturalytelseKoder::name)
                    .collect(Collectors.toList());
            case YTELSE_TYPE -> Stream.of(YtelseKoder.values())
                    .map(YtelseKoder::name)
                    .collect(Collectors.toList());
            default -> Collections.emptyList();
        };
    }

    public enum EnumTypes {

        AARSAK_TIL_INNSENDING_TYPE,
        AARSAK_TIL_UTSETTELSE_TYPE,
        AARSAK_VED_ENDRING_TYPE,
        BEGRUNNELSE_TYPE,
        NATURALYTELSE_TYPE,
        YTELSE_TYPE
    }
}
