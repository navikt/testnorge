package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonVedtakRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.RevurderingVedtakRequest;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pensjonforvalter.mapper.PensjonMappingSupportUtils.getRandomAnsatt;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.NAV_ENHET;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Component
public class PensjonAlderspensjonRevurderingVedtakMappingStrategy implements MappingStrategy {

    private static final String SIVILSTAND = "sivilstand";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(AlderspensjonVedtakRequest.class, RevurderingVedtakRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(AlderspensjonVedtakRequest alderspensjonVedtakRequest, RevurderingVedtakRequest revurderingVedtakRequest, MappingContext context) {

                        var sivilstand = (List<PdlPerson.Sivilstand>) context.getProperty(SIVILSTAND);

                        revurderingVedtakRequest.setFom(getNesteMaaned(getGjeldeneSivilstand(sivilstand)));
                        revurderingVedtakRequest.setRevurderingArsakType("SIVILSTANDENDRING");
                        revurderingVedtakRequest.setNavEnhetId(isNotBlank(alderspensjonVedtakRequest.getNavEnhetId()) ?
                                alderspensjonVedtakRequest.getNavEnhetId() : ((String) context.getProperty(NAV_ENHET)));
                        revurderingVedtakRequest.setSaksbehandler(isNotBlank(alderspensjonVedtakRequest.getSaksbehandler()) ?
                                alderspensjonVedtakRequest.getSaksbehandler() : getRandomAnsatt());
                        revurderingVedtakRequest.setAttesterer(isNotBlank(alderspensjonVedtakRequest.getAttesterer()) ?
                                alderspensjonVedtakRequest.getAttesterer() : getRandomAnsatt());
                    }
                })
                .byDefault()
                .register();
    }

    private static PdlPerson.Sivilstand getGjeldeneSivilstand(List<PdlPerson.Sivilstand> sivilstandDTO) {

        return sivilstandDTO.stream()
                .filter(sivilstand -> isFalse(sivilstand.getMetadata().getHistorisk()))
                .findFirst()
                .orElse(new PdlPerson.Sivilstand());
    }

    private static LocalDate getNesteMaaned(PdlPerson.Sivilstand sivilstand) {

        if (nonNull(sivilstand.getGyldigFraOgMed())) {
            return PensjonMappingSupportUtils.getNesteMaaned(sivilstand.getGyldigFraOgMed());
        }

        if (nonNull(sivilstand.getBekreftelsesdato())) {
            return PensjonMappingSupportUtils.getNesteMaaned(sivilstand.getBekreftelsesdato());
        }

        return null;
    }
}