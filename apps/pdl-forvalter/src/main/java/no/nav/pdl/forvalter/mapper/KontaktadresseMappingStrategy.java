package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.dto.PdlKontaktadresse;
import no.nav.pdl.forvalter.dto.PdlKontaktadresse.VegadresseForPost;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;
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
                            kontaktadresse.setMaster(DbVersjonDTO.Master.FREG);
                            kontaktadresse.setVegadresseForPost(
                                    mapperFacade.map(kildeAdresse.getVegadresse(), VegadresseForPost.class));
                            kontaktadresse.setVegadresse(null);
                        } else if (nonNull(kontaktadresse.getVegadresse())) {
                            kontaktadresse.setMaster(DbVersjonDTO.Master.PDL);
                        }
                    }
                })
                .byDefault()
                .register();
    }
}