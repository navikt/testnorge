package no.nav.testnav.apps.personservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import no.nav.testnav.apps.personservice.consumer.dto.pdl.FoedselDTO;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql.Foedsel;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql.Folkeregisteridentifikator;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql.HentPerson;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql.Navn;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql.PdlPerson;
import no.nav.testnav.apps.personservice.consumer.dto.tpsf.TpsPerson;
import no.nav.testnav.libs.dto.personservice.v1.PersonDTO;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    private String ident;
    private LocalDate foedselsdato;
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
    private Adresse adresse;
    private Set<String> tags;

    public Person(PersonDTO dto) {
        ident = dto.getIdent();
        foedselsdato = dto.getFoedselsdato();
        fornavn = dto.getFornavn();
        mellomnavn = dto.getMellomnavn();
        etternavn = dto.getEtternavn();
        adresse = dto.getAdresse() != null ? new Adresse(dto.getAdresse()) : null;
        tags = dto.getTags();
    }

    public Person(PdlPerson pdlPerson) {
        HentPerson person = pdlPerson.getData().getHentPerson();
        Optional<Navn> navn = person.getNavn().stream().findFirst();
        Optional<Foedsel> foedsel = person.getFoedsel().stream().findFirst();

        ident = person.getFolkeregisteridentifikator()
                .stream()
                .findFirst()
                .map(Folkeregisteridentifikator::getIdentifikasjonsnummer)
                .orElse(null);
        foedselsdato = foedsel.map(Foedsel::getFoedselsdato).orElse(null);
        fornavn = navn.map(Navn::getFornavn).orElse(null);
        mellomnavn = navn.map(Navn::getMellomnavn).orElse(null);
        etternavn = navn.map(Navn::getEtternavn).orElse(null);
        adresse = person.getBostedsadresse()
                .stream()
                .findFirst()
                .map(value -> new Adresse(value.getVegadresse()))
                .orElse(null);
    }

    public Person(TpsPerson tpsPerson) {
        ident = tpsPerson.getIdent();
        fornavn = tpsPerson.getFornavn();
        mellomnavn = tpsPerson.getMellomnavn();
        etternavn = tpsPerson.getEtternavn();
        foedselsdato = tpsPerson.getFoedselsdato().toLocalDate();
        adresse = !tpsPerson.getBoadresse().isEmpty() ? new Adresse(tpsPerson.getBoadresse().get(0)) : null;
    }

    public PersonDTO toDTO() {
        return PersonDTO.builder()
                .ident(ident)
                .foedselsdato(foedselsdato)
                .fornavn(fornavn)
                .mellomnavn(mellomnavn)
                .etternavn(etternavn)
                .adresse(adresse != null ? adresse.toDto() : null)
                .tags(tags)
                .build();
    }

    public Optional<FoedselDTO> toFoedselDTO(String kilde) {
        if (foedselsdato == null) {
            return Optional.empty();
        }

        return Optional.of(
                FoedselDTO
                        .builder()
                        .foedselsaar(foedselsdato.getYear())
                        .foedselsdato(foedselsdato)
                        .kilde(kilde)
                        .master("FREG")
                        .build()
        );
    }
}
