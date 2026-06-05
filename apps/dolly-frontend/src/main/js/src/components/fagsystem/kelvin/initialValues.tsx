export const kelvinAapPath = 'kelvinAap'

export const initialKelvinAap = {
	andreUtbetalinger: {
		afp: {
			hvemBetaler: '',
		},
		loenn: '',
		stoenad: ['NEI'],
	},
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
