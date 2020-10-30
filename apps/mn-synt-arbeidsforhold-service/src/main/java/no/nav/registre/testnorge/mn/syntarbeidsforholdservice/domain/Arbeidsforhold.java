package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.ArbeidsforholdDTO;

public class Arbeidsforhold {
    public static final DateTimeFormatter SYNT_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final ArbeidsforholdDTO dto;
    private final String ident;

    private Arbeidsforhold(String ident, String yrke, LocalDate startdato) {
        this.ident = ident;
        this.dto = ArbeidsforholdDTO
                .builder()
                .typeArbeidsforhold("ordinaertArbeidsforhold")
                .antallTimerPerUke(37.5f)
                .arbeidstidsordning("ikkeSkift")
                .sisteLoennsendringsdato(startdato)
                .stillingsprosent(100.0f)
                .yrke(yrke)
                .startdato(startdato)
                .arbeidsforholdId(UUID.randomUUID().toString())
                .build();
    }

    public Arbeidsforhold(
            no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdDTO arbeidsforholdDTO,
            String ident,
            String arbeidsforholdId
    ) {
        this.ident = ident;
        this.dto = ArbeidsforholdDTO
                .builder()
                .typeArbeidsforhold(emptyToNull(arbeidsforholdDTO.getArbeidsforholdType()))
                .antallTimerPerUke(emptyToNull(arbeidsforholdDTO.getAntallTimerPerUkeSomEnFullStillingTilsvarer()))
                .arbeidstidsordning(emptyToNull(arbeidsforholdDTO.getArbeidsordning()))
                .sisteLoennsendringsdato(format(arbeidsforholdDTO.getSisteDatoForStillingsprosentendring()))
                .stillingsprosent(emptyToNull(arbeidsforholdDTO.getStillingsprosent()))
                .yrke(emptyToNull(arbeidsforholdDTO.getYrke()))
                .startdato(format(arbeidsforholdDTO.getStartdato()))
                .sluttdato(format(arbeidsforholdDTO.getSluttdato()))
                .arbeidsforholdId(arbeidsforholdId)
                .build();
    }


    public static Arbeidsforhold from(String ident, String yrke, LocalDate startdato) {
        return new Arbeidsforhold(ident, yrke, startdato);
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

    public no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdDTO toSyntrestDTO(LocalDate kaldermaaned) {
        return no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdDTO
                .builder()
                .antallTimerPerUkeSomEnFullStillingTilsvarer(nullToEmpty(dto.getAntallTimerPerUke()))
                .arbeidsforholdType(nullToEmpty(dto.getTypeArbeidsforhold()))
                .arbeidsordning(nullToEmpty(dto.getArbeidstidsordning()))
                .avloenningstype("")
                .permisjon(0f)
                .permisjonMedForeldrepenger(0f)
                .permisjonVedMilitaertjeneste(0f)
                .permittering(0f)
                .rapporteringsmaaned(formatKaldenermaand(kaldermaaned))
                .sisteDatoForStillingsprosentendring(format(kaldermaaned))
                .sisteLoennsendringsdato(format(dto.getSisteLoennsendringsdato()))
                .sluttdato(format(dto.getSluttdato()))
                .startdato(format(dto.getStartdato()))
                .stillingsprosent(nullToEmpty(dto.getStillingsprosent()))
                .utdanningspermisjon(0f)
                .yrke(nullToEmpty(dto.getYrke()))
                .velferdspermisjon(0f)
                .build();
    }

    private Float nullToEmpty(Float value) {
        return value == null ? 0f : value;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String emptyToNull(String value) {
        return value.equals("") ? null : value;
    }

    private Float emptyToNull(Float value) {
        return value == 0f ? null : value;
    }

    private LocalDate format(String value) {
        return value.equals("") ? null : LocalDate.parse(value, SYNT_FORMATTER);
    }

    private String format(LocalDate value) {
        if (value == null) {
            return "";
        }
        return value.format(SYNT_FORMATTER);
    }

    private String formatKaldenermaand(LocalDate value) {
        return value.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }
}
