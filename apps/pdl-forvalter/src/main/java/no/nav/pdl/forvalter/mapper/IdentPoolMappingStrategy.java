package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.dto.HentIdenterRequest;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
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

    private static HentIdenterRequest.Kjoenn mapKjoenn(KjoennDTO.Kjoenn kjoenn) {

        return isNull(kjoenn) ?
                null :
                switch (kjoenn) {
                    case MANN -> HentIdenterRequest.Kjoenn.MANN;
                    case KVINNE -> HentIdenterRequest.Kjoenn.KVINNE;
                    case UKJENT -> null;
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

                        destinasjon.setKjoenn(mapKjoenn(kilde.getKjoenn()));

                        destinasjon.setAntall(1);
                        destinasjon.setRekvirertAv(PDL_FORVALTER);

                        if (isNull(kilde.getSyntetisk())) {
                            destinasjon.setSyntetisk(true);
                        }
                    }
                })
                .exclude("identtype")
                .exclude("kjoenn")
                .byDefault()
                .register();

        factory.classMap(BestillingRequestDTO.class, HentIdenterRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BestillingRequestDTO kilde, HentIdenterRequest destinasjon, MappingContext context) {

                        destinasjon.setIdenttype(mapIdenttype(kilde.getIdenttype()));
                        var foedsel = kilde.getPerson().getFoedselsdato().stream()
                                .findFirst()
                                .orElse(kilde.getPerson().getFoedsel().stream()
                                        .findFirst()
                                        .orElse(new FoedselDTO()));

                        if (nonNull(foedsel.getFoedselsaar())) {
                            destinasjon.setFoedtEtter(LocalDate.of(foedsel.getFoedselsaar() - 1, 12, 31));
                            destinasjon.setFoedtFoer(LocalDate.of(foedsel.getFoedselsaar() + 1, 1, 1));

                        } else if (nonNull(foedsel.getFoedselsdato())) {
                            destinasjon.setFoedtEtter(foedsel.getFoedselsdato().toLocalDate().minusDays(1));
                            destinasjon.setFoedtFoer(foedsel.getFoedselsdato().toLocalDate().equals(LocalDate.now()) ?
                                    LocalDate.now() :
                                    foedsel.getFoedselsdato().toLocalDate().plusDays(1));

                        } else if (nonNull(kilde.getAlder()) && kilde.getAlder() > 0) {
                            destinasjon.setFoedtEtter(LocalDate.now().minusYears(kilde.getAlder()).minusYears(1));
                            destinasjon.setFoedtFoer(LocalDate.now().minusYears(kilde.getAlder()).minusMonths(3));

                        } else if (isNull(kilde.getFoedtEtter()) && isNull(kilde.getFoedtFoer())) {
                            destinasjon.setFoedtEtter(LocalDate.now().minusYears(60));
                            destinasjon.setFoedtFoer(LocalDate.now().minusYears(30));
                        }

                        if (nonNull(kilde.getPerson())) {
                            destinasjon.setKjoenn(mapKjoenn(kilde.getPerson().getKjoenn().stream()
                                    .findFirst().orElse(new KjoennDTO()).getKjoenn()));
                        }

                        destinasjon.setAntall(1);
                        destinasjon.setRekvirertAv(PDL_FORVALTER);
                    }
                })
                .exclude("identtype")
                .exclude("kjoenn")
                .byDefault()
                .register();
    }
}
