package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.AvvikDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.FartoeyDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.InntektDTO;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdRequest;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdResponse;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.PermisjonDTO;

@Slf4j
public class Arbeidsforhold {

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

        var permisjoner = Optional.ofNullable(response.getPermisjoner()).orElse(Collections.emptyList())
                .stream()
                .map(dto -> no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.PermisjonDTO
                        .builder()
                        .permisjonId(UUID.randomUUID().toString())
                        .beskrivelse(dto.getBeskrivelse())
                        .permisjonsprosent(Float.parseFloat(dto.getPermisjonsprosent()))
                        .sluttdato(dto.getSluttdato())
                        .startdato(dto.getStartdato())
                        .avvik(getAvvik(dto.getAvvik()))
                        .build()
                ).collect(Collectors.toList());

        var inntekter = Optional.ofNullable(response.getInntekter())
                .orElse(Collections.emptyList())
                .stream()
                .map(inntekt -> InntektDTO
                        .builder()
                        .startdatoOpptjeningsperiode(inntekt.getStartdatoOpptjeningsperiode())
                        .sluttdatoOpptjeningsperiode(inntekt.getSluttdatoOpptjeningsperiode())
                        .opptjeningsland(inntekt.getOpptjeningsland())
                        .antall(inntekt.getAntall())
                        .avvik(getAvvik(inntekt.getAvvik()))
                        .build()
                ).collect(Collectors.toList());

        if (!permisjoner.isEmpty()) {
            log.trace("Permisjoner registert på arbeidsforhold id {}.", arbeidsforholdId);
        }

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
                .permisjoner(permisjoner)
                .historikk(historikk)
                .fartoey(response.getFartoey() != null ? FartoeyDTO.builder()
                        .fartsomraade(response.getFartoey().getFartsomraade())
                        .skipsregister(response.getFartoey().getSkipsregister())
                        .skipstype(response.getFartoey().getSkipstype())
                        .build() : null
                )
                .inntekter(inntekter)
                .avvik(getAvvik(response.getAvvik()))
                .build();
    }

    private List<AvvikDTO> getAvvik(no.nav.registre.testnorge.libs.dto.syntrest.v1.AvvikDTO avvik) {
        return avvik != null ? Collections.singletonList(AvvikDTO.builder()
                .id(avvik.getId())
                .alvorlighetsgrad(avvik.getAlvorlighetsgrad())
                .navn(avvik.getNavn())
                .build()
        ) : Collections.emptyList();
    }

    public boolean isForenklet() {
        return dto.getTypeArbeidsforhold().equals("forenkletOppgjoersordning");
    }

    public Arbeidsforhold(ArbeidsforholdResponse response, String ident, String virksomhetsnummer) {
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
                .fartoey(dto.getFartoey() != null ? no.nav.registre.testnorge.libs.dto.syntrest.v1.FartoeyDTO
                        .builder()
                        .fartsomraade(dto.getFartoey().getFartsomraade())
                        .skipsregister(dto.getFartoey().getSkipsregister())
                        .skipstype(dto.getFartoey().getSkipstype())
                        .build()
                        : null
                )
                .permisjoner(dto.getPermisjoner().stream().map(value -> new PermisjonDTO(
                        value.getBeskrivelse(),
                        value.getPermisjonsprosent() != null ? value.getPermisjonsprosent().toString() : null,
                        value.getStartdato(),
                        value.getSluttdato(),
                        getAvvik(value.getAvvik())
                )).collect(Collectors.toList()))
                .inntekter(dto.getInntekter().stream().map(value -> no.nav.registre.testnorge.libs.dto.syntrest.v1.InntektDTO
                        .builder()
                        .antall(value.getAntall())
                        .opptjeningsland(value.getOpptjeningsland())
                        .startdatoOpptjeningsperiode(value.getStartdatoOpptjeningsperiode())
                        .sluttdatoOpptjeningsperiode(value.getSluttdatoOpptjeningsperiode())
                        .avvik(getAvvik(value.getAvvik()))
                        .build()
                ).collect(Collectors.toList()))
                .antallInntekter(dto.getInntekter().size())
                .avvik(getAvvik(dto.getAvvik()))
                .build();
    }

    private no.nav.registre.testnorge.libs.dto.syntrest.v1.AvvikDTO getAvvik(List<no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.AvvikDTO> list) {
        return list.stream().findFirst().map(avvik -> no.nav.registre.testnorge.libs.dto.syntrest.v1.AvvikDTO.builder().alvorlighetsgrad(avvik.getAlvorlighetsgrad()).navn(avvik.getNavn()).id(avvik.getId()).build()).orElse(null);
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
