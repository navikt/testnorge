package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdRequest;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdResponse;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdWithHistorikkRequest;

@Slf4j
public class Arbeidsforhold {

    public Arbeidsforhold(ArbeidsforholdDTO dto, String ident, String virksomhetsnummer, String historikk) {
        this.dto = dto;
        this.ident = ident;
        this.virksomhetsnummer = virksomhetsnummer;
        this.historikk = historikk;
    }

    private final ArbeidsforholdDTO dto;
    private final String ident;
    private final String historikk;
    private String virksomhetsnummer;

    public Arbeidsforhold(
            ArbeidsforholdResponse response,
            String ident,
            String arbeidsforholdId,
            String virksomhetsnummer
    ) {
        this.virksomhetsnummer = virksomhetsnummer;
        this.ident = ident;
        this.historikk = response.getHistorikk();
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
            String virksomhetsnummer
    ) {
        this(response, ident, UUID.randomUUID().toString(), virksomhetsnummer);
    }

    public String getVirksomhetsnummer() {
        return virksomhetsnummer;
    }

    public void setVirksomhetsnummer(String virksomhetsnummer) {
        this.virksomhetsnummer = virksomhetsnummer;
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

    public LocalDate getStartdato() {
        return dto.getStartdato();
    }

    public ArbeidsforholdRequest toSyntrestDTO(LocalDate kaldermaaned, Integer count) {
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
                .historikk(historikk)
                .numEndringer(count)
                .build();
    }

    public ArbeidsforholdHistorikk toHistorikk(String miljo) {
        return new ArbeidsforholdHistorikk(getArbeidsforholdId(), historikk, miljo);
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
