package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonVedtakRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.RevurderingVedtakRequest;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.data.pdlforvalter.v1.SivilstandDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pensjonforvalter.mapper.PensjonMappingSupportUtils.getRandomAnsatt;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Component
public class PensjonAlederspensjonRevurderingVedtakMappingStrategy implements MappingStrategy {

    private static final String NAV_ENHET = "navEnhetId";
    private static final String SIVILSTAND = "sivilstand";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(AlderspensjonVedtakRequest.class, RevurderingVedtakRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(AlderspensjonVedtakRequest alderspensjonVedtakRequest, RevurderingVedtakRequest revurderingVedtakRequest, MappingContext context) {

                        var sivilstand = (List<SivilstandDTO>) context.getProperty(SIVILSTAND);

                        revurderingVedtakRequest.setFom(getNesteMaaned(sivilstand.getFirst()));
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

    private static LocalDate getNesteMaaned(SivilstandDTO sivilstand) {

        if (nonNull(sivilstand.getSivilstandsdato())) {
            return getNesteMaaned(sivilstand.getSivilstandsdato());
        }

        if (nonNull(sivilstand.getBekreftelsesdato())) {
            return getNesteMaaned(sivilstand.getBekreftelsesdato());
        }

        if (nonNull(sivilstand.getFolkeregistermetadata()) &&
                nonNull(sivilstand.getFolkeregistermetadata().getGyldighetstidspunkt())) {
            return getNesteMaaned(sivilstand.getFolkeregistermetadata().getGyldighetstidspunkt());
        }

        return getNesteMaaned(now());
    }

    private static LocalDate getNesteMaaned(LocalDateTime dato) {

            var nesteMaaned = dato.plusMonths(1);
            return LocalDate.of(nesteMaaned.getYear(), nesteMaaned.getMonth(), 1);
    }
}