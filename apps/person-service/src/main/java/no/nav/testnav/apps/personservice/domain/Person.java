package no.nav.testnav.apps.personservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.apps.personservice.consumer.v1.pdl.FoedselsdatoDTO;
import no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql.Foedselsdato;
import no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql.Folkeregisteridentifikator;
import no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql.HentPerson;
import no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql.Navn;
import no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql.PdlPerson;
import no.nav.testnav.libs.dto.personservice.v1.PersonDTO;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

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
        Optional<Foedselsdato> foedsel = person.getFoedselsdato().stream().findFirst();

        ident = person.getFolkeregisteridentifikator()
                .stream()
                .findFirst()
                .map(Folkeregisteridentifikator::getIdentifikasjonsnummer)
                .orElse(null);
        foedselsdato = foedsel.map(Foedselsdato::getFoedselsdato).orElse(null);
        fornavn = navn.map(Navn::getFornavn).orElse(null);
        mellomnavn = navn.map(Navn::getMellomnavn).orElse(null);
        etternavn = navn.map(Navn::getEtternavn).orElse(null);
        adresse = person.getBostedsadresse()
                .stream()
                .findFirst()
                .map(value -> new Adresse(value.getVegadresse()))
                .orElse(null);
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

    public Optional<FoedselsdatoDTO> toFoedselDTO(String kilde) {
        if (foedselsdato == null) {
            return Optional.empty();
        }

        return Optional.of(
                FoedselsdatoDTO
                        .builder()
                        .foedselsaar(foedselsdato.getYear())
                        .foedselsdato(foedselsdato)
                        .kilde(kilde)
                        .master("FREG")
                        .build()
        );
    }
}
