import subYears from 'date-fns/subYears'

export const initialValues = [
	{
		arbeidsforholdstype: '',
		genererPeriode: {
			fom: null,
			tom: null
		}
	}
]

export const initialAmelding = [
	{
		maaned: null,
		arbeidsforhold: []
	}
]

export const initialForenkletOppgjoersordning = {
	ansettelsesPeriode: {
		fom: subYears(new Date(), 20),
		tom: null,
		sluttaarsak: null
	},
	arbeidsavtale: {
		yrke: ''
	}
}

export const initialArbeidsforholdOrg = {
	arbeidsforholdstype: '',
	arbeidsgiver: {
		aktoertype: 'ORG',
		orgnummer: ''
	},
	arbeidsforholdId: '',
	ansettelsesPeriode: {
		fom: subYears(new Date(), 20),
		tom: null,
		sluttaarsak: null
	},
	arbeidsavtale: {
		yrke: '',
		ansettelsesform: 'fast',
		stillingsprosent: 100,
		endringsdatoStillingsprosent: null,
		endringsdatoLoenn: null,
		arbeidstidsordning: 'ikkeSkift',
		avtaltArbeidstimerPerUke: 37.5
	},
	fartoy: []
}

export const initialArbeidsforholdPers = {
	arbeidsforholdstype: '',
	arbeidsgiver: {
		aktoertype: 'PERS',
		ident: ''
	},
	arbeidsforholdId: '',
	ansettelsesPeriode: {
		fom: subYears(new Date(), 20),
		tom: null,
		sluttaarsak: null
	},
	arbeidsavtale: {
		yrke: '',
		ansettelsesform: 'fast',
		stillingsprosent: 100,
		endringsdatoStillingsprosent: null,
		endringsdatoLoenn: null,
		arbeidstidsordning: 'ikkeSkift',
		avtaltArbeidstimerPerUke: 37.5
	},
	fartoy: []
}

export const initialFartoy = {
	skipsregister: '',
	fartoystype: '',
	fartsomraade: ''
}

export const initialTimeloennet = {
	periode: {
		fom: null,
		tom: null
	},
	antallTimer: 0
}

export const initialUtenlandsopphold = {
	periode: {
		fom: null,
		tom: null
	},
	land: ''
}

export const initialPermisjon = {
	permisjonsPeriode: {
		fom: null,
		tom: null
	},
	permisjonsprosent: 100,
	permisjon: ''
}

export const initialPermittering = {
	permitteringsPeriode: {
		fom: null,
		tom: null
	},
	permitteringsprosent: 100
}
