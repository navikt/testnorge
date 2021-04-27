import subYears from 'date-fns/subYears'

export const initialValues = [
	{
		arbeidsforholdstype: '',
		// ansettelsesPeriode: {
		// 	fom: subYears(new Date(), 20),
		// 	tom: null,
		// 	sluttaarsak: null
		// },
		// yrke: '',
		genererPeriode: {
			fom: null,
			tom: null
		},
		arbeidsforhold: []
		// arbeidsgiver: {
		// 	aktoertype: 'ORG',
		// 	orgnummer: '',
		// 	ident: ''
		// },
		// arbeidsforhold: initialArbeidsforhold
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

export const initialArbeidsforhold = {
	arbeidsgiver: {
		aktoertype: 'ORG',
		orgnummer: '',
		ident: ''
	},
	arbeidsforholdId: '',
	ansettelsesPeriode: {
		fom: subYears(new Date(), 20),
		tom: null,
		sluttaarsak: null
	},
	// Ansettelsesdetaljer:
	arbeidsavtale: {
		yrke: '',
		ansettelsesform: 'fast',
		stillingsprosent: 100,
		endringsdatoStillingsprosent: null,
		endringsdatoLoenn: null,
		arbeidstidsordning: 'ikkeSkift',
		avtaltArbeidstimerPerUke: 37.5
	}
}

export const initialFartoey = {
	fartoey: {
		skipsregister: '',
		skipstype: '',
		fartsomraade: ''
	}
}

// {
// 	ansettelsesPeriode: {
// 		fom: subYears(new Date(), 20),
// 		tom: null
// 	},
// 	arbeidsforholdstype: 'ordinaertArbeidsforhold',
// 	arbeidsgiver: {
// 		aktoertype: '',
// 		orgnummer: '',
// 		ident: ''
// 	},
// 	arbeidsavtale: {
// 		yrke: '',
// 		stillingsprosent: 100,
// 		endringsdatoStillingsprosent: null,
// 		arbeidstidsordning: 'ikkeSkift',
// 		antallKonverterteTimer: '',
// 		avtaltArbeidstimerPerUke: 37.5
// 	},
// 	antallTimerForTimeloennet: [],
// 	permisjon: [],
// 	utenlandsopphold: []
// }

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
	permisjonOgPermittering: ''
}
