import { format, getDaysInMonth, isWeekend, parseISO, subMonths } from 'date-fns'
import {
	type DashboardBrukeradferdDTO,
	type DashboardBrukeradferdKriterium,
	type DashboardOversiktDTO,
} from '@/utils/hooks/useDashboard'
import { MONTH_NAMES } from '@/pages/adminPages/Dashboard/dashboardFeilUtils'
import { USPESIFISERT } from './brukeradferdUtils'

type MockFagsystem = {
	fagsystem: string
	sannsynlighet: number
	detaljer?: (antall: number) => Record<string, number>
}

const tilfeldig = (min: number, max: number) => Math.floor(Math.random() * (max - min + 1)) + min

const fordel = (antall: number, andel: number) => Math.round(antall * andel)

const MOCK_FAGSYSTEMER: MockFagsystem[] = [
	{
		fagsystem: 'PdlData',
		sannsynlighet: 0.95,
		detaljer: (antall) => {
			const syntetiske = fordel(antall, 0.8)
			return {
				'Syntetisk-true': syntetiske,
				'Syntetisk-false': antall - syntetiske,
				'Alder-true': fordel(antall, 0.6),
				Statsborgerskap: fordel(antall, 0.4),
				Bostedsadresse: fordel(antall, 0.7),
				Sivilstand: fordel(antall, 0.3),
			}
		},
	},
	{
		fagsystem: 'Aareg',
		sannsynlighet: 0.6,
		detaljer: (antall) => ({ 'Antall arbeidsforhold': antall + tilfeldig(0, antall) }),
	},
	{ fagsystem: 'Krrstub', sannsynlighet: 0.5 },
	{
		fagsystem: 'Pensjon',
		sannsynlighet: 0.35,
		detaljer: (antall) => ({
			'PoppInntekt-true': fordel(antall, 0.7),
			'Alderspensjon-true': fordel(antall, 0.3),
			Tjenestepensjon: fordel(antall, 0.2),
		}),
	},
	{
		fagsystem: 'Arena',
		sannsynlighet: 0.3,
		detaljer: (antall) => ({
			'AAP-true': fordel(antall, 0.4),
			'Dagpenger-true': fordel(antall, 0.3),
			'ArenaBrukertype-MED_SERVICEBEHOV': fordel(antall, 0.6),
			'ArenaBrukertype-UTEN_SERVICEBEHOV': fordel(antall, 0.4),
		}),
	},
	{
		fagsystem: 'Bankkonto',
		sannsynlighet: 0.3,
		detaljer: (antall) => ({
			'NorskBankkonto-true': fordel(antall, 0.85),
			'UtenlandskBankkonto-true': fordel(antall, 0.15),
		}),
	},
	{
		fagsystem: 'Inntekter',
		sannsynlighet: 0.25,
		detaljer: (antall) => ({ 'Array-størrelse/antall': antall * tilfeldig(1, 3) }),
	},
	{ fagsystem: 'Udistub', sannsynlighet: 0.15 },
	{ fagsystem: 'Skjerming', sannsynlighet: 0.12 },
	{ fagsystem: 'Sykemelding', sannsynlighet: 0.1 },
	{
		fagsystem: 'Dokarkiv',
		sannsynlighet: 0.1,
		detaljer: (antall) => ({ 'Array-størrelse/antall': antall }),
	},
	{ fagsystem: 'Arbeidssoekerregisteret', sannsynlighet: 0.08 },
	{ fagsystem: 'Skattekort', sannsynlighet: 0.08 },
	{ fagsystem: 'Medl', sannsynlighet: 0.05 },
]

const lagKriterier = (): DashboardBrukeradferdKriterium[] => {
	const kriterier = MOCK_FAGSYSTEMER.filter(
		(mockFagsystem) => Math.random() < mockFagsystem.sannsynlighet,
	).map((mockFagsystem) => {
		const antall = tilfeldig(1, 60)
		return {
			fagsystem: mockFagsystem.fagsystem,
			antall,
			detaljer: mockFagsystem.detaljer?.(antall) ?? {},
		}
	})
	if (Math.random() < 0.3) {
		kriterier.push({ fagsystem: USPESIFISERT, antall: tilfeldig(1, 5), detaljer: {} })
	}
	return kriterier
}

export type BrukeradferdMockData = {
	oversikt: DashboardOversiktDTO[]
	adferd: DashboardBrukeradferdDTO[]
}

export const createBrukeradferdMockData = (antallMaaneder = 14): BrukeradferdMockData => {
	const idag = new Date()
	const oversikt: DashboardOversiktDTO[] = []
	const adferd: DashboardBrukeradferdDTO[] = []

	for (let maanedOffset = antallMaaneder - 1; maanedOffset >= 0; maanedOffset -= 1) {
		const maanedDato = subMonths(idag, maanedOffset)
		const interval = format(maanedDato, 'yyyy-MM')
		const sisteDag = maanedOffset === 0 ? idag.getDate() : getDaysInMonth(maanedDato)

		oversikt.push({
			aar: maanedDato.getFullYear(),
			maaned: MONTH_NAMES[maanedDato.getMonth()],
			totaltAntallPersoner: 0,
			nye: 0,
			gjenopprettede: 0,
		})

		for (let dag = 1; dag <= sisteDag; dag += 1) {
			const dato = `${interval}-${String(dag).padStart(2, '0')}`
			if (isWeekend(parseISO(dato)) && Math.random() < 0.8) {
				continue
			}
			const dollyPersoner = tilfeldig(5, 120)
			const testnorgePersoner = Math.random() < 0.6 ? tilfeldig(0, 40) : 0
			adferd.push({
				dato,
				antallNyeBestillinger: tilfeldig(1, 30),
				antallNyePersoner: dollyPersoner + testnorgePersoner,
				andelOpprettedeDollyPersoner: dollyPersoner,
				andelImporterteTestnorgePersoner: testnorgePersoner,
				kriterier: lagKriterier(),
			})
		}
	}

	return { oversikt, adferd }
}
