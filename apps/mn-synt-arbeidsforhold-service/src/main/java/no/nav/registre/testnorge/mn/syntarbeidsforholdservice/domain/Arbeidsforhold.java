package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdRequest;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdResponse;

@Slf4j
public class Arbeidsforhold {

    public Arbeidsforhold(ArbeidsforholdDTO dto, String ident, String virksomhentsnummer) {
        this.dto = dto;
        this.ident = ident;
        this.virksomhentsnummer = virksomhentsnummer;
    }

    private final ArbeidsforholdDTO dto;
    private final String ident;
    private String virksomhentsnummer;

    public Arbeidsforhold(
            ArbeidsforholdResponse response,
            String ident,
            String arbeidsforholdId,
            String virksomhentsnummer
    ) {
        this.virksomhentsnummer = virksomhentsnummer;
        this.ident = ident;
        this.dto = ArbeidsforholdDTO
                .builder()
                .typeArbeidsforhold(emptyToNull(response.getArbeidsforholdType()))
                .antallTimerPerUke(emptyToNull(response.getAntallTimerPerUkeSomEnFullStillingTilsvarer()))
                .arbeidstidsordning(emptyToNull(response.getArbeidstidsordning()))
                .sisteLoennsendringsdato(response.getSisteDatoForStillingsprosentendring())
                .stillingsprosent(emptyToNull(response.getStillingsprosent()))
                .yrke(emptyToNull(response.getYrke()))
                .startdato(response.getStartdato())
                .sluttdato(response.getSluttdato().equals("") ? null : LocalDate.parse(response.getSluttdato()))
                .arbeidsforholdId(arbeidsforholdId)
                .build();
    }

    public Arbeidsforhold(
            ArbeidsforholdResponse response,
            String ident,
            String virksomhentsnummer
    ) {
        this(response, ident, UUID.randomUUID().toString(), virksomhentsnummer);
    }

    public String getVirksomhentsnummer() {
        return virksomhentsnummer;
    }

    public void setVirksomhentsnummer(String virksomhentsnummer) {
        this.virksomhentsnummer = virksomhentsnummer;
    }

    public String getIdent() {
        return ident;
    }

    public ArbeidsforholdDTO toDTO() {
        return dto;
    }

    public String getArbeidsforholdId() {
        return dto.getArbeidsforholdId();
    }

    public LocalDate getSluttdato() {
        return dto.getSluttdato();
    }

    public ArbeidsforholdRequest toSyntrestDTO(LocalDate kaldermaaned) {
        float velferdspermisjon = 0;
        float utdanningspermisjon = 0;
        float permisjonMedForeldrepenger = 0;
        float permisjonVedMilitaertjeneste = 0;
        float permisjon = 0;
        float permittering = 0;

        for (var permisjonDTO : dto.getPermisjoner()) {
            switch (permisjonDTO.getBeskrivelse()) {
                case "velferdspermisjon":
                    velferdspermisjon++;
                    break;
                case "utdanningspermisjon":
                    utdanningspermisjon++;
                    break;
                case "permisjonMedForeldrepenger":
                    permisjonMedForeldrepenger++;
                    break;
                case "permisjonVedMilitaertjeneste":
                    permisjonVedMilitaertjeneste++;
                    break;
                case "permisjon":
                    permisjon++;
                    break;
                case "permittering":
                    permittering++;
                    break;
                default:
                    log.warn("Ukjent permisjons beskrivelse {}", permisjonDTO.getBeskrivelse());
                    break;
            }
        }

        return ArbeidsforholdRequest
                .builder()
                .antallTimerPerUkeSomEnFullStillingTilsvarer(nullToEmpty(dto.getAntallTimerPerUke()))
                .arbeidsforholdType(nullToEmpty(dto.getTypeArbeidsforhold()))
                .arbeidstidsordning(nullToEmpty(dto.getArbeidstidsordning()))
                .permisjon(permisjon)
                .permisjonMedForeldrepenger(permisjonMedForeldrepenger)
                .permisjonVedMilitaertjeneste(permisjonVedMilitaertjeneste)
                .permittering(permittering)
                .rapporteringsmaaned(formatKaldenermaand(kaldermaaned))
                .sisteDatoForStillingsprosentendring(kaldermaaned)
                .sisteLoennsendringsdato(dto.getSisteLoennsendringsdato())
                .sluttdato(format(dto.getSluttdato()))
                .startdato(dto.getStartdato())
                .stillingsprosent(nullToEmpty(dto.getStillingsprosent()))
                .utdanningspermisjon(utdanningspermisjon)
                .yrke(nullToEmpty(dto.getYrke()))
                .velferdspermisjon(velferdspermisjon)
                .build();
    }

    private Float nullToEmpty(Float value) {
        return value == null ? 0f : value;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String emptyToNull(String value) {
        return value == null ? null : value.equals("") ? null : value;
    }

    private Float emptyToNull(Float value) {
        return value == 0f ? null : value;
    }

    private String formatKaldenermaand(LocalDate value) {
        return value.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    private String format(LocalDate value) {
        if (value == null) {
            return "";
        }
        return value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
