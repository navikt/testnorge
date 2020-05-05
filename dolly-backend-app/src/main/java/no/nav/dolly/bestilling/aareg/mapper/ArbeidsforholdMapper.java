package no.nav.dolly.bestilling.aareg.mapper;

import java.util.List;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsAaregArbeidsforhold;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;

@Service
@RequiredArgsConstructor
public class ArbeidsforholdMapper {

    private final MapperFacade mapperFacade;

    public List<Arbeidsforhold> map(List<RsAaregArbeidsforhold> rsAaregArbeidsforhold, TpsPerson tpsPerson) {

        return mapperFacade.mapAsList(PersonArbeidsforhold.builder()
                .arbeidsforhold(rsAaregArbeidsforhold)
                .tpsPerson(tpsPerson)
                .build(), Arbeidsforhold.class);
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonArbeidsforhold {

        private TpsPerson tpsPerson;
        private RsAaregArbeidsforhold arbeidsforhold;
    }
}
