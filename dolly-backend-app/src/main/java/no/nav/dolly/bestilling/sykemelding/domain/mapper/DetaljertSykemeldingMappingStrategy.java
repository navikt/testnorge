package no.nav.dolly.bestilling.sykemelding.domain.mapper;

import static java.util.Objects.isNull;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.domain.resultset.sykemelding.RsDetaljertSykemelding;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class DetaljertSykemeldingMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsDetaljertSykemelding.class, DetaljertSykemeldingRequest.class)
                .customize(new CustomMapper<RsDetaljertSykemelding, DetaljertSykemeldingRequest>() {
                    @Override
                    public void mapAtoB(RsDetaljertSykemelding rsSykemelding, DetaljertSykemeldingRequest request, MappingContext context) {

                        DetaljertSykemeldingRequest.Organisasjon organisasjon = DetaljertSykemeldingRequest.Organisasjon.builder()
                                .adresse(DetaljertSykemeldingRequest.Adresse.builder()
                                        .postnummer("6789")
                                        .land("NOR")
                                        .gate("Syntegaten")
                                        .by("Synteby")
                                        .build())
                                .navn("Synt & Co.")
                                .orgNr("123")
                                .build();

                        if (isNull(rsSykemelding.getDetaljer())) {
                            request.setDetaljer(DetaljertSykemeldingRequest.Detaljer.builder()
                                    .arbeidsforEtterEndtPeriode(true)
                                    .build());
                        }

                        if (isNull(rsSykemelding.getMottaker())) {
                            request.setMottaker(organisasjon);
                        }

                        if (isNull(rsSykemelding.getSender())) {
                            request.setSender(organisasjon);
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
