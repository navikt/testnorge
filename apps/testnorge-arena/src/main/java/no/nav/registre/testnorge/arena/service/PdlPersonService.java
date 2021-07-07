package no.nav.registre.testnorge.arena.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.PdlPersonConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.response.pdl.PdlPerson.Gruppe;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlPersonService {

    private final PdlPersonConsumer pdlPersonConsumer;

    public String getAktoerIdTilIdent(String ident) {
        var personData = pdlPersonConsumer.getPdlPerson(ident);
        if (personData != null && personData.getData().getHentIdenter() != null) {
            var identInfo = personData.getData().getHentIdenter().getIdenter();
            for (var info : identInfo) {
                if (info.getGruppe() == Gruppe.AKTORID) {
                    return info.getIdent();
                }
            }
        }
        log.info("Fant ikke aktørid for ident");
        return null;
    }

}
