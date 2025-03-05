package no.nav.testnav.dollysearchservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.libs.data.dollysearchservice.v1.PersonRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.PersonSearch;
import no.nav.testnav.libs.data.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus;
import no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class LegacyRequestMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(PersonSearch.class, PersonRequest.class)
                .customize(new CustomMapper<PersonSearch, PersonRequest>() {
                    @Override
                    public void mapAtoB(PersonSearch personSearch, PersonRequest personRequest, MappingContext context) {

                        Optional.ofNullable(personSearch.getAlder())
                                        .ifPresent(alder -> {
                                            personRequest.setAlderFom(nonNull(alder.getFra()) ?
                                                    alder.getFra().intValue() : null);
                                            personRequest.setAlderTom(nonNull(alder.getTil()) ?
                                                    alder.getTil().intValue() : null);
                                        });

                        Optional.ofNullable(personSearch.getPersonstatus())
                                .ifPresent(personstatus ->
                                        personRequest.setPersonStatus(FolkeregisterPersonstatus.valueOf(personstatus.getStatus())));

                        Optional.ofNullable(personSearch.getKjoenn())
                                .ifPresent(kjoenn -> personRequest.setKjoenn(KjoennDTO.Kjoenn.valueOf(kjoenn)));

                        Optional.ofNullable(personSearch.getNasjonalitet())
                                .ifPresent(nasjonalitet -> personRequest.setStatsborgerskap(nasjonalitet.getStatsborgerskap()));

                        Optional.ofNullable(personSearch.getAdresser())
                                .ifPresent(adresser -> {
                                    personRequest.setAdresse(PersonRequest.AdresseRequest.builder()
                                                    .harUtenlandsadresse(isNotBlank(adresser.getHarUtenlandskAdresse()))
                                                    .harKontaktadresse(isNotBlank(adresser.getHarKontaktadresse()))
                                                    .harOppholdsadresse(isNotBlank(adresser.getHarOppholdsadresse()))
                                            .build());
                                });
                    }
                }).register();
    }
}
