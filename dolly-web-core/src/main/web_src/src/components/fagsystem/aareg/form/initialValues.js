import subYears from 'date-fns/subYears'

export const initialValues = [
	{
		ansettelsesPeriode: {
			fom: subYears(new Date(), 20),
			tom: null
		},
		arbeidsforholdstype: 'ordinaertArbeidsforhold',
		arbeidsgiver: {
			aktoertype: '',
			orgnummer: '',
			ident: ''
		},
		arbeidsavtale: {
			yrke: '',
			stillingsprosent: 100,
			endringsdatoStillingsprosent: null,
			arbeidstidsordning: 'ikkeSkift',
			antallKonverterteTimer: null,
			avtaltArbeidstimerPerUke: 37.5
		},
		antallTimerForTimeloennet: [],
		permisjon: [],
		utenlandsopphold: []
	}
]

export const initialTimeloennet = {
	periode: {
		fom: null,
		tom: null
	},
	antallTimer: 0
}

export const initialPermisjon = {
	permisjonsPeriode: {
		fom: null,
		tom: null
	},
	permisjonsprosent: 100,
	permisjonOgPermittering: ''
}

export const initialUtenlandsopphold = {
	periode: {
		fom: null,
		tom: null
	},
	land: ''
}
