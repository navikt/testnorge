package no.nav.dolly.domain.resultset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum BAFeilkoder {
    BA10("Arbeidsforholdet mangler virksomhet."),
    BA20("En verdi er utenfor det gyldige intervallet for feltet."),
    BA30("Et organisasjonsnummer har ugyldig format."),
    BA40("Et fødselsnummer har ugyldig format."),
    BA50("En dato som må være tidligere enn eller lik en annen dato er isteden senere."),
    BA51("En dato som må være senere eller lik en annen dato er isteden tidligere."),
    BA60("Start- og sluttdato er ikke innenfor samme kalendermåned."),
    BA70("Et felt som ikke skal kunne defineres ble definert."),
    BA80("Et låst felt ble gjort endringer på."),
    BA90("Ingen arbeidsforhold med den oppgitte identifikatoren eksisterer fra før."),
    BA100("Ingen arbeidsgiver har blitt spesifisert for arbeidsforholdet."),
    BA110("En permisjonsidentifikator blir forsøkt brukt i to forskjellige permisjoner."),
    BA120("Et felt som trengs internt i valideringsprosessen mangler."),
    BA160("Et fødselsnummer ble ikke funnet i PDL."),
    BA161("En teknisk feil oppsto da et fødselsnummer skulle valideres mot PDL."),
    BA170("Et organisasjonsnummer ble ikke funnet i enhetsregisteret."),
    BA171("En teknisk feil oppsto da et organisasjonsnummer skulle valideres mot enhetsregisteret"),
    BA172("Et areidsforhold ble forsøkt registrert der arbeidstakers fødselsnummer er identisk med arbeidsgivers organisasjonsnummer."),
    BA173("En bestilt organisasjon har ikke riktig enhetstype."),
    BA180("En kodeverkskode ble ikke funnet i felles kodeverk."),
    BA181("En teknisk feil oppsto da en kodeverkskode skulle valideres mot felles kodeverk."),
    BA190("To arbeidsforhold med overlappende ansettelsesperioder har identiske identifikatorer."),
    BA191("Ekstern arbeidsforholdId kan ikke endres for et aktivt arbeidsforhold"),
    BA192("Stillingsprosent kan ikke settes når feltene antall timer og konverterte timer er satt"),
    BA193("Det skal ikke være mulig å endre verdien i feltet for Antall konverterte timer hvis arbeidsforholdet er sist endret av a-ordningen"),
    BA194("Det skal kun være mulig å sette feltene Stillingsprosent og Antall timer i full stilling hvis arbeidsforholdet er sist endret av a-ordningen");

    private String beskrivelse;
}