package no.nav.brregstub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import no.nav.brregstub.api.common.RsOrganisasjon;
import no.nav.brregstub.api.v1.RolleoversiktTo;
import no.nav.brregstub.database.repository.HentRolleRepository;
import no.nav.brregstub.database.repository.RolleoversiktRepository;
import no.nav.brregstub.mapper.HentRolleMapper;
import no.nav.brregstub.mapper.RolleoversiktMapper;
import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BrregService {

    public static final int PERSON_IKKE_FUNNET = 180;
    public static final int ENHET_IKKE_FUNNET = 100;

    private final RolleoversiktRepository rolleoversiktRepository;

    private final HentRolleRepository hentRolleRepository;

    private final ObjectMapper objectMapper;


    @SneakyThrows
    public Grunndata hentRoller(String orgnummer) {
        var orgNr = Integer.parseInt(orgnummer);
        var hentRolle = hentRolleRepository.findByOrgnr(orgNr);
        if (hentRolle.isPresent()) {
            var fromDb = objectMapper.readValue(hentRolle.get().getJson(), RsOrganisasjon.class);
            return HentRolleMapper.map(fromDb);
        }

        var organisasjonIkkeFunnet = new RsOrganisasjon();
        organisasjonIkkeFunnet.setOrgnr(orgNr);
        organisasjonIkkeFunnet.setHovedstatus(1);
        organisasjonIkkeFunnet.getUnderstatuser().add(ENHET_IKKE_FUNNET);
        return HentRolleMapper.map(organisasjonIkkeFunnet);
    }

    @SneakyThrows
    public no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata hentRolleutskrift(String requestId) {
        var rolleutskrift = rolleoversiktRepository.findByIdent(requestId);
        if (rolleutskrift.isPresent()) {
            var d = objectMapper.readValue(rolleutskrift.get().getJson(), RolleoversiktTo.class);
            return RolleoversiktMapper.map(d);
        }

        var personIkkeFunnet = new RolleoversiktTo();
        personIkkeFunnet.setFnr(requestId);
        personIkkeFunnet.setHovedstatus(1);
        personIkkeFunnet.getUnderstatuser().add(PERSON_IKKE_FUNNET);
        return RolleoversiktMapper.map(personIkkeFunnet);
    }
}
