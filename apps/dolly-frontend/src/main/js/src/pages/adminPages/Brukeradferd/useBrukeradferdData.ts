import { useMemo, useState } from 'react'
import { useDashboardBrukeradferdForMonths, useDashboardOversikt } from '@/utils/hooks/useDashboard'
import {
	buildFeilPeriodView,
	toFeilPeriodeOptions,
} from '@/pages/adminPages/Dashboard/dashboardFeilUtils'
import { makeYearChangeHandler } from '@/pages/adminPages/Dashboard/dashboardUtils'
import {
	filtrerAdferd,
	filtrerPaaPunkt,
	granularitetForVisning,
	sorterAdferd,
	toAdferdTidView,
	toFagsystemDetaljer,
	toFagsystemTotaler,
	toMaanederForVisning,
	toNoekkeltall,
	toPersonNoekkeltall,
	toPersonTidView,
} from './brukeradferdUtils'
import { type PeriodeVisning, toPeriodeTittel } from '@/pages/statistikk/brukerStatistikkUtils'
import {
	createAdferdOverTidChartOptions,
	createFagsystemFordelingChartOptions,
	createNyePersonerChartOptions,
} from './brukeradferdChartOptions'
import { createFagsystemDetaljerChartOptions } from '@/pages/statistikk/brukerStatistikkChartOptions'
import { type BrukeradferdMockData, createBrukeradferdMockData } from './brukeradferdMockData'

export const FAGSYSTEM_DETALJER_ID = 'brukeradferd-fagsystem-detaljer'

export const useBrukeradferdData = () => {
	const [mockData, setMockData] = useState<BrukeradferdMockData | null>(null)
	const [valgtInterval, setValgtInterval] = useState('')
	const [visning, setVisning] = useState<PeriodeVisning>('maaned')
	const [valgtPunkt, setValgtPunkt] = useState<string | null>(null)
	const [valgtFagsystem, setValgtFagsystem] = useState<string | null>(null)

	const mockModeEnabled = mockData !== null

	const { dashboardOversikt, loadingDashboardOversikt, dashboardOversiktError } =
		useDashboardOversikt(!mockModeEnabled)
	const aktivOversikt = mockData ? mockData.oversikt : dashboardOversikt

	const periodeOptions = useMemo(() => toFeilPeriodeOptions(aktivOversikt), [aktivOversikt])
	const tilgjengeligeIntervaller = useMemo(
		() => periodeOptions.map((option) => option.interval),
		[periodeOptions],
	)
	const effektivtInterval = tilgjengeligeIntervaller.includes(valgtInterval)
		? valgtInterval
		: (tilgjengeligeIntervaller.at(-1) ?? '')

	const periodView = useMemo(
		() => buildFeilPeriodView(periodeOptions, effektivtInterval),
		[periodeOptions, effektivtInterval],
	)

	const maaneder = useMemo(
		() => toMaanederForVisning(effektivtInterval, visning, tilgjengeligeIntervaller),
		[effektivtInterval, visning, tilgjengeligeIntervaller],
	)

	const { brukeradferd, loadingBrukeradferd, brukeradferdError } =
		useDashboardBrukeradferdForMonths(mockModeEnabled ? [] : maaneder)

	const alleRader = useMemo(() => {
		if (!mockData) {
			return sorterAdferd(brukeradferd)
		}
		const intervaller = new Set(maaneder.map((maaned) => maaned.interval))
		return sorterAdferd(mockData.adferd.filter((rad) => intervaller.has(rad.dato.slice(0, 7))))
	}, [mockData, brukeradferd, maaneder])
	const aktivAdferd = useMemo(() => filtrerAdferd(alleRader), [alleRader])

	const granularitet = granularitetForVisning(visning)

	const personNoekkeltall = useMemo(() => toPersonNoekkeltall(alleRader), [alleRader])
	const personTidView = useMemo(
		() => toPersonTidView(alleRader, granularitet),
		[alleRader, granularitet],
	)

	const fagsystemTotaler = useMemo(() => toFagsystemTotaler(aktivAdferd), [aktivAdferd])
	const noekkeltall = useMemo(
		() => toNoekkeltall(aktivAdferd, fagsystemTotaler),
		[aktivAdferd, fagsystemTotaler],
	)
	const tidView = useMemo(
		() => toAdferdTidView(aktivAdferd, granularitet),
		[aktivAdferd, granularitet],
	)
	const fagsystemDetaljer = useMemo(() => toFagsystemDetaljer(aktivAdferd), [aktivAdferd])

	const valgtPunktTotaler = useMemo(
		() =>
			valgtPunkt ? toFagsystemTotaler(filtrerPaaPunkt(aktivAdferd, valgtPunkt, granularitet)) : [],
		[aktivAdferd, valgtPunkt, granularitet],
	)
	const valgtPunktPersoner = useMemo(
		() =>
			valgtPunkt ? toPersonNoekkeltall(filtrerPaaPunkt(alleRader, valgtPunkt, granularitet)) : null,
		[alleRader, valgtPunkt, granularitet],
	)
	const valgtPunktVisning =
		tidView.punkter.find((punkt) => punkt.noekkel === valgtPunkt)?.visning ??
		personTidView.find((punkt) => punkt.noekkel === valgtPunkt)?.visning ??
		''

	const aktivtFagsystem =
		fagsystemDetaljer.find((detaljer) => detaljer.fagsystem === valgtFagsystem) ??
		fagsystemDetaljer[0] ??
		null

	const visFagsystemDetaljer = (fagsystem: string) => {
		setValgtFagsystem(fagsystem)
		document
			.getElementById(FAGSYSTEM_DETALJER_ID)
			?.scrollIntoView({ behavior: 'smooth', block: 'start' })
	}

	const fordelingChartOptions = useMemo(() => {
		const fagsystemerMedDetaljer = new Set(fagsystemDetaljer.map((detaljer) => detaljer.fagsystem))
		return createFagsystemFordelingChartOptions(
			fagsystemTotaler,
			visFagsystemDetaljer,
			(fagsystem) => fagsystemerMedDetaljer.has(fagsystem),
		)
	}, [fagsystemTotaler, fagsystemDetaljer])
	const detaljValgChartOptions = useMemo(
		() =>
			aktivtFagsystem && aktivtFagsystem.valg.length > 0
				? createFagsystemDetaljerChartOptions(
						aktivtFagsystem.valg,
						aktivtFagsystem.label,
						'valg',
						'valgt periode',
					)
				: null,
		[aktivtFagsystem],
	)
	const detaljAntallChartOptions = useMemo(
		() =>
			aktivtFagsystem && aktivtFagsystem.oppfoeringer.length > 0
				? createFagsystemDetaljerChartOptions(
						aktivtFagsystem.oppfoeringer,
						aktivtFagsystem.label,
						'antall',
						'valgt periode',
					)
				: null,
		[aktivtFagsystem],
	)
	const overTidChartOptions = useMemo(
		() => createAdferdOverTidChartOptions(tidView, granularitet, setValgtPunkt),
		[tidView, granularitet],
	)
	const nyePersonerChartOptions = useMemo(
		() => createNyePersonerChartOptions(personTidView, granularitet, setValgtPunkt),
		[personTidView, granularitet],
	)

	const nullstillValg = () => {
		setValgtPunkt(null)
		setValgtFagsystem(null)
	}

	const velgInterval = (interval: string) => {
		setValgtInterval(interval)
		nullstillValg()
	}

	const velgMaaned = (interval: string) => {
		velgInterval(interval)
		setVisning('maaned')
	}

	const valgtAar = Number(periodView.selectedYear)
	const intervalVisning =
		periodeOptions.find((option) => option.interval === effektivtInterval)?.intervalVisning ?? ''
	const periodeTittel = toPeriodeTittel(visning, valgtAar, intervalVisning)

	return {
		mockModeEnabled,
		onSeedMockData: () => {
			setMockData(createBrukeradferdMockData())
			setValgtInterval('')
			nullstillValg()
		},
		onShowRealData: () => {
			setMockData(null)
			setValgtInterval('')
			nullstillValg()
		},

		loadingPerioder: loadingDashboardOversikt && !mockModeEnabled,
		loadingAdferd: (loadingDashboardOversikt || loadingBrukeradferd) && !mockModeEnabled,
		oversiktError: mockModeEnabled ? undefined : dashboardOversiktError,
		brukeradferdError: mockModeEnabled ? undefined : brukeradferdError,

		yearOptions: periodView.yearOptions,
		selectedYear: periodView.selectedYear,
		onYearChange: makeYearChangeHandler(periodView.intervalOptions, velgInterval),
		monthOptions: periodView.monthOptions,
		selectedInterval: effektivtInterval,
		onIntervalChange: velgMaaned,
		visning,
		onVisningChange: (nyVisning: PeriodeVisning) => {
			setVisning(nyVisning)
			nullstillValg()
		},
		periodeTittel,
		granularitet,

		noekkeltall,
		personNoekkeltall,
		personTidPunkter: personTidView,
		nyePersonerChartOptions,
		fagsystemTotaler,
		fordelingChartOptions,

		harTidsdata: tidView.punkter.length > 0,
		overTidChartOptions,
		valgtPunkt,
		valgtPunktVisning,
		valgtPunktTotaler,
		valgtPunktPersoner,
		onLukkValgtPunkt: () => setValgtPunkt(null),

		fagsystemOptions: fagsystemDetaljer.map((detaljer) => ({
			value: detaljer.fagsystem,
			label: detaljer.label,
		})),
		valgtFagsystem: aktivtFagsystem?.fagsystem ?? null,
		valgtFagsystemLabel: aktivtFagsystem?.label ?? '',
		onFagsystemChange: setValgtFagsystem,
		detaljValgChartOptions,
		detaljAntallChartOptions,
	}
}

export type BrukeradferdData = ReturnType<typeof useBrukeradferdData>
