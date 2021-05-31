package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.domain.Identtype;
import no.nav.pdl.forvalter.dto.HentIdenterRequest;
import no.nav.pdl.forvalter.dto.RsPersonRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class IdentPoolMappingStrategy implements MappingStrategy {

    private static final String PDL_FORVALTER = "PDLF";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsPersonRequest.class, HentIdenterRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsPersonRequest kilde, HentIdenterRequest destinasjon, MappingContext context) {

                        if (isNull(kilde.getIdenttype())) {
                            destinasjon.setIdenttype(Identtype.FNR);
                        }

                        if (nonNull(kilde.getAlder()) && kilde.getAlder() > 0) {
                            destinasjon.setFoedtEtter(LocalDate.now().minusYears(kilde.getAlder()).minusYears(1));
                            destinasjon.setFoedtFoer(LocalDate.now().minusYears(kilde.getAlder()).minusMonths(3));
                        }

                        destinasjon.setAntall(1);
                        destinasjon.setRekvirertAv(PDL_FORVALTER);
                    }
                })
                .byDefault()
                .register();
    }
}
