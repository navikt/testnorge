package no.nav.dolly.bestilling.sigrunstub.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubPensjonsgivendeInntektRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubSummertskattegrunnlagRequest;
import no.nav.dolly.domain.resultset.sigrunstub.RsPensjonsgivendeForFolketrygden;
import no.nav.dolly.domain.resultset.sigrunstub.RsSummertSkattegrunnlag;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
@RequiredArgsConstructor
public class SigrunstubDataMapper implements MappingStrategy {

    private static final String IDENT = "ident";

    private final JsonMapper jsonMapper;

    @Override
    public void register(MapperFactory factory) {

                factory.classMap(RsPensjonsgivendeForFolketrygden.class, SigrunstubPensjonsgivendeInntektRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsPensjonsgivendeForFolketrygden kilde, SigrunstubPensjonsgivendeInntektRequest destinasjon, MappingContext context) {

                        destinasjon.setNorskident((String) context.getProperty(IDENT));

                        destinasjon.setTestdataEier(isNotBlank(kilde.getTestdataEier()) ? kilde.getTestdataEier() : "Dolly");
                        destinasjon.setInntektsaar(kilde.getInntektsaar());

                        try {
                            destinasjon.setPensjonsgivendeInntekt(
                                    jsonMapper.readTree(
                                            jsonMapper.writeValueAsString(kilde.getPensjonsgivendeInntekt())));
                        } catch (JacksonException _) {
                            log.error("Feilet å gjøre {} om til JSON", kilde.getPensjonsgivendeInntekt());
                        }
                    }
                })
                .register();

        factory.classMap(RsSummertSkattegrunnlag.class, SigrunstubSummertskattegrunnlagRequest.Summertskattegrunnlag.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsSummertSkattegrunnlag kilde, SigrunstubSummertskattegrunnlagRequest.Summertskattegrunnlag destinasjon, MappingContext context) {
                        log.info("Mottok summertSkattegrunnlag {}", kilde);

                        destinasjon.setPersonidentifikator((String) context.getProperty(IDENT));
                    }
                })
                .byDefault()
                .register();
    }
}
