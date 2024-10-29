export enum ArbeidsgiverTyper {
	egen = 'EGEN',
	felles = 'FELLES',
	fritekst = 'FRITEKST',
	privat = 'PRIVAT',
}

export type AaregListe = {
	aareg: Array<Aareg>
}

export type Aareg = {
	arbeidsforholdstype: string
	genererPeriode: {
		fom: string
		tom: string
		peiode: Array<string>
	}
}

export type Amelding = {
	maaned: string
	arbeidsforhold: Array<Arbeidsforhold>
}

type Arbeidsforhold = {
	arbeidsforholdId: string
}

export type KodeverkValue = {
	gyldigFra: string
	gyldigTil: string
	label: string
	value: string
}
