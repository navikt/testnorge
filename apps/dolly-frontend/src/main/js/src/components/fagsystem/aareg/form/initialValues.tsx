import subYears from 'date-fns/subYears'

export const initialArbeidsgiverOrg = {
	aktoertype: 'ORG',
	orgnummer: '',
}

export const initialArbeidsgiverPers = {
	aktoertype: 'PERS',
	ident: '',
}

export const initialForenkletOppgjoersordning = {
	ansettelsesPeriode: {
		fom: subYears(new Date(), 20),
		tom: null,
		sluttaarsak: null,
	},
	arbeidsavtale: {
		yrke: '',
	},
	arbeidsforholdstype: 'forenkletOppgjoersordning',
}

export const initialForenkletOppgjoersordningOrg = {
	...initialForenkletOppgjoersordning,
	arbeidsgiver: initialArbeidsgiverOrg,
}

export const initialForenkletOppgjoersordningPers = {
	...initialForenkletOppgjoersordning,
	arbeidsgiver: initialArbeidsgiverPers,
}

export const initialArbeidsforhold = {
	arbeidsforholdstype: 'ordinaertArbeidsforhold',
	arbeidsforholdId: '',
	ansettelsesPeriode: {
		fom: subYears(new Date(), 20),
		tom: null,
		sluttaarsak: null,
	},
	arbeidsavtale: {
		yrke: '',
		ansettelsesform: 'fast',
		stillingsprosent: 100,
		endringsdatoStillingsprosent: null,
		sisteLoennsendringsdato: null,
		arbeidstidsordning: 'ikkeSkift',
		avtaltArbeidstimerPerUke: 37.5,
	},
	antallTimerForTimeloennet: [],
	utenlandsopphold: [],
	permisjon: [],
	permittering: [],
}

export const initialArbeidsforholdOrg = {
	...initialArbeidsforhold,
	arbeidsgiver: initialArbeidsgiverOrg,
}

export const initialArbeidsforholdPers = {
	...initialArbeidsforhold,
	arbeidsgiver: initialArbeidsgiverPers,
}

export const initialPeriode = {
	fom: null,
	tom: null,
	periode: [],
}

export const initialAmelding = [
	{
		maaned: null,
		arbeidsforhold: [],
	},
]

export const initialValues = {
	arbeidsforholdstype: 'ordinaertArbeidsforhold',
	genererPeriode: initialPeriode,
	amelding: initialAmelding,
}

export const initialFartoy = [
	{
		skipsregister: '',
		skipstype: '',
		fartsomraade: '',
	},
]

export const initialTimeloennet = {
	periode: {
		fom: null,
		tom: null,
	},
	antallTimer: 0,
}

export const initialUtenlandsopphold = {
	periode: {
		fom: null,
		tom: null,
	},
	land: '',
}

export const initialPermisjon = {
	permisjonsPeriode: {
		fom: null,
		tom: null,
	},
	permisjonsprosent: 100,
	permisjon: '',
}

export const initialPermittering = {
	permitteringsPeriode: {
		fom: null,
		tom: null,
	},
	permitteringsprosent: 100,
}
