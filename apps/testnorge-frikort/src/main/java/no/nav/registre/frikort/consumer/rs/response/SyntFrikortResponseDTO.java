package no.nav.registre.frikort.consumer.rs.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.io.Resources;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.frikort.domain.xml.Egenandelskode;
import no.nav.registre.frikort.domain.xml.Samhandlertypekode;
import no.nav.registre.frikort.service.util.JsonDateDeserializer;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.util.Objects.isNull;

@Slf4j
@Getter
public class SyntFrikortResponseDTO {

    @JsonIgnore
    private static final Map<String, Integer> SAMHANDLERTYPE_MAKS_BELOEP;
    @JsonIgnore
    private static final Long AVSPILLERGRUPPE_SAMHANDLERE = 100001163L;
    @JsonIgnore
    private static final String MILJOE_SAMHANDLERE = "q2";

    static {
        SAMHANDLERTYPE_MAKS_BELOEP = new HashMap<>();
        URL resourceSamhandlertyper = Resources.getResource("samhandlertypekode_til_maksbeloep.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            var map = objectMapper.readValue(resourceSamhandlertyper, new TypeReference<Map<String, Integer>>() {
            });
            SAMHANDLERTYPE_MAKS_BELOEP.putAll(map);
        } catch (IOException e) {
            log.error("Kunne ikke laste inn samhandlertyper.", e);
        }
    }

    private final String betalt;
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonAlias("dato_mottatt")
    private final LocalDateTime datoMottatt;
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonAlias("datotjeneste")
    private final LocalDateTime datoTjeneste;
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonAlias("datotjenestestart")
    private final LocalDateTime datoTjenestestart;
    private final Double egenandelsats;
    private final Double egenandelsbelop;
    private final Egenandelskode egenandelskode;
    private final String enkeltregningsstatuskode;
    private final String innsendingstypekode;
    private final String kildesystemkode;
    private final String merknader;
    private final Samhandlertypekode samhandlertypekode;

    public SyntFrikortResponseDTO(SyntFrikortResponse response) {
        this.datoMottatt = response.getDatoMottatt();
        this.datoTjeneste = response.getDatoTjeneste();
        this.egenandelsats = response.getEgenandelsats();
        this.egenandelsbelop = response.getEgenandelsbelop();
        this.betalt = response.getBetalt();
        this.datoTjenestestart = response.getDatoTjenestestart();
        this.egenandelskode = response.getEgenandelskode();
        this.enkeltregningsstatuskode = response.getEnkeltregningsstatuskode();
        this.innsendingstypekode = response.getInnsendingstypekode();
        this.kildesystemkode = response.getKildesystemkode();
        this.merknader = response.getMerknader();
        this.samhandlertypekode = response.getSamhandlertypekode();
    }

    public SyntFrikortResponseDTO(SyntFrikortResponse response, boolean valider, Random rand) {
        if (valider) {
            // sett gyldige datoer
            var datoMottatt = response.getDatoMottatt();
            var datoTjeneste = response.getDatoTjeneste();
            if (ChronoUnit.WEEKS.between(datoMottatt, LocalDateTime.now()) > 12) {
                datoMottatt = LocalDateTime.now().minusWeeks(rand.nextInt(6)).minusWeeks(1);
                datoTjeneste = datoMottatt.minusDays(rand.nextInt(7));
            }
            this.datoMottatt = datoMottatt;
            this.datoTjeneste = datoTjeneste;

            // sett gyldig egenandelskode og beloeper
            var egenandelsbelop = response.getEgenandelsbelop();
            var egenandelsats = response.getEgenandelsats();
            if (isNull(response.getEgenandelsbelop())) {
                egenandelsbelop = response.getEgenandelsats();
            }
            if (Egenandelskode.C != response.getEgenandelskode()) {
                egenandelsbelop = 0.0;
            }
            if (Egenandelskode.F != response.getEgenandelskode() && Egenandelskode.C != response.getEgenandelskode()) {
                egenandelsats = 0.0;
            }


            // sett gyldige beloper gitt samhandlertypekode
            var maksBeloep = SAMHANDLERTYPE_MAKS_BELOEP.get(response.getSamhandlertypekode().toString());
            if (egenandelsbelop > maksBeloep) {
                egenandelsbelop = maksBeloep * .9;
            }
            if (egenandelsats > maksBeloep) {
                egenandelsats = maksBeloep * .9;
            }

            this.egenandelsbelop = egenandelsbelop;
            this.egenandelsats = egenandelsats;
        } else {
            this.datoMottatt = response.getDatoMottatt();
            this.datoTjeneste = response.getDatoTjeneste();
            this.egenandelsats = response.getEgenandelsats();
            this.egenandelsbelop = response.getEgenandelsbelop();
        }

        this.betalt = response.getBetalt();
        this.datoTjenestestart = response.getDatoTjenestestart();
        this.egenandelskode = response.getEgenandelskode();
        this.enkeltregningsstatuskode = response.getEnkeltregningsstatuskode();
        this.innsendingstypekode = response.getInnsendingstypekode();
        this.kildesystemkode = response.getKildesystemkode();
        this.merknader = response.getMerknader();
        this.samhandlertypekode = response.getSamhandlertypekode();
    }
}
