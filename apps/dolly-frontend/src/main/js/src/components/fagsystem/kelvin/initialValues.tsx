import { date } from 'yup'

export const kelvinAapPath = 'kelvinAap'

export const initialKelvinAap = {
	andreUtbetalinger: {
		afp: {
			hvemBetaler: '',
		},
		loenn: '',
		stoenad: ['NEI'],
	},
	soeknadsdato: new Date(),
	automatiskMeldekort: false,
	erStudent: false,
	harMedlemskap: false,
	harYrkesskade: false,
}

export type KelvinAapTypes = {
	andreUtbetalinger: {
		afp: {
			hvemBetaler?: string
		}
		loenn?: string
		stoenad: Array<string>
	}
	soeknadsdato: Date
	automatiskMeldekort: boolean
	erStudent: boolean
	harMedlemskap: boolean
	harYrkesskade: boolean
}

export type KelvinAapVisningTypes = {
	data: {
		behandlingStatus: string
		ferdig: boolean
		saksnummer: string
		soeknad: KelvinAapTypes
	}
	loading: boolean
	harKelvinAapBestilling: boolean
}
