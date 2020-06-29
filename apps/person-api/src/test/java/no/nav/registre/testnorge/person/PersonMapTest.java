package no.nav.registre.testnorge.person;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import no.nav.registre.testnorge.dto.person.v1.AdresseDTO;
import no.nav.registre.testnorge.dto.person.v1.PersonDTO;
import no.nav.registre.testnorge.person.consumer.dto.graphql.Bostedsadresse;
import no.nav.registre.testnorge.person.consumer.dto.graphql.Data;
import no.nav.registre.testnorge.person.consumer.dto.graphql.Folkeregisteridentifikator;
import no.nav.registre.testnorge.person.consumer.dto.graphql.HentPerson;
import no.nav.registre.testnorge.person.consumer.dto.graphql.Navn;
import no.nav.registre.testnorge.person.consumer.dto.graphql.PdlPerson;
import no.nav.registre.testnorge.person.consumer.dto.graphql.Vegadresse;
import no.nav.registre.testnorge.person.domain.Person;

@RunWith(SpringRunner.class)
public class PersonMapTest {

    @Test
    public void should_map_pdlperson_to_dtoPerson() {

        Navn pdlNavn = new Navn("Line", null, "Linesen");
        Folkeregisteridentifikator folkeregisteridentifikator = new Folkeregisteridentifikator("12345678912", null, null);
        Bostedsadresse bostedsadresse = new Bostedsadresse(new Vegadresse("Linegata", "12", "2650", null));
        PdlPerson graphqlResponse = new PdlPerson(null, new Data (new HentPerson(
                Collections.singletonList(pdlNavn),
                Collections.singletonList(bostedsadresse),
                Collections.singletonList(folkeregisteridentifikator))));

        Person domenePerson = new Person(graphqlResponse);
        PersonDTO dtoPerson = domenePerson.toDTO();

        PersonDTO expectedPerson = PersonDTO.builder()
                .fornavn("Line")
                .etternavn("Linesen")
                .ident("12345678912")
                .adresse(new AdresseDTO("Linegata 12", "2650", null, null))
                .build();

        assertThat(dtoPerson).isEqualTo(expectedPerson);
    }
}
