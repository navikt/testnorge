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
	type BrukerBestillingerDTO,
} from '@/utils/hooks/useBrukerStatistikk'
import { detaljNoekkelLabel, fagsystemLabel } from './fagsystemLabels'

const USPESIFISERTE_FAGSYSTEMER = new Set(['Uspesifisert', 'Ingen data'])
const ANDRE_FAGSYSTEMER = 'Andre'
const SKJULTE_FAGSYSTEMER = new Set(['Beskrivelse'])
const ANTALL_NOEKLER = new Set(['Array-størrelse/antall', 'Array/matrise antall'])

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
	dollyPersoner: number
	testnorgePersoner: number
}

export type PersonFordeling = {
	dollyPersoner: number
	testnorgePersoner: number
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
	kategorier: string[]
	overskrifter: string[]
	serier: DagFagsystemSerie[]
}

export type DagPersonMatrise = {
	kategorier: string[]
	overskrifter: string[]
	dollyPersoner: number[]
	testnorgePersoner: number[]
	nyeBestillinger: number[]
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

export const parsePeriode = (periode: BrukerBestillingerDTO['periode']): Periode | null => {
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

const maanedVisningKort = (periode: Periode) =>
	format(periodeTilDato(periode), 'MMM', { locale: nb })

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
	dollyPersoner: 0,
	testnorgePersoner: 0,
})

export const toMaanedPunkter = (
	oversikt: BrukerBestillingerDTO[],
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
			nyeBestillinger: punkt.nyeBestillinger + asNumber(rad.antallNyeBestillinger),
			gjenopprettinger: punkt.gjenopprettinger + asNumber(rad.antallGjenopprettedeBestillinger),
			nyePersoner: punkt.nyePersoner + asNumber(rad.antallNyePersoner),
			dollyPersoner: punkt.dollyPersoner + asNumber(rad.andelOpprettedeDollyPersoner),
			testnorgePersoner: punkt.testnorgePersoner + asNumber(rad.andelImporterteTestnorgePersoner),
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
		a.localeCompare(b),
	)

export const toMaanedOptions = (aktivePerioder: MaanedPunkt[], year: number) =>
	aktivePerioder
		.filter((punkt) => punkt.year === year)
		.map((punkt) => ({ value: punkt.key, label: maanedVisningKort(punkt) }))

export type AarSammendrag = {
	year: number
	nyePersoner: number
	dollyPersoner: number
	testnorgePersoner: number
	nyeBestillinger: number
	gjenopprettinger: number
	aktiveMaaneder: number
	mestAktiveMaaned: MaanedPunkt | null
}

const antallBestillinger = (punkt: MaanedPunkt) => punkt.nyeBestillinger + punkt.gjenopprettinger

export const toAarSammendrag = (punkter: MaanedPunkt[]): Map<number, AarSammendrag> => {
	const sammendragPerAar = new Map<number, AarSammendrag>()

	for (const punkt of punkter) {
		let sammendrag = sammendragPerAar.get(punkt.year)
		if (!sammendrag) {
			sammendrag = {
				year: punkt.year,
				nyePersoner: 0,
				dollyPersoner: 0,
				testnorgePersoner: 0,
				nyeBestillinger: 0,
				gjenopprettinger: 0,
				aktiveMaaneder: 0,
				mestAktiveMaaned: null,
			}
			sammendragPerAar.set(punkt.year, sammendrag)
		}
		sammendrag.nyePersoner += punkt.nyePersoner
		sammendrag.dollyPersoner += punkt.dollyPersoner
		sammendrag.testnorgePersoner += punkt.testnorgePersoner
		sammendrag.nyeBestillinger += punkt.nyeBestillinger
		sammendrag.gjenopprettinger += punkt.gjenopprettinger
		if (harAktivitet(punkt)) {
			sammendrag.aktiveMaaneder += 1
			if (
				!sammendrag.mestAktiveMaaned ||
				antallBestillinger(punkt) > antallBestillinger(sammendrag.mestAktiveMaaned)
			) {
				sammendrag.mestAktiveMaaned = punkt
			}
		}
	}

	return sammendragPerAar
}

export const harPersonFordeling = ({ dollyPersoner, testnorgePersoner }: PersonFordeling) =>
	dollyPersoner + testnorgePersoner > 0

export const aarSammendragTittel = (year: number, referanseDato: Date = new Date()) =>
	year === referanseDato.getFullYear() ? `Hittil i ${year}` : `Hele ${year}`

export type PeriodeVisning = 'maaned' | 'aar'

export const erPeriodeVisning = (verdi: string): verdi is PeriodeVisning =>
	verdi === 'maaned' || verdi === 'aar'

export const aarPeriodeTittel = (year: number, referanseDato: Date = new Date()) =>
	year === referanseDato.getFullYear() ? `hittil i ${year}` : `i ${year}`

export const toPeriodeTittel = (
	visning: PeriodeVisning,
	year: number,
	maanedVisning: string,
	referanseDato: Date = new Date(),
): string => {
	if (visning === 'aar') {
		return aarPeriodeTittel(year, referanseDato)
	}
	return maanedVisning ? `i ${maanedVisning}` : ''
}

const synligeKriterier = (kriterier?: BrukeradferdKriterium[] | null) =>
	(kriterier ?? []).filter((kriterium) => !SKJULTE_FAGSYSTEMER.has(kriterium.fagsystem))

const sorterSummer = (a: FagsystemSum, b: FagsystemSum) =>
	b.antall - a.antall || a.label.localeCompare(b.label, 'nb')

export const toFagsystemSummer = (detaljert: BrukerBestillingerDTO[]): FagsystemSum[] => {
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

export const toFagsystemSummerUtenUspesifisert = (summer: FagsystemSum[]) =>
	summer.filter((sum) => !USPESIFISERTE_FAGSYSTEMER.has(sum.fagsystem))

export type Tidsakse = {
	kategorier: string[]
	overskrifter: string[]
	indeksFor: (dato?: string | null) => number | null
}

const dagIMaaned = (dato?: string | null) => {
	const dag = Number(dato?.slice(8, 10))
	return Number.isInteger(dag) && dag > 0 ? dag : null
}

export const dagAkse = (periode: Periode): Tidsakse => {
	const kategorier = Array.from({ length: getDaysInMonth(periodeTilDato(periode)) }, (_, index) =>
		String(index + 1).padStart(2, '0'),
	)
	return {
		kategorier,
		overskrifter: kategorier.map((dag) => `Dag ${dag}`),
		indeksFor: (dato) => {
			const dagNummer = dagIMaaned(dato)
			return dagNummer && dagNummer <= kategorier.length ? dagNummer - 1 : null
		},
	}
}

export const maanedAkse = (year: number, referanseDato: Date = new Date()): Tidsakse => {
	const antallMaaneder =
		year === referanseDato.getFullYear() ? referanseDato.getMonth() + 1 : MONTH_NAMES.length
	const perioder = Array.from({ length: antallMaaneder }, (_, index) => ({
		year,
		month: index + 1,
	}))
	return {
		kategorier: perioder.map(maanedVisningKort),
		overskrifter: perioder.map(periodeVisning),
		indeksFor: (dato) => {
			const periode = parsePeriode(dato)
			return periode && periode.year === year && periode.month <= antallMaaneder
				? periode.month - 1
				: null
		},
	}
}

export const toPersonMatrise = (
	detaljert: BrukerBestillingerDTO[],
	akse: Tidsakse,
): DagPersonMatrise => {
	const antall = akse.kategorier.length
	const matrise: DagPersonMatrise = {
		kategorier: akse.kategorier,
		overskrifter: akse.overskrifter,
		dollyPersoner: new Array(antall).fill(0),
		testnorgePersoner: new Array(antall).fill(0),
		nyeBestillinger: new Array(antall).fill(0),
	}

	for (const rad of detaljert) {
		const index = akse.indeksFor(rad.dato)
		if (index === null) {
			continue
		}
		matrise.dollyPersoner[index] += asNumber(rad.andelOpprettedeDollyPersoner)
		matrise.testnorgePersoner[index] += asNumber(rad.andelImporterteTestnorgePersoner)
		matrise.nyeBestillinger[index] += asNumber(rad.antallNyeBestillinger)
	}

	return matrise
}

export const toDagPersonMatrise = (detaljert: BrukerBestillingerDTO[], periode: Periode) =>
	toPersonMatrise(detaljert, dagAkse(periode))

export const harDagPersonData = (matrise: DagPersonMatrise) =>
	matrise.dollyPersoner.some((antall) => antall > 0) ||
	matrise.testnorgePersoner.some((antall) => antall > 0)

export const toFagsystemMatrise = (
	detaljert: BrukerBestillingerDTO[],
	akse: Tidsakse,
	maksAntallSerier = 6,
): DagFagsystemMatrise => {
	const antall = akse.kategorier.length
	const summer = toFagsystemSummer(detaljert)
	const trengerAndre = summer.length > maksAntallSerier
	const egneSerier = trengerAndre ? summer.slice(0, maksAntallSerier - 1) : summer
	const egneFagsystemer = new Set(egneSerier.map((sum) => sum.fagsystem))

	const serier: DagFagsystemSerie[] = egneSerier.map((sum) => ({
		fagsystem: sum.fagsystem,
		label: sum.label,
		data: new Array(antall).fill(0),
	}))
	const andreSerie: DagFagsystemSerie = {
		fagsystem: ANDRE_FAGSYSTEMER,
		label: ANDRE_FAGSYSTEMER,
		data: new Array(antall).fill(0),
	}

	detaljert.forEach((rad) => {
		const index = akse.indeksFor(rad.dato)
		if (index === null) {
			return
		}
		synligeKriterier(rad.kriterier).forEach((kriterium) => {
			const serie = egneFagsystemer.has(kriterium.fagsystem)
				? serier.find((egenSerie) => egenSerie.fagsystem === kriterium.fagsystem)
				: andreSerie
			if (serie) {
				serie.data[index] += asNumber(kriterium.antall)
			}
		})
	})

	return {
		kategorier: akse.kategorier,
		overskrifter: akse.overskrifter,
		serier: trengerAndre ? [...serier, andreSerie] : serier,
	}
}

export const toDagFagsystemMatrise = (
	detaljert: BrukerBestillingerDTO[],
	periode: Periode,
	maksAntallSerier = 6,
) => toFagsystemMatrise(detaljert, dagAkse(periode), maksAntallSerier)

export const parseDetaljNoekkel = (noekkel: string): { label: string; type: DetaljType } => {
	if (ANTALL_NOEKLER.has(noekkel)) {
		return { label: detaljNoekkelLabel(noekkel), type: 'antall' }
	}
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

export const toFagsystemerMedDetaljer = (detaljert: BrukerBestillingerDTO[]): FagsystemSum[] => {
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
	detaljert: BrukerBestillingerDTO[],
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
