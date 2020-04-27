package no.nav.brregstub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import no.nav.brregstub.api.OrganisasjonTo;
import no.nav.brregstub.api.RolleutskriftTo;
import no.nav.brregstub.database.repository.HentRolleRepository;
import no.nav.brregstub.database.repository.RolleutskriftRepository;
import no.nav.brregstub.mapper.HentRolleMapper;
import no.nav.brregstub.mapper.RolleutskriftMapper;
import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BrregService {

    public static final int PERSON_IKKE_FUNNET = 180;
    public static final int ENHET_IKKE_FUNNET = 100;

    private final RolleutskriftRepository rolleutskriftRepository;

    private final HentRolleRepository hentRolleRepository;

    private final ObjectMapper objectMapper;


    @SneakyThrows
    public Grunndata hentRoller(String orgnummer) {
        var orgNr = Integer.parseInt(orgnummer);
        var hentRolle = hentRolleRepository.findByOrgnr(orgNr);
        if (hentRolle.isPresent()) {
            var fromDb = objectMapper.readValue(hentRolle.get().getJson(), OrganisasjonTo.class);
            return HentRolleMapper.map(fromDb);
        }

        var organisasjonIkkeFunnet = new OrganisasjonTo();
        organisasjonIkkeFunnet.setOrgnr(orgNr);
        organisasjonIkkeFunnet.setHovedstatus(1);
        organisasjonIkkeFunnet.getUnderstatuser().add(ENHET_IKKE_FUNNET);
        return HentRolleMapper.map(organisasjonIkkeFunnet);
    }

    @SneakyThrows
    public no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata hentRolleutskrift(String requestId) {
        var rolleutskrift = rolleutskriftRepository.findByIdent(requestId);
        if (rolleutskrift.isPresent()) {
            var d = objectMapper.readValue(rolleutskrift.get().getJson(), RolleutskriftTo.class);
            return RolleutskriftMapper.map(d);
        }

        var personIkkeFunnet = new RolleutskriftTo();
        personIkkeFunnet.setFnr(requestId);
        personIkkeFunnet.setHovedstatus(1);
        personIkkeFunnet.getUnderstatuser().add(PERSON_IKKE_FUNNET);
        return RolleutskriftMapper.map(personIkkeFunnet);
    }
}
