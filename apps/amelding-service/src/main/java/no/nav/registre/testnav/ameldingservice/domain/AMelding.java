package no.nav.registre.testnav.ameldingservice.domain;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.AMeldingDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.AvvikDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.FartoeyDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.InntektDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.PermisjonDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.PersonDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.VirksomhetDTO;

@RequiredArgsConstructor
public class AMelding {
    private final AMeldingDTO dto;

    public LocalDate getKalendermaaned() {
        return dto.getKalendermaaned();
    }

    public String getOpplysningspliktigOrganisajonsnummer() {
        return dto.getOpplysningspliktigOrganisajonsnummer();
    }

    public AMelding(OppsummeringsdokumentDTO oppsummeringsdokumentDTO) {
        this.dto = create(oppsummeringsdokumentDTO);
    }

    public AMeldingDTO toDTO() {
        return this.dto;
    }

    public OppsummeringsdokumentDTO updateOppsummeringsdokumentDTO(OppsummeringsdokumentDTO oppsummeringsdokumentDTO) {
        oppsummeringsdokumentDTO.setVersion(oppsummeringsdokumentDTO.getVersion() + 1);
        dto.getVirksomheter().forEach(newVirksomhet -> {
            var result = oppsummeringsdokumentDTO
                    .getVirksomheter()
                    .stream()
                    .filter(value -> value.getOrganisajonsnummer().equals(newVirksomhet.getOrganisajonsnummer()))
                    .findFirst();

            if (result.isEmpty()) {
                oppsummeringsdokumentDTO.getVirksomheter().add(create(newVirksomhet));
            } else {
                merge(result.get(), newVirksomhet);
            }
        });
        return oppsummeringsdokumentDTO;
    }

    public OppsummeringsdokumentDTO newOppsummeringsdokumentDTO() {
        return create(dto);
    }

    private void merge(
            VirksomhetDTO target,
            no.nav.registre.testnorge.libs.dto.ameldingservice.v1.VirksomhetDTO other
    ) {
        other.getPersoner().forEach(newPerson -> {
            var result = target.getPersoner()
                    .stream()
                    .filter(value -> value.getIdent().equals(newPerson.getIdent()))
                    .findFirst();

            if (result.isEmpty()) {
                target.getPersoner().add(create(newPerson));
            } else {
                merge(result.get(), newPerson);
            }


        });
    }

    private void merge(PersonDTO target, no.nav.registre.testnorge.libs.dto.ameldingservice.v1.PersonDTO other) {
        other.getArbeidsforhold().forEach(newArbeidsforhold -> {
            var result = target.getArbeidsforhold()
                    .stream()
                    .filter(value -> isEqual(value, newArbeidsforhold))
                    .findFirst();

            if (result.isEmpty()) {
                target.getArbeidsforhold().add(create(newArbeidsforhold));
            } else {
                merge(result.get(), newArbeidsforhold);
            }
        });
    }

    private void merge(ArbeidsforholdDTO target, no.nav.registre.testnorge.libs.dto.ameldingservice.v1.ArbeidsforholdDTO other) {
        target.setArbeidstidsordning(other.getArbeidstidsordning());
        target.setAvvik(other.getAvvik().stream().map(this::create).collect(Collectors.toList()));
        target.setFartoey(create(other.getFartoey()));
        target.setInntekter(other.getInntekter().stream().map(this::create).collect(Collectors.toList()));
        target.setPermisjoner(other.getPermisjoner().stream().map(this::create).collect(Collectors.toList()));
        target.setAntallTimerPerUke(other.getAntallTimerPerUke());
        target.setSluttdato(other.getSluttdato());
        target.setStartdato(other.getStartdato());
        target.setSisteLoennsendringsdato(other.getSisteLoennsendringsdato());
        target.setStillingsprosent(other.getStillingsprosent());
        target.setTypeArbeidsforhold(other.getArbeidsforholdType());
        target.setYrke(other.getYrke());
    }

    private boolean isEqual(ArbeidsforholdDTO first, no.nav.registre.testnorge.libs.dto.ameldingservice.v1.ArbeidsforholdDTO second) {
        return first.getArbeidsforholdId() == null && second.getArbeidsforholdId() == null
                || first.getArbeidsforholdId() != null && second.getArbeidsforholdId() != null && first.getArbeidsforholdId().equals(second.getArbeidsforholdId());
    }


    private OppsummeringsdokumentDTO create(AMeldingDTO aMeldingDTO) {
        return OppsummeringsdokumentDTO
                .builder()
                .kalendermaaned(aMeldingDTO.getKalendermaaned())
                .opplysningspliktigOrganisajonsnummer(aMeldingDTO.getOpplysningspliktigOrganisajonsnummer())
                .virksomheter(aMeldingDTO.getVirksomheter().stream().map(this::create).collect(Collectors.toList()))
                .version(1L)
                .build();
    }

    private AMeldingDTO create(OppsummeringsdokumentDTO oppsummeringsdokumentDTO) {
        return AMeldingDTO
                .builder()
                .kalendermaaned(oppsummeringsdokumentDTO.getKalendermaaned())
                .opplysningspliktigOrganisajonsnummer(oppsummeringsdokumentDTO.getOpplysningspliktigOrganisajonsnummer())
                .virksomheter(oppsummeringsdokumentDTO.getVirksomheter().stream().map(this::create).collect(Collectors.toList()))
                .build();
    }

    private VirksomhetDTO create(no.nav.registre.testnorge.libs.dto.ameldingservice.v1.VirksomhetDTO virksomhet) {
        return VirksomhetDTO
                .builder()
                .organisajonsnummer(virksomhet.getOrganisajonsnummer())
                .personer(virksomhet.getPersoner().stream().map(this::create).collect(Collectors.toList()))
                .build();
    }

    private no.nav.registre.testnorge.libs.dto.ameldingservice.v1.VirksomhetDTO create(VirksomhetDTO virksomhet) {
        return no.nav.registre.testnorge.libs.dto.ameldingservice.v1.VirksomhetDTO
                .builder()
                .organisajonsnummer(virksomhet.getOrganisajonsnummer())
                .personer(virksomhet.getPersoner().stream().map(this::create).collect(Collectors.toList()))
                .build();
    }

    private PersonDTO create(no.nav.registre.testnorge.libs.dto.ameldingservice.v1.PersonDTO personDTO) {
        return PersonDTO
                .builder()
                .ident(personDTO.getIdent())
                .arbeidsforhold(personDTO.getArbeidsforhold().stream().map(this::create).collect(Collectors.toList()))
                .build();
    }

    private no.nav.registre.testnorge.libs.dto.ameldingservice.v1.PersonDTO create(PersonDTO personDTO) {
        return no.nav.registre.testnorge.libs.dto.ameldingservice.v1.PersonDTO
                .builder()
                .ident(personDTO.getIdent())
                .arbeidsforhold(personDTO.getArbeidsforhold().stream().map(this::create).collect(Collectors.toList()))
                .build();
    }

    private ArbeidsforholdDTO create(no.nav.registre.testnorge.libs.dto.ameldingservice.v1.ArbeidsforholdDTO arbeidsforholdDTO) {
        return ArbeidsforholdDTO
                .builder()
                .antallTimerPerUke(arbeidsforholdDTO.getAntallTimerPerUke())
                .arbeidsforholdId(arbeidsforholdDTO.getArbeidsforholdId())
                .arbeidstidsordning(arbeidsforholdDTO.getArbeidstidsordning())
                .avvik(arbeidsforholdDTO.getAvvik().stream().map(this::create).collect(Collectors.toList()))
                .fartoey(create(arbeidsforholdDTO.getFartoey()))
                .sisteLoennsendringsdato(arbeidsforholdDTO.getSisteLoennsendringsdato())
                .sluttdato(arbeidsforholdDTO.getSluttdato())
                .startdato(arbeidsforholdDTO.getStartdato())
                .stillingsprosent(arbeidsforholdDTO.getStillingsprosent())
                .typeArbeidsforhold(arbeidsforholdDTO.getArbeidsforholdType())
                .yrke(arbeidsforholdDTO.getYrke())
                .inntekter(arbeidsforholdDTO.getInntekter().stream().map(this::create).collect(Collectors.toList()))
                .permisjoner(arbeidsforholdDTO.getPermisjoner().stream().map(this::create).collect(Collectors.toList()))
                .build();
    }

    private no.nav.registre.testnorge.libs.dto.ameldingservice.v1.ArbeidsforholdDTO create(ArbeidsforholdDTO arbeidsforholdDTO) {
        return no.nav.registre.testnorge.libs.dto.ameldingservice.v1.ArbeidsforholdDTO
                .builder()
                .antallTimerPerUke(arbeidsforholdDTO.getAntallTimerPerUke())
                .arbeidsforholdId(arbeidsforholdDTO.getArbeidsforholdId())
                .arbeidstidsordning(arbeidsforholdDTO.getArbeidstidsordning())
                .avvik(arbeidsforholdDTO.getAvvik().stream().map(this::create).collect(Collectors.toList()))
                .fartoey(create(arbeidsforholdDTO.getFartoey()))
                .sisteLoennsendringsdato(arbeidsforholdDTO.getSisteLoennsendringsdato())
                .sluttdato(arbeidsforholdDTO.getSluttdato())
                .startdato(arbeidsforholdDTO.getStartdato())
                .stillingsprosent(arbeidsforholdDTO.getStillingsprosent())
                .arbeidsforholdType(arbeidsforholdDTO.getTypeArbeidsforhold())
                .yrke(arbeidsforholdDTO.getYrke())
                .inntekter(arbeidsforholdDTO.getInntekter().stream().map(this::create).collect(Collectors.toList()))
                .permisjoner(arbeidsforholdDTO.getPermisjoner().stream().map(this::create).collect(Collectors.toList()))
                .build();
    }

    private PermisjonDTO create(no.nav.registre.testnorge.libs.dto.ameldingservice.v1.PermisjonDTO permisjonDTO) {
        return PermisjonDTO
                .builder()
                .avvik(permisjonDTO.getAvvik().stream().map(this::create).collect(Collectors.toList()))
                .beskrivelse(permisjonDTO.getBeskrivelse())
                .permisjonId(permisjonDTO.getPermisjonId())
                .permisjonsprosent(permisjonDTO.getPermisjonsprosent())
                .sluttdato(permisjonDTO.getSluttdato())
                .startdato(permisjonDTO.getStartdato())
                .build();
    }

    private no.nav.registre.testnorge.libs.dto.ameldingservice.v1.PermisjonDTO create(PermisjonDTO permisjonDTO) {
        return no.nav.registre.testnorge.libs.dto.ameldingservice.v1.PermisjonDTO
                .builder()
                .avvik(permisjonDTO.getAvvik().stream().map(this::create).collect(Collectors.toList()))
                .beskrivelse(permisjonDTO.getBeskrivelse())
                .permisjonId(permisjonDTO.getPermisjonId())
                .permisjonsprosent(permisjonDTO.getPermisjonsprosent())
                .sluttdato(permisjonDTO.getSluttdato())
                .startdato(permisjonDTO.getStartdato())
                .build();
    }

    private InntektDTO create(no.nav.registre.testnorge.libs.dto.ameldingservice.v1.InntektDTO inntektDTO) {
        return InntektDTO
                .builder()
                .antall(inntektDTO.getAntall())
                .avvik(inntektDTO.getAvvik().stream().map(this::create).collect(Collectors.toList()))
                .opptjeningsland(inntektDTO.getOpptjeningsland())
                .sluttdatoOpptjeningsperiode(inntektDTO.getSluttdatoOpptjeningsperiode())
                .startdatoOpptjeningsperiode(inntektDTO.getStartdatoOpptjeningsperiode())
                .build();
    }

    private no.nav.registre.testnorge.libs.dto.ameldingservice.v1.InntektDTO create(InntektDTO inntektDTO) {
        return no.nav.registre.testnorge.libs.dto.ameldingservice.v1.InntektDTO
                .builder()
                .antall(inntektDTO.getAntall())
                .avvik(inntektDTO.getAvvik().stream().map(this::create).collect(Collectors.toList()))
                .opptjeningsland(inntektDTO.getOpptjeningsland())
                .sluttdatoOpptjeningsperiode(inntektDTO.getSluttdatoOpptjeningsperiode())
                .startdatoOpptjeningsperiode(inntektDTO.getStartdatoOpptjeningsperiode())
                .build();
    }


    private FartoeyDTO create(no.nav.registre.testnorge.libs.dto.ameldingservice.v1.FartoeyDTO fartoeyDTO) {
        if (fartoeyDTO == null) {
            return null;
        }
        return FartoeyDTO
                .builder()
                .fartsomraade(fartoeyDTO.getFartsomraade())
                .skipsregister(fartoeyDTO.getSkipsregister())
                .skipstype(fartoeyDTO.getSkipstype())
                .build();
    }

    private no.nav.registre.testnorge.libs.dto.ameldingservice.v1.FartoeyDTO create(FartoeyDTO fartoeyDTO) {
        if (fartoeyDTO == null) {
            return null;
        }
        return no.nav.registre.testnorge.libs.dto.ameldingservice.v1.FartoeyDTO
                .builder()
                .fartsomraade(fartoeyDTO.getFartsomraade())
                .skipsregister(fartoeyDTO.getSkipsregister())
                .skipstype(fartoeyDTO.getSkipstype())
                .build();
    }

    private AvvikDTO create(no.nav.registre.testnorge.libs.dto.ameldingservice.v1.AvvikDTO avvikDTO) {
        return AvvikDTO
                .builder()
                .id(avvikDTO.getId())
                .alvorlighetsgrad(avvikDTO.getAlvorlighetsgrad())
                .navn(avvikDTO.getNavn())
                .build();
    }

    private no.nav.registre.testnorge.libs.dto.ameldingservice.v1.AvvikDTO create(AvvikDTO avvikDTO) {
        return no.nav.registre.testnorge.libs.dto.ameldingservice.v1.AvvikDTO
                .builder()
                .id(avvikDTO.getId())
                .alvorlighetsgrad(avvikDTO.getAlvorlighetsgrad())
                .navn(avvikDTO.getNavn())
                .build();
    }

}
