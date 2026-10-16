import { format, parseISO } from 'date-fns'
import { nb } from 'date-fns/locale'
import {
	type DashboardBrukeradferdDTO,
	type DashboardBrukeradferdKriterium,
} from '@/utils/hooks/useDashboard'
import { MONTH_NAMES, type MonthName } from '@/pages/adminPages/Dashboard/dashboardFeilUtils'
import { fagsystemLabel } from '@/pages/statistikk/fagsystemLabels'
import {
	type DetaljPunkt,
	parseDetaljNoekkel,
	type PeriodeVisning,
} from '@/pages/statistikk/brukerStatistikkUtils'

export const USPESIFISERT = 'Uspesifisert'
const SKJULTE_FAGSYSTEMER = new Set([USPESIFISERT, 'Ingen data', 'Beskrivelse'])

export type AdferdGranularitet = 'DAG' | 'MAANED'

export const granularitetForVisning = (visning: PeriodeVisning): AdferdGranularitet =>
	visning === 'maaned' ? 'DAG' : 'MAANED'

const MYK_BINDESTREK = '\u00AD'

const KORTE_FAGSYSTEM_LABELS: Record<string, string> = {
	Arbeidssoekerregisteret: `Arbeidssøker${MYK_BINDESTREK}registeret`,
	Brregstub: `Brønnøysund${MYK_BINDESTREK}registrene`,
	EtterlatteYtelser: `Etterlatte${MYK_BINDESTREK}ytelser`,
	Instdata: `Institusjons${MYK_BINDESTREK}opphold`,
	InstdataKdi: `Institusjons${MYK_BINDESTREK}opphold (KDI)`,
	Krrstub: 'KRR',
	Oppfoelgingsvedtak14a: `Oppfølgings${MYK_BINDESTREK}vedtak § 14 a`,
	SigrunstubPensjonsgivende: `Pensjons${MYK_BINDESTREK}givende inntekt`,
	SigrunstubSummertSkattegrunnlag: `Summert skatte${MYK_BINDESTREK}grunnlag`,
	Skjerming: `Skjermings${MYK_BINDESTREK}registeret`,
}

export const fagsystemKortLabel = (fagsystem: string): string =>
	KORTE_FAGSYSTEM_LABELS[fagsystem] ?? fagsystemLabel(fagsystem)

export const erForkortetLabel = (fagsystem: string): boolean =>
	fagsystemKortLabel(fagsystem).replaceAll(MYK_BINDESTREK, '') !== fagsystemLabel(fagsystem)

const asAntall = (value: number | string | null | undefined): number => {
	if (typeof value === 'number') {
		return Number.isFinite(value) ? value : 0
	}
	const parsed = Number(value)
	return Number.isFinite(parsed) ? parsed : 0
}

export const toMaanederForVisning = (
	selectedInterval: string,
	visning: PeriodeVisning,
	tilgjengeligeIntervaller: string[],
): { year: number; month: MonthName; interval: string }[] => {
	if (!tilgjengeligeIntervaller.includes(selectedInterval)) {
		return []
	}
	const intervaller =
		visning === 'aar'
			? tilgjengeligeIntervaller
					.filter((interval) => interval.slice(0, 4) === selectedInterval.slice(0, 4))
					.sort()
			: [selectedInterval]
	return intervaller.map((interval) => {
		const maanedDato = parseISO(`${interval}-01`)
		return {
			year: maanedDato.getFullYear(),
			month: MONTH_NAMES[maanedDato.getMonth()],
			interval,
		}
	})
}

export const filtrerAdferd = (rader: DashboardBrukeradferdDTO[]): DashboardBrukeradferdDTO[] =>
	rader
		.map((rad) => ({
			...rad,
			kriterier: (rad.kriterier ?? []).filter(
				(kriterium) =>
					!SKJULTE_FAGSYSTEMER.has(kriterium.fagsystem) && asAntall(kriterium.antall) > 0,
			),
		}))
		.filter((rad) => Boolean(rad.dato) && rad.kriterier.length > 0)
		.sort((a, b) => a.dato.localeCompare(b.dato))

const alleKriterier = (rader: DashboardBrukeradferdDTO[]): DashboardBrukeradferdKriterium[] =>
	rader.flatMap((rad) => rad.kriterier)

export type FagsystemTotal = {
	fagsystem: string
	label: string
	antall: number
	andel: number
}

export const toFagsystemTotaler = (rader: DashboardBrukeradferdDTO[]): FagsystemTotal[] => {
	const totaler = new Map<string, number>()
	alleKriterier(rader).forEach((kriterium) => {
		totaler.set(
			kriterium.fagsystem,
			(totaler.get(kriterium.fagsystem) ?? 0) + asAntall(kriterium.antall),
		)
	})
	const sum = [...totaler.values()].reduce((acc, antall) => acc + antall, 0)
	return [...totaler.entries()]
		.map(([fagsystem, antall]) => ({
			fagsystem,
			label: fagsystemLabel(fagsystem),
			antall,
			andel: sum > 0 ? antall / sum : 0,
		}))
		.sort((a, b) => b.antall - a.antall || a.label.localeCompare(b.label))
}

export type AdferdTidPunkt = {
	noekkel: string
	kategori: string
	visning: string
	total: number
	perFagsystem: Record<string, number>
}

export type AdferdTidView = {
	punkter: AdferdTidPunkt[]
	fagsystemNoekler: string[]
}

const tilPunktNoekkel = (dato: string, granularitet: AdferdGranularitet) =>
	granularitet === 'DAG' ? dato.slice(0, 10) : dato.slice(0, 7)

const formaterPunkt = (noekkel: string, granularitet: AdferdGranularitet) => {
	const dato = parseISO(granularitet === 'DAG' ? noekkel : `${noekkel}-01`)
	if (Number.isNaN(dato.getTime())) {
		return { kategori: noekkel, visning: noekkel }
	}
	if (granularitet === 'DAG') {
		return { kategori: format(dato, 'dd.MM'), visning: format(dato, 'dd.MM.yyyy') }
	}
	return {
		kategori: format(dato, 'MMM yy', { locale: nb }),
		visning: format(dato, 'MMMM yyyy', { locale: nb }),
	}
}

export const toAdferdTidView = (
	rader: DashboardBrukeradferdDTO[],
	granularitet: AdferdGranularitet,
): AdferdTidView => {
	const fagsystemNoekler = toFagsystemTotaler(rader).map((total) => total.fagsystem)

	const punktMap = new Map<string, AdferdTidPunkt>()
	rader.forEach((rad) => {
		const noekkel = tilPunktNoekkel(rad.dato, granularitet)
		const punkt = punktMap.get(noekkel) ?? {
			noekkel,
			...formaterPunkt(noekkel, granularitet),
			total: 0,
			perFagsystem: {},
		}
		rad.kriterier.forEach((kriterium) => {
			const antall = asAntall(kriterium.antall)
			punkt.perFagsystem[kriterium.fagsystem] =
				(punkt.perFagsystem[kriterium.fagsystem] ?? 0) + antall
			punkt.total += antall
		})
		punktMap.set(noekkel, punkt)
	})

	return {
		punkter: [...punktMap.values()].sort((a, b) => a.noekkel.localeCompare(b.noekkel)),
		fagsystemNoekler,
	}
}

export const filtrerPaaPunkt = (
	rader: DashboardBrukeradferdDTO[],
	punktNoekkel: string,
	granularitet: AdferdGranularitet,
): DashboardBrukeradferdDTO[] =>
	rader.filter((rad) => tilPunktNoekkel(rad.dato, granularitet) === punktNoekkel)

export type FagsystemDetaljer = {
	fagsystem: string
	label: string
	antall: number
	valg: DetaljPunkt[]
	oppfoeringer: DetaljPunkt[]
}

type FagsystemSamling = {
	antall: number
	detaljer: Map<string, number>
}

const sorterDetaljPunkter = (a: DetaljPunkt, b: DetaljPunkt) =>
	b.antall - a.antall || a.label.localeCompare(b.label, 'nb')

const tilDetaljPunkter = (detaljer: Map<string, number>): DetaljPunkt[] =>
	[...detaljer.entries()]
		.map(([noekkel, antall]) => ({ noekkel, antall, ...parseDetaljNoekkel(noekkel) }))
		.sort(sorterDetaljPunkter)

export const toFagsystemDetaljer = (rader: DashboardBrukeradferdDTO[]): FagsystemDetaljer[] => {
	const perFagsystem = new Map<string, FagsystemSamling>()

	alleKriterier(rader).forEach((kriterium) => {
		const samling = perFagsystem.get(kriterium.fagsystem) ?? {
			antall: 0,
			detaljer: new Map<string, number>(),
		}
		samling.antall += asAntall(kriterium.antall)
		Object.entries(kriterium.detaljer ?? {}).forEach(([noekkel, verdi]) => {
			const antall = asAntall(verdi)
			if (antall > 0) {
				samling.detaljer.set(noekkel, (samling.detaljer.get(noekkel) ?? 0) + antall)
			}
		})
		perFagsystem.set(kriterium.fagsystem, samling)
	})

	return [...perFagsystem.entries()]
		.filter(([, samling]) => samling.detaljer.size > 0)
		.map(([fagsystem, samling]) => {
			const punkter = tilDetaljPunkter(samling.detaljer)
			return {
				fagsystem,
				label: fagsystemLabel(fagsystem),
				antall: samling.antall,
				valg: punkter.filter((punkt) => punkt.type === 'valg'),
				oppfoeringer: punkter.filter((punkt) => punkt.type === 'antall'),
			}
		})
		.sort((a, b) => b.antall - a.antall || a.label.localeCompare(b.label, 'nb'))
}

export type BrukeradferdNoekkeltall = {
	totaltFagsystemvalg: number
	fagsystemerIBruk: number
	mestBrukt: FagsystemTotal | null
	dagerMedBestillinger: number
}

export const toNoekkeltall = (
	rader: DashboardBrukeradferdDTO[],
	totaler: FagsystemTotal[],
): BrukeradferdNoekkeltall => ({
	totaltFagsystemvalg: totaler.reduce((sum, total) => sum + total.antall, 0),
	fagsystemerIBruk: totaler.length,
	mestBrukt: totaler[0] ?? null,
	dagerMedBestillinger: new Set(rader.map((rad) => rad.dato.slice(0, 10))).size,
})

export const formaterAndel = (andel: number): string =>
	`${(andel * 100).toLocaleString('nb-NO', { maximumFractionDigits: 1 })} %`

export type PersonNoekkeltall = {
	nyeBestillinger: number
	nyePersoner: number
	dollyPersoner: number
	testnorgePersoner: number
}

const tomPersonNoekkeltall = (): PersonNoekkeltall => ({
	nyeBestillinger: 0,
	nyePersoner: 0,
	dollyPersoner: 0,
	testnorgePersoner: 0,
})

const leggTilPersontall = (summer: PersonNoekkeltall, rad: DashboardBrukeradferdDTO) => {
	summer.nyeBestillinger += asAntall(rad.antallNyeBestillinger)
	summer.nyePersoner += asAntall(rad.antallNyePersoner)
	summer.dollyPersoner += asAntall(rad.andelOpprettedeDollyPersoner)
	summer.testnorgePersoner += asAntall(rad.andelImporterteTestnorgePersoner)
}

export const sorterAdferd = (rader: DashboardBrukeradferdDTO[]): DashboardBrukeradferdDTO[] =>
	rader.filter((rad) => Boolean(rad.dato)).sort((a, b) => a.dato.localeCompare(b.dato))

export const toPersonNoekkeltall = (rader: DashboardBrukeradferdDTO[]): PersonNoekkeltall =>
	rader.reduce((summer, rad) => {
		leggTilPersontall(summer, rad)
		return summer
	}, tomPersonNoekkeltall())

export type PersonTidPunkt = PersonNoekkeltall & {
	noekkel: string
	kategori: string
	visning: string
}

export const toPersonTidView = (
	rader: DashboardBrukeradferdDTO[],
	granularitet: AdferdGranularitet,
): PersonTidPunkt[] => {
	const punktMap = new Map<string, PersonTidPunkt>()
	rader.forEach((rad) => {
		const noekkel = tilPunktNoekkel(rad.dato, granularitet)
		const punkt = punktMap.get(noekkel) ?? {
			noekkel,
			...formaterPunkt(noekkel, granularitet),
			...tomPersonNoekkeltall(),
		}
		leggTilPersontall(punkt, rad)
		punktMap.set(noekkel, punkt)
	})
	return [...punktMap.values()]
		.filter((punkt) => punkt.nyePersoner > 0 || punkt.nyeBestillinger > 0)
		.sort((a, b) => a.noekkel.localeCompare(b.noekkel))
}
