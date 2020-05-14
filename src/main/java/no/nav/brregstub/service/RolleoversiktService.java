package no.nav.brregstub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import no.nav.brregstub.api.common.Egenskap;
import no.nav.brregstub.api.common.RsNavn;
import no.nav.brregstub.api.common.RsOrganisasjon;
import no.nav.brregstub.api.common.RsPersonOgRolle;
import no.nav.brregstub.api.v1.RolleoversiktTo;
import no.nav.brregstub.api.common.RsSamendring;
import no.nav.brregstub.api.v2.RsRolle;
import no.nav.brregstub.api.v2.RsRolleoversikt;
import no.nav.brregstub.database.domene.HentRolle;
import no.nav.brregstub.database.domene.Rolleoversikt;
import no.nav.brregstub.database.repository.HentRolleRepository;
import no.nav.brregstub.database.repository.RolleoversiktRepository;
import no.nav.brregstub.mapper.HentRolleMapper;
import no.nav.brregstub.mapper.RolleoversiktMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class RolleoversiktService {

    private final RolleoversiktRepository rolleoversiktRepository;
    private final HentRolleRepository rolleRepository;

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public Optional<RolleoversiktTo> opprettRolleoversiktV1(RolleoversiktTo rolleoversiktTo) {
        RolleoversiktMapper.map(rolleoversiktTo); //Sjekker om object kan mappes

        var rolleoversikt = rolleoversiktRepository.findByIdent(rolleoversiktTo.getFnr())
                .orElseGet(() -> {
                    var rolleutskrift = new Rolleoversikt();
                    rolleutskrift.setIdent(rolleoversiktTo.getFnr());
                    return rolleutskrift;
                });

        rolleoversikt.setJson(objectMapper.writeValueAsString(rolleoversiktTo));

        rolleoversiktRepository.save(rolleoversikt);
        return Optional.of(rolleoversiktTo);
    }

    @SneakyThrows
    public Optional<RsRolleoversikt> opprettRolleoversiktV2(RsRolleoversikt rsRolleoversikt) {
        RolleoversiktMapper.map(rsRolleoversikt); //Sjekker om object kan mappes

        setRollebeskrivelse(rsRolleoversikt);

        var organisasjoner = byggOrganisasjoner(rsRolleoversikt);

        var rolleoversikt = rolleoversiktRepository.findByIdent(rsRolleoversikt.getFnr())
                .orElseGet(() -> {
                    var rolleutskrift = new Rolleoversikt();
                    rolleutskrift.setIdent(rsRolleoversikt.getFnr());
                    return rolleutskrift;
                });

        rolleoversikt.setJson(objectMapper.writeValueAsString(rsRolleoversikt));

        rolleoversiktRepository.save(rolleoversikt);

        organisasjoner.forEach(this::lagreEllerOppdaterOrganisasjon);

        return Optional.of(rsRolleoversikt);
    }

    @SneakyThrows
    public Optional<RolleoversiktTo> hentRolleoversiktV1(String ident) {
        var rolleoversikt = rolleoversiktRepository.findByIdent(ident);

        if (rolleoversikt.isPresent()) {
            var to = objectMapper.readValue(rolleoversikt.get().getJson(), RolleoversiktTo.class);
            return Optional.of(to);
        }
        return Optional.empty();
    }

    @SneakyThrows
    public Optional<RsRolleoversikt> hentRolleoversiktV2(String ident) {
        var rolleoversikt = rolleoversiktRepository.findByIdent(ident);

        if (rolleoversikt.isPresent()) {
            var rsRolleoversikt = objectMapper.readValue(rolleoversikt.get().getJson(), RsRolleoversikt.class);
            return Optional.of(rsRolleoversikt);
        }
        return Optional.empty();
    }

    public void slettRolleoversikt(String ident) {
        rolleoversiktRepository.findByIdent(ident).ifPresent(rolleoversiktRepository::delete);
    }

    private List<RsOrganisasjon> byggOrganisasjoner(RsRolleoversikt rsRolleoversikt) {
        List<RsOrganisasjon> organisasjoner = new ArrayList<>();
        for (var enhet : rsRolleoversikt.getEnheter()) {
            var orgNr = enhet.getOrgNr();
            var registreringsdato = enhet.getRegistreringsdato();
            var personRoller = enhet.getPersonRolle();

            var organisasjon = organisasjoner.stream().filter(eksisterendeOrganisasjon -> orgNr.equals(eksisterendeOrganisasjon.getOrgnr())).findFirst().orElse(null);

            if (organisasjon == null) {
                organisasjon = RsOrganisasjon.builder()
                        .orgnr(orgNr)
                        .hovedstatus(rsRolleoversikt.getHovedstatus())
                        .understatuser(rsRolleoversikt.getUnderstatuser())
                        .registreringsdato(registreringsdato)
                        .build();
            }

            for (var personRolle : personRoller) {
                var egenskap = personRolle.getEgenskap();
                if (egenskap.equals(Egenskap.Deltager)) {
                    organisasjon.setDeltakere(oppdaterSamendringsliste(organisasjon.getDeltakere(), enhet, rsRolleoversikt.getFnr(), rsRolleoversikt.getNavn(), personRolle.isFratraadt()));
                } else if (egenskap.equals(Egenskap.Komplementar)) {
                    organisasjon.setKomplementar(oppdaterSamendringsliste(organisasjon.getKomplementar(), enhet, rsRolleoversikt.getFnr(), rsRolleoversikt.getNavn(), personRolle.isFratraadt()));
                } else if (egenskap.equals(Egenskap.Kontaktperson)) {
                    organisasjon.setKontaktperson(oppdaterSamendringsliste(organisasjon.getKontaktperson(), enhet, rsRolleoversikt.getFnr(), rsRolleoversikt.getNavn(), personRolle.isFratraadt()));
                } else if (egenskap.equals(Egenskap.Sameier)) {
                    organisasjon.setSameier(oppdaterSamendringsliste(organisasjon.getSameier(), enhet, rsRolleoversikt.getFnr(), rsRolleoversikt.getNavn(), personRolle.isFratraadt()));
                } else if (egenskap.equals(Egenskap.Styre)) {
                    organisasjon.setStyre(oppdaterSamendringsliste(organisasjon.getStyre(), enhet, rsRolleoversikt.getFnr(), rsRolleoversikt.getNavn(), personRolle.isFratraadt()));
                }
            }

            organisasjoner.add(organisasjon);
        }

        return organisasjoner;
    }

    private RsSamendring oppdaterSamendringsliste(
            RsSamendring samendring,
            RsRolle enhet,
            String fnr,
            RsNavn navn,
            boolean fratraadt
    ) {
        if (samendring == null) {
            samendring = new RsSamendring();
            samendring.setRegistringsDato(enhet.getRegistreringsdato());
        }
        if (samendring.getRoller() == null) {
            samendring.setRoller(new ArrayList<>());
        }
        samendring.getRoller().add(RsPersonOgRolle.builder()
                .fodselsnr(fnr)
                .rolle(enhet.getRolle().name())
                .rollebeskrivelse(enhet.getRolle().getBeskrivelse())
                .fornavn(navn.getNavn1())
                .slektsnavn(navn.getNavn3())
                .adresse1(enhet.getForretningsAdresse() != null ? enhet.getForretningsAdresse().getAdresse1() : null)
                .postnr(enhet.getForretningsAdresse() != null ? enhet.getForretningsAdresse().getPostnr() : null)
                .poststed(enhet.getForretningsAdresse() != null ? enhet.getForretningsAdresse().getPoststed() : null)
                .fratraadt(fratraadt)
                .build());
        return samendring;
    }

    private void lagreEllerOppdaterOrganisasjon(RsOrganisasjon nyOrganisasjon) {
        HentRolleMapper.map(nyOrganisasjon); //sjekker om input kan mappes før lagring

        var rolleutskrift = rolleRepository.findByOrgnr(nyOrganisasjon.getOrgnr())
                .orElseGet(() -> {
                    var hentRolle = new HentRolle();
                    hentRolle.setOrgnr(nyOrganisasjon.getOrgnr());
                    return hentRolle;
                });

        try {
            var json = rolleutskrift.getJson();
            if (json == null) {
                log.info("Organisasjon eksisterer ikke fra før. Må opprettes.");
                rolleutskrift.setJson(objectMapper.writeValueAsString(nyOrganisasjon));
            } else {
                var eksisterendeOrganisasjon = objectMapper.readValue(json, RsOrganisasjon.class);
                log.info("Oppdaterer organisasjon med orgnummer {}", nyOrganisasjon.getOrgnr());
                oppdaterEksisterendeOrganisasjon(eksisterendeOrganisasjon, nyOrganisasjon);
                rolleutskrift.setJson(objectMapper.writeValueAsString(eksisterendeOrganisasjon));
            }
            rolleRepository.save(rolleutskrift);
        } catch (JsonProcessingException e) {
            log.error("Kunne ikke lagre organisasjon med orgnummer {}", nyOrganisasjon.getOrgnr(), e);
        }
    }

    private void oppdaterEksisterendeOrganisasjon(
            RsOrganisasjon eksisterende,
            RsOrganisasjon ny
    ) {
        if (ny.getKontaktperson() != null) {
            if (eksisterende.getKontaktperson() != null) {
                leggTilHvisIkkeDuplikat("kontaktperson", eksisterende.getOrgnr(), eksisterende.getKontaktperson().getRoller(), ny.getKontaktperson().getRoller());
            } else {
                eksisterende.setKontaktperson(ny.getKontaktperson());
            }
        }
        if (ny.getSameier() != null) {
            if (eksisterende.getSameier() != null) {
                leggTilHvisIkkeDuplikat("sameier", eksisterende.getOrgnr(), eksisterende.getSameier().getRoller(), ny.getSameier().getRoller());
            } else {
                eksisterende.setSameier(ny.getSameier());
            }
        }
        if (ny.getStyre() != null) {
            if (eksisterende.getStyre() != null) {
                leggTilHvisIkkeDuplikat("styre", eksisterende.getOrgnr(), eksisterende.getStyre().getRoller(), ny.getStyre().getRoller());
            } else {
                eksisterende.setStyre(ny.getStyre());
            }
        }
        if (ny.getKomplementar() != null) {
            if (eksisterende.getKomplementar() != null) {
                leggTilHvisIkkeDuplikat("komplementar", eksisterende.getOrgnr(), eksisterende.getKomplementar().getRoller(), ny.getKomplementar().getRoller());
            } else {
                eksisterende.setKomplementar(ny.getKomplementar());
            }
        }
        if (ny.getDeltakere() != null) {
            if (eksisterende.getDeltakere() != null) {
                leggTilHvisIkkeDuplikat("deltaker", eksisterende.getOrgnr(), eksisterende.getDeltakere().getRoller(), ny.getDeltakere().getRoller());
            } else {
                eksisterende.setDeltakere(ny.getDeltakere());
            }
        }
    }

    private void leggTilHvisIkkeDuplikat(
            String typeSamendring,
            Integer orgnr,
            List<RsPersonOgRolle> eksisterendeRoller,
            List<RsPersonOgRolle> nyeRoller
    ) {
        nyeRoller.forEach(nyRolle -> {
            if (!isRolleDuplikat(eksisterendeRoller, nyRolle)) {
                eksisterendeRoller.add(nyRolle);
            } else {
                log.info("Samendring av type '{}': Person med fnr '{}', rolle '{}' og fratredelsestatus '{}' finnes allerede i organisasjon '{}'.",
                        typeSamendring,
                        nyRolle.getFodselsnr(),
                        nyRolle.getRolle(),
                        nyRolle.isFratraadt(),
                        orgnr);
            }
        });
    }

    private boolean isRolleDuplikat(
            List<RsPersonOgRolle> eksisterendeRoller,
            RsPersonOgRolle nyRolle
    ) {
        return eksisterendeRoller.stream().anyMatch(
                eksisterendePerson ->
                        nyRolle.getFodselsnr().equals(eksisterendePerson.getFodselsnr())
                                && nyRolle.getRolle().equals(eksisterendePerson.getRolle())
                                && nyRolle.isFratraadt() == eksisterendePerson.isFratraadt()
        );
    }

    private void setRollebeskrivelse(RsRolleoversikt rsRolleoversikt) {
        for (var enhet : rsRolleoversikt.getEnheter()) {
            if (enhet.getRollebeskrivelse() == null || enhet.getRollebeskrivelse().isBlank()) {
                enhet.setRollebeskrivelse(enhet.getRolle().getBeskrivelse());
            }
        }
    }
}
