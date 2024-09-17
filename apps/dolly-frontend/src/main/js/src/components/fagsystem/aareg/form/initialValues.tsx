import subYears from 'date-fns/subYears'
import { runningE2ETest } from '@/service/services/Request'

export const initialForenkletOppgjoersordningOrg = {
	arbeidsgiver: {
		aktoertype: 'ORG',
		orgnummer: '',
	},
	ansettelsesPeriode: {
		fom: subYears(new Date(), 20),
		tom: null,
		sluttaarsak: null,
	},
	arbeidsavtale: {
		yrke: '',
	},
	navArbeidsforholdPeriode: null,
}

export const initialForenkletOppgjoersordningPers = {
	arbeidsgiver: {
		aktoertype: 'PERS',
		ident: '',
	},
	ansettelsesPeriode: {
		fom: subYears(new Date(), 20),
		tom: null,
		sluttaarsak: null,
	},
	arbeidsavtale: {
		yrke: '',
	},
	navArbeidsforholdPeriode: null,
}

export const initialArbeidsforholdOrg = {
	arbeidsforholdstype: '',
	arbeidsgiver: {
		aktoertype: 'ORG',
		orgnummer: '',
	},
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
	navArbeidsforholdPeriode: undefined,
	antallTimerForTimeloennet: [],
	utenlandsopphold: [],
	permisjon: [],
	permittering: [],
}

export const initialArbeidsforholdPers = {
	arbeidsgiver: {
		aktoertype: 'PERS',
		ident: '',
	},
	arbeidsforholdID: '',
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
	navArbeidsforholdPeriode: null,
}

export const initialPeriode = {
	fom: null,
	tom: null,
	periode: [],
}

export const initialPensjonInntekt = {
	fomAar: runningE2ETest() ? new Date().getFullYear() - 10 : null,
	tomAar: runningE2ETest() ? new Date().getFullYear() : null,
	belop: runningE2ETest() ? '12345' : '',
	redusertMedGrunnbelop: true,
}

export const initialPensjonGenerertInntekt = {
	generer: {
		fomAar: null,
		averageG: 1.5,
		tillatInntektUnder1G: false,
	},
}

export const initialAmelding = [
	{
		maaned: null,
		arbeidsforhold: [],
	},
]

export const initialValues = {
	arbeidsforholdstype: '',
	genererPeriode: initialPeriode,
	amelding: initialAmelding,
}

export const initialAaregOrg = {
	arbeidsforholdstype: '',
	...initialArbeidsforholdOrg,
}

export const initialAaregPers = {
	arbeidsforholdstype: '',
	...initialArbeidsforholdPers,
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
