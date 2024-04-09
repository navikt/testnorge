package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DollyRequest2MalBestillingMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsDollyBestillingRequest.class, RsDollyBestillingRequest.class)
                .mapNulls(false)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsDollyBestillingRequest rsDollyBestillingRequest, RsDollyBestillingRequest bestillingMal, MappingContext context) {

                        bestillingMal.setEnvironments(Set.of("q1"));

                    }
                })
                .byDefault()
                .register();

        factory.classMap(Arenadata.class, Arenadata.class)
                .mapNulls(false)
                .field("aktiveringDato", "aktiveringDato")
                .field("arenaBrukertype", "arenaBrukertype")
                .field("kvalifiseringsgruppe", "kvalifiseringsgruppe")
                .field("automatiskInnsendingAvMeldekort", "automatiskInnsendingAvMeldekort")
                .field("inaktiveringDato", "inaktiveringDato")
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Arenadata arenadata, Arenadata akkumulert, MappingContext context) {
                        akkumulert.getAap115().addAll(arenadata.getAap115());
                        akkumulert.getAap().addAll(arenadata.getAap());
                        akkumulert.getDagpenger().addAll(arenadata.getDagpenger());
                    }
                })
                .register();

        factory.classMap(PdlPersondata.class, PdlPersondata.class)
                .mapNulls(false)
                .byDefault()
                .register();

        factory.classMap(PersonDTO.class, PersonDTO.class)
                .mapNulls(false)
                .field("ident", "ident")
                .field("identtype", "identtype")
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PersonDTO personDTO, PersonDTO akkumulertDTO, MappingContext context) {
                        akkumulertDTO.setIdent(personDTO.getIdent());
                        akkumulertDTO.setIdenttype(personDTO.getIdenttype());
                        akkumulertDTO.setBostedsadresse(personDTO.getBostedsadresse());
                        akkumulertDTO.setNyident(personDTO.getNyident());
                        akkumulertDTO.getBostedsadresse().addAll(personDTO.getBostedsadresse());
                        akkumulertDTO.getKontaktadresse().addAll(personDTO.getKontaktadresse());
                        akkumulertDTO.getOppholdsadresse().addAll(personDTO.getOppholdsadresse());
                        akkumulertDTO.getDeltBosted().addAll(personDTO.getDeltBosted());
                        akkumulertDTO.getForelderBarnRelasjon().addAll(personDTO.getForelderBarnRelasjon());
                        akkumulertDTO.getAdressebeskyttelse().addAll(personDTO.getAdressebeskyttelse());
                        akkumulertDTO.getFoedsel().addAll(personDTO.getFoedsel());
                        akkumulertDTO.getDoedsfall().addAll(personDTO.getDoedsfall());
                        akkumulertDTO.getKjoenn().addAll(personDTO.getKjoenn());
                        akkumulertDTO.getNavn().addAll(personDTO.getNavn());
                        akkumulertDTO.getFolkeregisterPersonstatus().addAll(personDTO.getFolkeregisterPersonstatus());
                        akkumulertDTO.getFullmakt().addAll(personDTO.getFullmakt());
                        akkumulertDTO.getStatsborgerskap().addAll(personDTO.getStatsborgerskap());
                        akkumulertDTO.getOpphold().addAll(personDTO.getOpphold());
                        akkumulertDTO.getSivilstand().addAll(personDTO.getSivilstand());
                        akkumulertDTO.getTelefonnummer().addAll(personDTO.getTelefonnummer());
                        akkumulertDTO.getInnflytting().addAll(personDTO.getInnflytting());
                        akkumulertDTO.getUtflytting().addAll(personDTO.getUtflytting());
                        akkumulertDTO.getForeldreansvar().addAll(personDTO.getForeldreansvar());
                        akkumulertDTO.getVergemaal().addAll(personDTO.getVergemaal());
                        akkumulertDTO.getKontaktinformasjonForDoedsbo().addAll(personDTO.getKontaktinformasjonForDoedsbo());
                        akkumulertDTO.getUtenlandskIdentifikasjonsnummer().addAll(personDTO.getUtenlandskIdentifikasjonsnummer());
                        akkumulertDTO.getFalskIdentitet().addAll(personDTO.getFalskIdentitet());
                        akkumulertDTO.getTilrettelagtKommunikasjon().addAll(personDTO.getTilrettelagtKommunikasjon());
                        akkumulertDTO.getDoedfoedtBarn().addAll(personDTO.getDoedfoedtBarn());
                        akkumulertDTO.getSikkerhetstiltak().addAll(personDTO.getSikkerhetstiltak());
                    }
                })
                .register();
    }
}
