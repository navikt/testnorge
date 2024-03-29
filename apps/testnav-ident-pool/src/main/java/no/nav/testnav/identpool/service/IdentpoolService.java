package no.nav.testnav.identpool.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.exception.IdentAlleredeIBrukException;
import no.nav.testnav.identpool.exception.UgyldigPersonidentifikatorException;
import no.nav.testnav.identpool.providers.v1.support.MarkerBruktRequest;
import no.nav.testnav.identpool.repository.IdentRepository;
import no.nav.testnav.identpool.repository.WhitelistRepository;
import no.nav.testnav.identpool.util.IdentGeneratorUtil;
import no.nav.testnav.identpool.util.PersonidentUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.identpool.domain.Rekvireringsstatus.I_BRUK;
import static no.nav.testnav.identpool.domain.Rekvireringsstatus.LEDIG;
import static no.nav.testnav.identpool.util.PersonidentUtil.getIdentType;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentpoolService {

    private final IdentRepository identRepository;
    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final WhitelistRepository whitelistRepository;

    public Boolean erLedig(String personidentifikator) {

        var ident = identRepository.findByPersonidentifikator(personidentifikator);

        if (nonNull(ident)) {
            return ident.getRekvireringsstatus().equals(Rekvireringsstatus.LEDIG);

        } else {
            var tpsStatus = tpsMessagingConsumer.getIdenterStatuser(Collections.singleton(personidentifikator));
            return tpsStatus.stream()
                    .map(status -> !status.isInUse())
                    .findFirst()
                    .orElseThrow(() -> new HttpServerErrorException(INTERNAL_SERVER_ERROR,
                            "Fikk ikke riktig antall statuser tilbake på metodekall til checkIdentsInTps"));
        }
    }

    public List<TpsStatusDTO> finnesIProd(Set<String> identer) {

        var tpsStatus = tpsMessagingConsumer.getIdenterProdStatus(identer);
        return tpsStatus.stream()
                .filter(TpsStatusDTO::isInUse)
                .toList();
    }

    public void markerBrukt(MarkerBruktRequest markerBruktRequest) {

        var ident = identRepository.findByPersonidentifikator(markerBruktRequest.getPersonidentifikator());

        if (isNull(ident)) {
            String personidentifikator = markerBruktRequest.getPersonidentifikator();

            var newIdent = Ident.builder()
                    .identtype(getIdentType(personidentifikator))
                    .personidentifikator(personidentifikator)
                    .rekvireringsstatus(I_BRUK)
                    .rekvirertAv(markerBruktRequest.getBruker())
                    .finnesHosSkatt(false)
                    .kjoenn(PersonidentUtil.getKjonn(personidentifikator))
                    .foedselsdato(PersonidentUtil.toBirthdate(personidentifikator))
                    .syntetisk(isSyntetisk(personidentifikator))
                    .build();
            identRepository.save(newIdent);
            return;

        } else if (Rekvireringsstatus.LEDIG == ident.getRekvireringsstatus()) {
            ident.setRekvireringsstatus(I_BRUK);
            ident.setRekvirertAv(markerBruktRequest.getBruker());
            identRepository.save(ident);
            return;

        } else if (ident.getRekvireringsstatus().equals(I_BRUK)) {
            throw new IdentAlleredeIBrukException("Den etterspurte identen er allerede markert som i bruk.");
        }
        throw new IllegalStateException("Den etterspurte identen er ugyldig siden den hverken er markert som i bruk eller ledig.");
    }

    public List<String> markerBruktFlere(String rekvirertAv, List<String> identer) {

        var identerMarkertSomIBruk = new ArrayList<String>(identer.size());
        var identerSomSkalSjekkes = new HashSet<String>(identer.size());

        for (String id : identer) {
            Ident ident = identRepository.findByPersonidentifikator(id);

            if (isNull(ident)) {
                identerSomSkalSjekkes.add(id);

            } else if (Rekvireringsstatus.LEDIG == ident.getRekvireringsstatus()) {
                ident.setRekvireringsstatus(I_BRUK);
                ident.setRekvirertAv(rekvirertAv);
                identRepository.save(ident);
                identerMarkertSomIBruk.add(id);
            }
        }

        Set<TpsStatusDTO> tpsStatusDTOS = tpsMessagingConsumer.getIdenterStatuser(identerSomSkalSjekkes);

        for (TpsStatusDTO tpsStatusDTO : tpsStatusDTOS) {
            if (!tpsStatusDTO.isInUse()) {
                String id = tpsStatusDTO.getIdent();
                identRepository.save(Ident.builder()
                        .identtype(getIdentType(id))
                        .personidentifikator(id)
                        .rekvireringsstatus(I_BRUK)
                        .rekvirertAv(rekvirertAv)
                        .finnesHosSkatt(false)
                        .kjoenn(PersonidentUtil.getKjonn(id))
                        .foedselsdato(PersonidentUtil.toBirthdate(id))
                        .syntetisk(isSyntetisk(id))
                        .build());
                identerMarkertSomIBruk.add(id);
            }
        }

        return identerMarkertSomIBruk;
    }

    @Transactional
    public List<String> frigjoerIdenter(List<String> identer) {

        var personidentifikatore = identRepository.findByPersonidentifikatorIn(identer);

        personidentifikatore
                .forEach(personidentifikator -> {
                    if (!personidentifikator.isFinnesHosSkatt()) {

                        personidentifikator.setRekvireringsstatus(LEDIG);
                        personidentifikator.setRekvirertAv(null);
                    }
                });

        var frigjorteIdenter = personidentifikatore.stream()
                .filter(personidentifikator -> LEDIG.equals(personidentifikator.getRekvireringsstatus()))
                .map(Ident::getPersonidentifikator)
                .toList();

        log.info("Frigjort identer: " + String.join(", ", frigjorteIdenter));

        return frigjorteIdenter;
    }

    public List<String> frigjoerLedigeIdenter(List<String> identer) {

        var ledigeIdenter = new ArrayList<String>(identer.size());
        var fnrMedIdent = new HashMap<String, Ident>(identer.size());
        var identerSomSkalSjekkes = new HashSet<String>(identer.size());

        for (String id : identer) {
            Ident ident = identRepository.findByPersonidentifikator(id);
            if (nonNull(ident)) {
                if (Rekvireringsstatus.LEDIG == ident.getRekvireringsstatus()) {
                    ledigeIdenter.add(id);
                } else if (!ident.isFinnesHosSkatt()) {
                    fnrMedIdent.put(id, ident);
                    identerSomSkalSjekkes.add(id);
                }
            }
        }
        var tpsStatusDTOS = tpsMessagingConsumer.getIdenterStatuser(identerSomSkalSjekkes);
        return leggTilLedigeIdenterIMiljoer(ledigeIdenter, fnrMedIdent, tpsStatusDTOS);
    }

    public Ident lesInnhold(String personidentifikator) {

        return identRepository.findByPersonidentifikator(personidentifikator);
    }

    public void registrerFinnesHosSkatt(String personidentifikator) {

        if (Identtype.FNR.equals(getIdentType(personidentifikator))) {
            throw new UgyldigPersonidentifikatorException("personidentifikatoren er ikke et DNR");
        }

        var ident = identRepository.findByPersonidentifikator(personidentifikator);

        if (nonNull(ident)) {
            ident.setFinnesHosSkatt(true);
            ident.setRekvireringsstatus(I_BRUK);
            ident.setRekvirertAv("DREK");

        } else {
            ident = IdentGeneratorUtil.createIdent(personidentifikator, I_BRUK, "DREK");
            ident.setFinnesHosSkatt(true);
        }
        ident.setSyntetisk(ident.isSyntetisk());
        identRepository.save(ident);
    }

    public List<String> hentLedigeFNRFoedtMellom(LocalDate from, LocalDate to) {

        var identer = identRepository.findByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(from, to,
                Identtype.FNR, LEDIG, false);
        return identer.stream()
                .map(Ident::getPersonidentifikator)
                .toList();
    }

    public List<String> hentWhitelist() {

        var whiteFnrs = new ArrayList<String>();
        whitelistRepository.findAll().forEach(
                whitelist -> whiteFnrs.add(whitelist.getFnr())
        );
        return whiteFnrs;
    }

    private List<String> leggTilLedigeIdenterIMiljoer(List<String> ledigeIdenter, Map<String,
            Ident> fnrMedIdent, Set<TpsStatusDTO> tpsStatuser) {

        for (TpsStatusDTO tpsStatusDTO : tpsStatuser) {
            if (!tpsStatusDTO.isInUse()) {
                Ident ident = fnrMedIdent.get(tpsStatusDTO.getIdent());
                ident.setRekvireringsstatus(LEDIG);
                ident.setRekvirertAv(null);
                identRepository.save(ident);
                ledigeIdenter.add(tpsStatusDTO.getIdent());
            }
        }
        return ledigeIdenter;
    }

    private static boolean isSyntetisk(String ident) {
        return ident.charAt(2) > '3';
    }
}
