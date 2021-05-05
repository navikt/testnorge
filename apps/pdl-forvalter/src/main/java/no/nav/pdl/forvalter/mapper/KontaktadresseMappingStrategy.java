package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.domain.PdlKontaktadresse;
import no.nav.pdl.forvalter.dto.RsKontaktadresse;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.domain.PdlAdresse.Master.FREG;
import static no.nav.pdl.forvalter.domain.PdlAdresse.Master.PDL;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class KontaktadresseMappingStrategy implements MappingStrategy {



    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsKontaktadresse.class, PdlKontaktadresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsKontaktadresse kildeAdresse, PdlKontaktadresse kontaktadresse, MappingContext context) {

                        if (isNotBlank(kontaktadresse.getAdresseIdentifikatorFraMatrikkelen())) {
                            kontaktadresse.setMaster(FREG);
                            kontaktadresse.setVegadresseForPost(
                                    mapperFacade.map(kildeAdresse.getVegadresse(), PdlKontaktadresse.VegadresseForPost.class));
                            kontaktadresse.setVegadresse(null);
                        } else if (nonNull(kontaktadresse.getVegadresse())) {
                            kontaktadresse.setMaster(PDL);
                        }
                    }
                })
                .byDefault()
                .register();
    }
}