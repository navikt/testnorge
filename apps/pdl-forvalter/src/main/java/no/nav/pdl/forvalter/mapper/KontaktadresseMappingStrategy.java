package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.domain.KontaktadresseDTO;
import no.nav.pdl.forvalter.dto.PdlKontaktadresse;
import no.nav.pdl.forvalter.dto.PdlKontaktadresse.VegadresseForPost;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.domain.AdresseDTO.Master.FREG;
import static no.nav.pdl.forvalter.domain.AdresseDTO.Master.PDL;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class KontaktadresseMappingStrategy implements MappingStrategy {


    @Override
    public void register(MapperFactory factory) {

        factory.classMap(KontaktadresseDTO.class, PdlKontaktadresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(KontaktadresseDTO kildeAdresse, PdlKontaktadresse kontaktadresse, MappingContext context) {

                        if (isNotBlank(kontaktadresse.getAdresseIdentifikatorFraMatrikkelen())) {
                            kontaktadresse.setMaster(FREG);
                            kontaktadresse.setVegadresseForPost(
                                    mapperFacade.map(kildeAdresse.getVegadresse(), VegadresseForPost.class));
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