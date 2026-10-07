import { addMonths, format, getDaysInMonth } from 'date-fns'
import { nb } from 'date-fns/locale'
import { MONTH_NAMES, type MonthName } from '@/pages/adminPages/Dashboard/dashboardFeilUtils'
import {
	asNumber,
	MONTH_SCOPE_ALL,
	MONTH_SCOPE_LAST_12,
} from '@/pages/adminPages/Dashboard/dashboardUtils'
import {
	type BrukeradferdKriterium,
	type MinSideBestillingerDTO,
} from '@/utils/hooks/useBrukerStatistikk'
import { detaljNoekkelLabel, fagsystemLabel } from './fagsystemLabels'

export const INGEN_DATA = 'Ingen data'
const ANDRE_FAGSYSTEMER = 'Andre'
const SKJULTE_FAGSYSTEMER = new Set(['Beskrivelse'])

export type MonthScope = typeof MONTH_SCOPE_LAST_12 | typeof MONTH_SCOPE_ALL

export type Periode = {
	year: number
	month: number
}

export type MaanedPunkt = Periode & {
	key: string
	visning: string
	nyeBestillinger: number
	gjenopprettinger: number
	nyePersoner: number
}

export type FagsystemSum = {
	fagsystem: string
	label: string
	antall: number
}

export type DagFagsystemSerie = {
	fagsystem: string
	label: string
	data: number[]
}

export type DagFagsystemMatrise = {
	dager: string[]
	serier: DagFagsystemSerie[]
}

export type DetaljType = 'valg' | 'antall'

const DETALJ_TYPE_REKKEFOLGE: Record<DetaljType, number> = { valg: 0, antall: 1 }

export type DetaljPunkt = {
	noekkel: string
	label: string
	antall: number
	type: DetaljType
}

const erGyldigPeriode = (year: number, month: number) =>
	Number.isInteger(year) && Number.isInteger(month) && month >= 1 && month <= 12

export const parsePeriode = (periode: MinSideBestillingerDTO['periode']): Periode | null => {
	if (Array.isArray(periode) && periode.length >= 2) {
		const [year, month] = periode
		return erGyldigPeriode(year, month) ? { year, month } : null
	}
	if (typeof periode === 'string') {
		const match = /^(\d{4})-(\d{2})/.exec(periode)
		if (!match) {
			return null
		}
		const year = Number(match[1])
		const month = Number(match[2])
		return erGyldigPeriode(year, month) ? { year, month } : null
	}
	return null
}

export const periodeKey = ({ year, month }: Periode) => `${year}-${String(month).padStart(2, '0')}`

export const periodeTilMaanedNavn = ({ month }: Periode): MonthName => MONTH_NAMES[month - 1]

const periodeTilDato = ({ year, month }: Periode) => new Date(year, month - 1, 1)

const datoTilPeriode = (dato: Date): Periode => ({
	year: dato.getFullYear(),
	month: dato.getMonth() + 1,
})

const storForbokstav = (tekst: string) => tekst.charAt(0).toUpperCase() + tekst.slice(1)

export const periodeVisning = (periode: Periode) =>
	format(periodeTilDato(periode), 'MMM yyyy', { locale: nb })

export const periodeVisningLang = (periode: Periode) =>
	storForbokstav(format(periodeTilDato(periode), 'LLLL yyyy', { locale: nb }))

export const maanedVisning = (periode: Periode) =>
	storForbokstav(format(periodeTilDato(periode), 'LLLL', { locale: nb }))

export const harAktivitet = (punkt: MaanedPunkt) =>
	punkt.nyeBestillinger > 0 || punkt.gjenopprettinger > 0

const tomtMaanedPunkt = (periode: Periode): MaanedPunkt => ({
	...periode,
	key: periodeKey(periode),
	visning: periodeVisning(periode),
	nyeBestillinger: 0,
	gjenopprettinger: 0,
	nyePersoner: 0,
})

export const toMaanedPunkter = (
	oversikt: MinSideBestillingerDTO[],
	referanseDato: Date = new Date(),
): MaanedPunkt[] => {
	const punktPerKey = new Map<string, MaanedPunkt>()

	oversikt.forEach((rad) => {
		const periode = parsePeriode(rad.periode)
		if (!periode) {
			return
		}
		const key = periodeKey(periode)
		const punkt = punktPerKey.get(key) ?? tomtMaanedPunkt(periode)
		punktPerKey.set(key, {
			...punkt,
			nyeBestillinger: punkt.nyeBestillinger + asNumber(rad.antallNyBestillinger),
			gjenopprettinger: punkt.gjenopprettinger + asNumber(rad.antallGjenopprettinger),
			nyePersoner: punkt.nyePersoner + asNumber(rad.antallNyePersoner),
		})
	})

	if (punktPerKey.size === 0) {
		return []
	}

	const sorterteKeys = Array.from(punktPerKey.keys()).sort()
	const foerstePeriode = punktPerKey.get(sorterteKeys[0]) as MaanedPunkt
	const sistePeriode = punktPerKey.get(sorterteKeys[sorterteKeys.length - 1]) as MaanedPunkt
	const referansePeriode = datoTilPeriode(referanseDato)
	const sluttPeriode =
		periodeKey(referansePeriode) > sistePeriode.key ? referansePeriode : sistePeriode

	const punkter: MaanedPunkt[] = []
	let gjeldende = periodeTilDato(foerstePeriode)
	const slutt = periodeTilDato(sluttPeriode)
	while (gjeldende <= slutt) {
		const periode = datoTilPeriode(gjeldende)
		punkter.push(punktPerKey.get(periodeKey(periode)) ?? tomtMaanedPunkt(periode))
		gjeldende = addMonths(gjeldende, 1)
	}
	return punkter
}

export const filterMaanedPunkter = (punkter: MaanedPunkt[], scope: MonthScope) =>
	scope === MONTH_SCOPE_ALL ? punkter : punkter.slice(-12)

export const toAarOptions = (aktivePerioder: MaanedPunkt[]): string[] =>
	Array.from(new Set(aktivePerioder.map((punkt) => String(punkt.year)))).sort((a, b) =>
		b.localeCompare(a),
	)

export const toMaanedOptions = (aktivePerioder: MaanedPunkt[], year: number) =>
	aktivePerioder
		.filter((punkt) => punkt.year === year)
		.map((punkt) => ({ value: punkt.key, label: maanedVisning(punkt) }))

const synligeKriterier = (kriterier?: BrukeradferdKriterium[] | null) =>
	(kriterier ?? []).filter((kriterium) => !SKJULTE_FAGSYSTEMER.has(kriterium.fagsystem))

const sorterSummer = (a: FagsystemSum, b: FagsystemSum) =>
	b.antall - a.antall || a.label.localeCompare(b.label, 'nb')

export const toFagsystemSummer = (detaljert: MinSideBestillingerDTO[]): FagsystemSum[] => {
	const summer = new Map<string, number>()
	detaljert.forEach((dag) =>
		synligeKriterier(dag.kriterier).forEach((kriterium) =>
			summer.set(
				kriterium.fagsystem,
				(summer.get(kriterium.fagsystem) ?? 0) + asNumber(kriterium.antall),
			),
		),
	)
	return Array.from(summer.entries())
		.map(([fagsystem, antall]) => ({ fagsystem, label: fagsystemLabel(fagsystem), antall }))
		.sort(sorterSummer)
}

export const toFagsystemSummerUtenIngenData = (summer: FagsystemSum[]) =>
	summer.filter((sum) => sum.fagsystem !== INGEN_DATA)

const dagIMaaned = (dato?: string | null) => {
	const dag = Number(dato?.slice(8, 10))
	return Number.isInteger(dag) && dag > 0 ? dag : null
}

export const toDagFagsystemMatrise = (
	detaljert: MinSideBestillingerDTO[],
	periode: Periode,
	maksAntallSerier = 6,
): DagFagsystemMatrise => {
	const antallDager = getDaysInMonth(periodeTilDato(periode))
	const dager = Array.from({ length: antallDager }, (_, index) =>
		String(index + 1).padStart(2, '0'),
	)
	const summer = toFagsystemSummer(detaljert)
	const trengerAndre = summer.length > maksAntallSerier
	const egneSerier = trengerAndre ? summer.slice(0, maksAntallSerier - 1) : summer
	const egneFagsystemer = new Set(egneSerier.map((sum) => sum.fagsystem))

	const serier: DagFagsystemSerie[] = egneSerier.map((sum) => ({
		fagsystem: sum.fagsystem,
		label: sum.label,
		data: new Array(antallDager).fill(0),
	}))
	const andreSerie: DagFagsystemSerie = {
		fagsystem: ANDRE_FAGSYSTEMER,
		label: ANDRE_FAGSYSTEMER,
		data: new Array(antallDager).fill(0),
	}

	detaljert.forEach((dag) => {
		const dagNummer = dagIMaaned(dag.dato)
		if (!dagNummer || dagNummer > antallDager) {
			return
		}
		synligeKriterier(dag.kriterier).forEach((kriterium) => {
			const serie = egneFagsystemer.has(kriterium.fagsystem)
				? serier.find((egenSerie) => egenSerie.fagsystem === kriterium.fagsystem)
				: andreSerie
			if (serie) {
				serie.data[dagNummer - 1] += asNumber(kriterium.antall)
			}
		})
	})

	return { dager, serier: trengerAndre ? [...serier, andreSerie] : serier }
}

export const parseDetaljNoekkel = (noekkel: string): { label: string; type: DetaljType } => {
	const skilletegnIndex = noekkel.lastIndexOf('-')
	if (skilletegnIndex <= 0) {
		return { label: detaljNoekkelLabel(noekkel), type: 'antall' }
	}
	const navnLabel = detaljNoekkelLabel(noekkel.slice(0, skilletegnIndex))
	const verdi = noekkel.slice(skilletegnIndex + 1)
	if (verdi === 'true') {
		return { label: navnLabel, type: 'valg' }
	}
	if (verdi === 'false') {
		return {
			label: `Ikke ${navnLabel.charAt(0).toLowerCase()}${navnLabel.slice(1)}`,
			type: 'valg',
		}
	}
	return { label: `${navnLabel}: ${verdi}`, type: 'valg' }
}

export const toFagsystemerMedDetaljer = (detaljert: MinSideBestillingerDTO[]): FagsystemSum[] => {
	const medDetaljer = new Set<string>()
	detaljert.forEach((dag) =>
		synligeKriterier(dag.kriterier).forEach((kriterium) => {
			if (kriterium.detaljer && Object.keys(kriterium.detaljer).length > 0) {
				medDetaljer.add(kriterium.fagsystem)
			}
		}),
	)
	return toFagsystemSummer(detaljert).filter((sum) => medDetaljer.has(sum.fagsystem))
}

export const toFagsystemDetaljer = (
	detaljert: MinSideBestillingerDTO[],
	fagsystem: string,
): DetaljPunkt[] => {
	const summer = new Map<string, number>()
	detaljert.forEach((dag) =>
		synligeKriterier(dag.kriterier)
			.filter((kriterium) => kriterium.fagsystem === fagsystem)
			.forEach((kriterium) =>
				Object.entries(kriterium.detaljer ?? {}).forEach(([noekkel, antall]) =>
					summer.set(noekkel, (summer.get(noekkel) ?? 0) + asNumber(antall)),
				),
			),
	)

	return Array.from(summer.entries())
		.map(([noekkel, antall]) => ({ noekkel, antall, ...parseDetaljNoekkel(noekkel) }))
		.sort(
			(a, b) =>
				DETALJ_TYPE_REKKEFOLGE[a.type] - DETALJ_TYPE_REKKEFOLGE[b.type] ||
				b.antall - a.antall ||
				a.label.localeCompare(b.label, 'nb'),
		)
}
