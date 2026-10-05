package no.nav.dolly.bestilling.henvendelse.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.henvendelse.dto.HenvendelseMeldingRequest;
import no.nav.dolly.bestilling.henvendelse.dto.HenvendelseSamtalereferatRequest;
import no.nav.dolly.domain.resultset.henvendelse.RsHenvendelse;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class HenvendelseMappingStrategy implements MappingStrategy {

    private static final String SAMTALEREFERAT = "samtalereferat";
    private static final String MELDING = "melding";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsHenvendelse.Melding.class, HenvendelseMeldingRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsHenvendelse.Melding kilde, HenvendelseMeldingRequest resultat, MappingContext context) {

                        resultat.setAktorId((String) context.getProperty("aktorId"));
                        if (isBlank(resultat.getEnhet())) {
                            resultat.setEnhet((String) (context.getProperty("enhet")));
                        }
                        resultat.setType(MELDING);
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsHenvendelse.Samtalereferat.class, HenvendelseSamtalereferatRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsHenvendelse.Samtalereferat kilde, HenvendelseSamtalereferatRequest resultat, MappingContext context) {

                        resultat.setAktorId((String) context.getProperty("aktorId"));
                        if (isBlank(resultat.getEnhet())) {
                            resultat.setEnhet((String) (context.getProperty("enhet")));
                        }
                        resultat.setType(SAMTALEREFERAT);
                    }
                })
                .byDefault()
                .register();
    }
}
