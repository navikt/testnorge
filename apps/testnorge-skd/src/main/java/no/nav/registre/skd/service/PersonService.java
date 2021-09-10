package no.nav.registre.skd.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.consumer.PersonConsumer;
import no.nav.testnav.libs.dto.person.v1.PersonDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.skd.utils.DatoFraIdentUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonConsumer personConsumer;

    @Value("${mini.norge.avspillergruppe.id}")
    private String miniNorgeAvspillergruppeId;

    @Value("${mini.norge.miljoe}")
    private String miniNorgeMiljoe;

    void leggTilIdenterIPdl(List<String> identer, Long avspillergruppeId, String miljoe) {
        if (!identer.isEmpty()) {
            log.info("Legger til {} ident(er) i PDL", identer.size());
            String kilde = null;
            if (miniNorgeAvspillergruppeId.equals(avspillergruppeId.toString()) && miniNorgeMiljoe.equals(miljoe)) {
                kilde = "MiniNorge";
            }
            for (var ident : identer) {
                try {
                    var birthDate = DatoFraIdentUtils.getDato(ident);
                    var person = PersonDTO.builder().ident(ident).foedselsdato(birthDate).build();
                    personConsumer.createPerson(person, kilde);
                } catch (Exception e) {
                    log.error("Kunne ikke legge følgende ident til i PDL: {}", ident, e);
                }
            }
        }
    }

}
