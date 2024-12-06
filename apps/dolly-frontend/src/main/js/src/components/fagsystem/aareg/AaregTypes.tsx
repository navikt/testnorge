export enum ArbeidsgiverTyper {
	egen = 'EGEN',
	felles = 'FELLES',
	fritekst = 'FRITEKST',
	privat = 'PRIVAT',
}

export type Aareg = {
	arbeidsforholdstype: string
	genererPeriode: {
		fom: string
		tom: string
		periode: Array<string>
	}
	navArbeidsforholdPeriode?: {
		year: number
		monthValue: number
	}
	arbeidsgiver?: {
		orgnummer?: string
		ident?: string
	}
	ansettelsesPeriode?: {
		fom: string
		tom: string
		sluttaarsak: string
	}
	arbeidsavtale?: {
		yrke: string
		ansettelsesform: string
		stillingsprosent: number
		endringsdatoStillingsprosent: string
		sisteLoennsendringsdato: string
		arbeidstidsordning: string
		avtaltArbeidstimerPerUke: number
	}
	fartoy?: Array<Fartoy>
	antallTimerForTimeloennet?: Array<Timeloennet>
	utenlandsopphold?: Array<Utenlands>
	permisjon?: Array<PermisjonValues>
	permittering?: Array<PermitteringValues>
}

type Fartoy = {
	skipsregister: string
	skipstype: string
	fartsomraade: string
}

export type Timeloennet = {
	antallTimer: number
	periode: {
		fom: string
		tom: string
	}
}

export type Utenlands = {
	land: string
	periode: {
		fom: string
		tom: string
	}
}

export type PermisjonValues = {
	permisjon: string
	permisjonsPeriode: {
		fom: string
		tom: string
	}
	permisjonsprosent: number
}

export type PermitteringValues = {
	permitteringsPeriode: {
		fom: string
		tom: string
	}
	permitteringsprosent: number
}
