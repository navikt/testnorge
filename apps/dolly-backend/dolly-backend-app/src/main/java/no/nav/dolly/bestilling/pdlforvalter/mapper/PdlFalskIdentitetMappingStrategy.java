package no.nav.dolly.bestilling.pdlforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.pdlforvalter.PdlOpplysning.Master;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlFalskIdentitet;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlRettIdentitetErUkjent;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlRettIdentitetVedIdentifikasjonsnummer;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlRettIdentitetVedOpplysninger;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.RsPdlFalskIdentitet;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.RsPdlRettIdentitetVedOpplysninger;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

@Component
public class PdlFalskIdentitetMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsPdlFalskIdentitet.class, PdlFalskIdentitet.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsPdlFalskIdentitet rsPdlFalskIdentitet,
                                        PdlFalskIdentitet pdlFalskIdentitet, MappingContext context) {

                        if (rsPdlFalskIdentitet.getRettIdentitet() instanceof PdlRettIdentitetErUkjent) {
                            pdlFalskIdentitet.setRettIdentitet(PdlFalskIdentitet.PdlRettIdentitet.builder()
                                    .rettIdentitetErUkjent(
                                            ((PdlRettIdentitetErUkjent) rsPdlFalskIdentitet.getRettIdentitet()).getRettIdentitetErUkjent())
                                    .build());
                        } else if (rsPdlFalskIdentitet.getRettIdentitet() instanceof PdlRettIdentitetVedIdentifikasjonsnummer) {
                            pdlFalskIdentitet.setRettIdentitet(PdlFalskIdentitet.PdlRettIdentitet.builder()
                                    .rettIdentitetVedIdentifikasjonsnummer(
                                            ((PdlRettIdentitetVedIdentifikasjonsnummer) rsPdlFalskIdentitet.getRettIdentitet()).getRettIdentitetVedIdentifikasjonsnummer())
                                    .build());
                        } else if (rsPdlFalskIdentitet.getRettIdentitet() instanceof RsPdlRettIdentitetVedOpplysninger) {
                            pdlFalskIdentitet.setRettIdentitet(PdlFalskIdentitet.PdlRettIdentitet.builder()
                                    .rettIdentitetVedOpplysninger(
                                            mapperFacade.map(rsPdlFalskIdentitet.getRettIdentitet(), PdlRettIdentitetVedOpplysninger.class))
                                    .build());
                        }

                        pdlFalskIdentitet.setErFalsk(nullcheckSetDefaultValue(pdlFalskIdentitet.getErFalsk(), true));
                        pdlFalskIdentitet.setKilde(nullcheckSetDefaultValue(pdlFalskIdentitet.getKilde(), CONSUMER));
                        pdlFalskIdentitet.setMaster(Master.FREG);
                    }
                })
                .byDefault()
                .register();
    }
}