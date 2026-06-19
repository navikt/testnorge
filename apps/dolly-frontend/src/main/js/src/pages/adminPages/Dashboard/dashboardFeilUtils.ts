import { format, parseISO } from 'date-fns'
import { nb } from 'date-fns/locale'
import {
	type DashboardFeilDetaljertRad,
	type DashboardFeilSummertRad,
	type DashboardOversiktDTO,
	type FeilVerdi,
} from '@/utils/hooks/useDashboard'

export const MONTH_NAMES = [
	'JANUARY',
	'FEBRUARY',
	'MARCH',
	'APRIL',
	'MAY',
	'JUNE',
	'JULY',
	'AUGUST',
	'SEPTEMBER',
	'OCTOBER',
	'NOVEMBER',
	'DECEMBER',
] as const

export type MonthName = (typeof MONTH_NAMES)[number]

export const monthNumberToName = (month: number): MonthName | null =>
	month >= 1 && month <= 12 ? MONTH_NAMES[month - 1] : null

export const monthNameToNumber = (name: string): number | null => {
	const index = MONTH_NAMES.indexOf(name as MonthName)
	return index === -1 ? null : index + 1
}

const IDENTITETSFELT = new Set(['sistOppdatert', 'bestillingId', 'ident', 'master'])

const FAGSYSTEM_FEIL_LABELS: Record<string, string> = {
	andreFeil: 'Andre feil',
	aaregFeil: 'Aareg',
	arbeidsplassencvFeil: 'Arbeidsplassen CV',
	arbeidssoekerregisteretFeil: 'Arbeidssøkerregisteret',
	arenaforvalterFeil: 'Arena',
	brregstubFeil: 'BRREG-stub',
	dokarkivFeil: 'Dokarkiv',
	etterlatteFeil: 'Etterlatte',
	fullmaktFeil: 'Fullmakt',
	histarkFeil: 'Histark',
	inntektsmeldingFeil: 'Inntektsmelding',
	inntektstubFeil: 'Inntektstub',
	instdataFeil: 'Institusjonsdata',
	kelvinAapFeil: 'Kelvin AAP',
	kontoregisterFeil: 'Kontoregister',
	krrstubFeil: 'KRR-stub',
	medlFeil: 'MEDL',
	nomFeil: 'NOM',
	pdlForvalterFeil: 'PDL Forvalter',
	pdlImportFeil: 'PDL Import',
	pdlOrdreFeil: 'PDL Ordre',
	pdlPersonFeil: 'PDL Person',
	pensjonforvalterFeil: 'Pensjon',
	sigrunstubFeil: 'Sigrun-stub',
	skattekortFeil: 'Skattekort',
	skjermingsregisterFeil: 'Skjermingsregister',
	sykemeldingFeil: 'Sykmelding',
	udistubFeil: 'UDI-stub',
	yrkesskadeFeil: 'Yrkesskade',
}

export const statusFeltTilFeilNokkel = (key: string): string => {
	if (key === 'feil') {
		return 'andreFeil'
	}
	return key.endsWith('Status') ? key.replace(/Status$/, 'Feil') : key
}

const humanizeFeilNokkel = (feilKey: string): string => {
	const utenSuffix = feilKey.replace(/Feil$/, '')
	const medMellomrom = utenSuffix.replace(/([a-z])([A-Z])/g, '$1 $2')
	return medMellomrom.charAt(0).toUpperCase() + medMellomrom.slice(1)
}

export const fagsystemFeilLabel = (feilKey: string): string =>
	FAGSYSTEM_FEIL_LABELS[feilKey] ?? humanizeFeilNokkel(feilKey)

export type FeilPeriodeOption = {
	interval: string
	intervalVisning: string
	aar: number
	maaned: number
	maanedNavn: MonthName
}

const intervalVisning = (interval: string): string => {
	try {
		return format(parseISO(`${interval}-01`), 'MMM yyyy', { locale: nb })
	} catch {
		return interval
	}
}

export const toFeilPeriodeOptions = (oversikt: DashboardOversiktDTO[]): FeilPeriodeOption[] =>
	oversikt
		.map((rad) => {
			// maaned comes in as "01", "02", etc. from backend
			const maanedNum = parseInt(rad.maaned, 10)
			const maanedNavn = monthNumberToName(maanedNum)
			if (maanedNavn === null) {
				return null
			}
			const interval = `${rad.aar}-${String(maanedNum).padStart(2, '0')}`
			return {
				interval,
				intervalVisning: intervalVisning(interval),
				aar: rad.aar,
				maaned: maanedNum,
				maanedNavn,
			}
		})
		.filter((option): option is FeilPeriodeOption => option !== null)
		.sort((a, b) => a.interval.localeCompare(b.interval))

export type FeilDagPunkt = {
	dag: number
	dato: string
	datoVisning: string
	total: number
	perFagsystem: Record<string, number>
}

export type FeilSummertView = {
	punkter: FeilDagPunkt[]
	fagsystemNokler: string[]
}

const asAntall = (value: number | string | undefined): number => {
	if (typeof value === 'number') {
		return value
	}
	const parsed = Number(value)
	return Number.isFinite(parsed) ? parsed : 0
}

export const toFeilSummertView = (summert: DashboardFeilSummertRad[]): FeilSummertView => {
	const totalPerNokkel = new Map<string, number>()

	const punkter = summert
		.map((rad) => {
			const dato = String(rad.bestillingDato)
			const dag = Number(dato.slice(8, 10))
			const perFagsystem: Record<string, number> = {}
			let total = 0

			Object.entries(rad).forEach(([nokkel, verdi]) => {
				if (nokkel === 'bestillingDato') {
					return
				}
				const antall = asAntall(verdi)
				if (antall <= 0) {
					return
				}
				perFagsystem[nokkel] = antall
				total += antall
				totalPerNokkel.set(nokkel, (totalPerNokkel.get(nokkel) ?? 0) + antall)
			})

			return {
				dag,
				dato,
				datoVisning: intervalDagVisning(dato),
				total,
				perFagsystem,
			}
		})
		.filter((punkt) => Number.isFinite(punkt.dag))
		.sort((a, b) => a.dag - b.dag)

	const fagsystemNokler = [...totalPerNokkel.entries()]
		.sort((a, b) => {
			const aKey = a[0] ?? ''
			const bKey = b[0] ?? ''
			return b[1] - a[1] || aKey.localeCompare(bKey)
		})
		.map(([nokkel]) => nokkel)

	return { punkter, fagsystemNokler }
}

const intervalDagVisning = (dato: string): string => {
	try {
		return format(parseISO(dato), 'dd.MM.yyyy')
	} catch {
		return dato
	}
}

export type FeilDetaljRad = {
	ident: string
	bestillingId: number
	sistOppdatert: string
	master: string
	verdi: FeilVerdi
}

export type FeilGruppe = {
	feilNokkel: string
	label: string
	rader: FeilDetaljRad[]
}

export const toFeilGrupper = (detaljert: DashboardFeilDetaljertRad[]): FeilGruppe[] => {
	const grupper = new Map<string, FeilDetaljRad[]>()

	detaljert.forEach((rad) => {
		Object.entries(rad).forEach(([nokkel, verdi]) => {
			if (IDENTITETSFELT.has(nokkel)) {
				return
			}
			const feilNokkel = statusFeltTilFeilNokkel(nokkel)
			const eksisterende = grupper.get(feilNokkel) ?? []
			eksisterende.push({
				ident: rad.ident,
				bestillingId: rad.bestillingId,
				sistOppdatert: rad.sistOppdatert,
				master: rad.master,
				verdi,
			})
			grupper.set(feilNokkel, eksisterende)
		})
	})

	return [...grupper.entries()]
		.map(([feilNokkel, rader]) => ({
			feilNokkel,
			label: fagsystemFeilLabel(feilNokkel),
			rader: rader.sort((a, b) => {
				const aIdent = a.ident ?? ''
				const bIdent = b.ident ?? ''
				return aIdent.localeCompare(bIdent)
			}),
		}))
		.sort((a, b) => {
			const aLabel = a.label ?? ''
			const bLabel = b.label ?? ''
			return b.rader.length - a.rader.length || aLabel.localeCompare(bLabel)
		})
}

export const feilVerdiTilTekst = (verdi: FeilVerdi): string => {
	if (verdi === null || verdi === undefined) {
		return ''
	}
	if (typeof verdi === 'string') {
		return verdi
	}
	if (typeof verdi === 'number' || typeof verdi === 'boolean') {
		return String(verdi)
	}
	try {
		return JSON.stringify(verdi, null, 2)
	} catch {
		return String(verdi)
	}
}

export const erStrukturertFeilVerdi = (verdi: FeilVerdi): boolean =>
	verdi !== null && typeof verdi === 'object'
