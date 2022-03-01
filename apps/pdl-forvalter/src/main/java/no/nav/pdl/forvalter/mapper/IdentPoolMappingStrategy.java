package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.dto.HentIdenterRequest;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class IdentPoolMappingStrategy implements MappingStrategy {

    private static final String PDL_FORVALTER = "PDLF";

    private static HentIdenterRequest.Identtype mapIdenttype(Identtype identtype) {

        return isNull(identtype) ?

                HentIdenterRequest.Identtype.FNR :

                switch (identtype) {
                    case FNR -> HentIdenterRequest.Identtype.FNR;
                    case DNR -> HentIdenterRequest.Identtype.DNR;
                    case NPID -> HentIdenterRequest.Identtype.BOST;
                };
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(PersonRequestDTO.class, HentIdenterRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PersonRequestDTO kilde, HentIdenterRequest destinasjon, MappingContext context) {

                        destinasjon.setIdenttype(mapIdenttype(kilde.getIdenttype()));

                        if (nonNull(kilde.getAlder()) && kilde.getAlder() > 0) {
                            destinasjon.setFoedtEtter(LocalDate.now().minusYears(kilde.getAlder()).minusYears(1));
                            destinasjon.setFoedtFoer(LocalDate.now().minusYears(kilde.getAlder()).minusMonths(3));
                        }

                        destinasjon.setAntall(1);
                        destinasjon.setRekvirertAv(PDL_FORVALTER);
                    }
                })
                .exclude("identtype")
                .byDefault()
                .register();

        factory.classMap(BestillingRequestDTO.class, HentIdenterRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BestillingRequestDTO kilde, HentIdenterRequest destinasjon, MappingContext context) {

                        destinasjon.setIdenttype(mapIdenttype(kilde.getIdenttype()));

                        if (nonNull(kilde.getAlder()) && kilde.getAlder() > 0) {
                            destinasjon.setFoedtEtter(LocalDate.now().minusYears(kilde.getAlder()).minusYears(1));
                            destinasjon.setFoedtFoer(LocalDate.now().minusYears(kilde.getAlder()).minusMonths(3));

                        } else if (isNull(kilde.getFoedtEtter()) && isNull(kilde.getFoedtFoer())) {
                            destinasjon.setFoedtEtter(LocalDate.now().minusYears(60));
                            destinasjon.setFoedtFoer(LocalDate.now().minusYears(30));
                        }

                        if (nonNull(kilde.getPerson())) {
                            destinasjon.setKjoenn(kilde.getPerson().getKjoenn().stream()
                                    .findFirst().orElse(new KjoennDTO()).getKjoenn());
                        }

                        destinasjon.setAntall(1);
                        destinasjon.setRekvirertAv(PDL_FORVALTER);
                    }
                })
                .exclude("identtype")
                .byDefault()
                .register();
    }
}
