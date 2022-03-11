package no.nav.identpool.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.consumers.TpsMessagingConsumer;
import no.nav.identpool.domain.Ident;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.dto.TpsStatusDTO;
import no.nav.identpool.exception.IdentAlleredeIBrukException;
import no.nav.identpool.exception.UgyldigPersonidentifikatorException;
import no.nav.identpool.providers.v1.support.MarkerBruktRequest;
import no.nav.identpool.repository.IdentRepository;
import no.nav.identpool.repository.WhitelistRepository;
import no.nav.identpool.util.IdentGeneratorUtil;
import no.nav.identpool.util.PersonidentUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.identpool.domain.Rekvireringsstatus.I_BRUK;
import static no.nav.identpool.domain.Rekvireringsstatus.LEDIG;
import static no.nav.identpool.util.PersonidentUtil.getIdentType;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentpoolService {

    private final IdentRepository identRepository;
    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final WhitelistRepository whitelistRepository;

    private static boolean isSyntetisk(String ident) {
        return ident.charAt(2) > '3';
    }

    public Boolean erLedig(String personidentifikator) {

        var ident = identRepository.findTopByPersonidentifikator(personidentifikator);

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

    public void markerBrukt(MarkerBruktRequest markerBruktRequest) {

        var ident = identRepository.findTopByPersonidentifikator(markerBruktRequest.getPersonidentifikator());

        if (nonNull(ident)) {
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

        } else if (ident.getRekvireringsstatus().equals(Rekvireringsstatus.LEDIG)) {
            ident.setRekvireringsstatus(I_BRUK);
            ident.setRekvirertAv(markerBruktRequest.getBruker());
            identRepository.save(ident);
            return;
        } else if (ident.getRekvireringsstatus().equals(I_BRUK)) {
            throw new IdentAlleredeIBrukException("Den etterspurte identen er allerede markert som i bruk.");
        }
        throw new IllegalStateException("Den etterspurte identen er ugyldig siden den hverken er markert som i bruk eller ledig.");
    }

    public List<String> markerBruktFlere(
            String rekvirertAv,
            List<String> identer
    ) {
        List<String> identerMarkertSomIBruk = new ArrayList<>(identer.size());
        Set<String> identerSomSkalSjekkes = new HashSet<>(identer.size());
        for (String id : identer) {
            Ident ident = identRepository.findTopByPersonidentifikator(id);
            if (ident == null) {
                identerSomSkalSjekkes.add(id);
            } else if (LEDIG.equals(ident.getRekvireringsstatus())) {
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

    public List<String> frigjoerIdenter(String rekvirertAv, List<String> identer) {

        var ledigeIdenter = new ArrayList<String>(identer.size());
        var fnrMedIdent = new HashMap<String, Ident>(identer.size());
        var identerSomSkalSjekkes = new HashSet<String>(identer.size());

        for (String id : identer) {
            Ident ident = identRepository.findTopByPersonidentifikator(id);
            if (ident != null) {
                if (LEDIG.equals(ident.getRekvireringsstatus())) {
                    ledigeIdenter.add(id);
                } else if (!ident.isFinnesHosSkatt() && rekvirertAv.equals(ident.getRekvirertAv())) {
                    fnrMedIdent.put(id, ident);
                    identerSomSkalSjekkes.add(id);
                }
            }
        }

        var tpsStatusDTOS = tpsMessagingConsumer.getIdenterStatuser(identerSomSkalSjekkes);
        return leggTilLedigeIdenterIMiljoer(ledigeIdenter, fnrMedIdent, tpsStatusDTOS);
    }

    public List<String> frigjoerLedigeIdenter(List<String> identer) {

        var ledigeIdenter = new ArrayList<String>(identer.size());
        var fnrMedIdent = new HashMap<String, Ident>(identer.size());
        var identerSomSkalSjekkes = new HashSet<String>(identer.size());

        for (String id : identer) {
            Ident ident = identRepository.findTopByPersonidentifikator(id);
            if (ident != null) {
                if (LEDIG.equals(ident.getRekvireringsstatus())) {
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

    public Ident lesInnhold(String personidentifikator) {

        return identRepository.findTopByPersonidentifikator(personidentifikator);
    }

    public void registrerFinnesHosSkatt(String personidentifikator) {

        if (Identtype.FNR.equals(getIdentType(personidentifikator))) {
            throw new UgyldigPersonidentifikatorException("personidentifikatoren er ikke et DNR");
        }

        var ident = identRepository.findTopByPersonidentifikator(personidentifikator);

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
                .collect(Collectors.toList());
    }

    public List<String> hentWhitelist() {

        var whiteFnrs = new ArrayList<String>();
        whitelistRepository.findAll().forEach(
                whitelist -> whiteFnrs.add(whitelist.getFnr())
        );
        return whiteFnrs;
    }
}
