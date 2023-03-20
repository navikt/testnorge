package no.nav.dolly.service;

import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakBeregnetInntektEndringKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakInnsendingKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakUtsettelseKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.BegrunnelseIngenEllerRedusertUtbetalingKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.NaturalytelseKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.YtelseKodeListe;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class InntektsmeldingEnumService {

    public List<String> getEnumType(EnumTypes enumType) {

        return switch (enumType) {
            case AARSAK_TIL_INNSENDING_TYPE -> Stream.of(AarsakInnsendingKodeListe.values())
                    .map(AarsakInnsendingKodeListe::getValue)
                    .collect(Collectors.toList());
            case AARSAK_TIL_UTSETTELSE_TYPE -> Stream.of(AarsakUtsettelseKodeListe.values())
                    .map(AarsakUtsettelseKodeListe::getValue)
                    .collect(Collectors.toList());
            case AARSAK_VED_ENDRING_TYPE -> Stream.of(AarsakBeregnetInntektEndringKodeListe.values())
                    .map(AarsakBeregnetInntektEndringKodeListe::getValue)
                    .collect(Collectors.toList());
            case BEGRUNNELSE_TYPE -> Stream.of(BegrunnelseIngenEllerRedusertUtbetalingKodeListe.values())
                    .map(BegrunnelseIngenEllerRedusertUtbetalingKodeListe::getValue)
                    .collect(Collectors.toList());
            case NATURALYTELSE_TYPE -> Stream.of(NaturalytelseKodeListe.values())
                    .map(NaturalytelseKodeListe::getValue)
                    .collect(Collectors.toList());
            case YTELSE_TYPE -> Stream.of(YtelseKodeListe.values())
                    .map(YtelseKodeListe::getValue)
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
