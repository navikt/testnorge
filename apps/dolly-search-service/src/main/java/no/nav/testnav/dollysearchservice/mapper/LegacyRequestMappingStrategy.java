package no.nav.testnav.dollysearchservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.libs.dto.dollysearchservice.v1.PersonRequest;
import no.nav.testnav.libs.dto.dollysearchservice.v1.legacy.PersonSearch;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component
public class LegacyRequestMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(PersonSearch.class, PersonRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PersonSearch personSearch, PersonRequest personRequest, MappingContext context) {

                        Optional.ofNullable(personSearch.getAlder())
                                .ifPresent(alder -> {
                                    personRequest.setAlderFom(nonNull(alder.getFra()) ? alder.getFra() : null);
                                    personRequest.setAlderTom(nonNull(alder.getTil()) ? alder.getTil() : null);
                                });

                        Optional.ofNullable(personSearch.getPersonstatus())
                                .ifPresent(personstatus ->
                                        personRequest.setPersonStatus(FolkeregisterPersonstatus.valueOf(personstatus.getStatus())));

                        Optional.ofNullable(personSearch.getKjoenn())
                                .ifPresent(kjoenn -> personRequest.setKjoenn(KjoennDTO.Kjoenn.valueOf(kjoenn)));

                        Optional.ofNullable(personSearch.getNasjonalitet())
                                .ifPresent(nasjonalitet -> personRequest.setStatsborgerskap(nasjonalitet.getStatsborgerskap()));

                        Optional.ofNullable(personSearch.getAdresser())
                                .ifPresent(adresser ->
                                        personRequest.setAdresse(PersonRequest.AdresseRequest.builder()
                                                .harUtenlandsadresse(isTrue(adresser.getHarUtenlandskAdresse()))
                                                .harKontaktadresse(isTrue(adresser.getHarKontaktadresse()))
                                                .harOppholdsadresse(isTrue(adresser.getHarOppholdsadresse()))
                                                .build()));

                        personRequest.setErLevende(isTrue(personSearch.getKunLevende()));

                        Optional.ofNullable(personSearch.getRelasjoner())
                                .ifPresent(relasjoner ->
                                        personRequest.setHarBarn(isTrue(relasjoner.getHarBarn())));
                    }
                }).register();
    }
}
