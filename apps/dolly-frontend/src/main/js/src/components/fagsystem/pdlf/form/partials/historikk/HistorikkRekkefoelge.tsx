import { ReactNode } from 'react'
import { UseFormReturn } from 'react-hook-form'
import { Tag } from '@navikt/ds-react'
import { addDays, addYears, isAfter, isBefore, isValid, startOfDay, subDays } from 'date-fns'
import {
	BestillingsveilederContextType,
	useBestillingsveileder,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import {
	erHistoriskOpplysning,
	PdlOpplysning,
} from '@/components/ui/historikk/PdlHistorikkFieldArray'

export type HistorikkRegel = {
	opplysning: string
	gjeldendePerMaster: boolean
	harGyldighetsperiode: boolean
	pdlMasterVarerEttAar?: boolean
	arverDatoFraEksisterende?: boolean
	paavirkesAvFlytting?: boolean
	tilleggstekst?: string
}

export type HistorikkOpplysning = PdlOpplysning & {
	master?: string | null
	gyldigFraOgMed?: string | Date | null
	gyldigTilOgMed?: string | Date | null
	vegadresse?: object | null
	matrikkeladresse?: object | null
	ukjentBosted?: object | null
	utenlandskAdresse?: object | null
}

export type HistorikkKontekst = {
	identtype?: string
	tvungenMaster?: string
	pdlEksisterende?: Array<PdlOpplysning>
	utflytting?: { utflyttingsdato?: string | Date | null }
	innflytting?: { innflyttingsdato?: string | Date | null }
}

export type HistorikkStatus = 'GJELDENDE' | 'ERSTATTET' | 'UTLOEPT'

interface HistorikkTagProps {
	status: HistorikkStatus
	master?: string
}

export const historikkRegler: Record<string, HistorikkRegel> = {
	bostedsadresse: {
		opplysning: 'bostedsadresse',
		gjeldendePerMaster: false,
		harGyldighetsperiode: true,
		arverDatoFraEksisterende: true,
		paavirkesAvFlytting: true,
		tilleggstekst:
			'Utflytting avslutter den nyeste norske bostedsadressen dagen før utflyttingsdatoen. Innflytting gir en ny norsk bostedsadresse hvis personen ikke har en gyldig norsk bostedsadresse på innflyttingsdatoen.',
	},
	oppholdsadresse: {
		opplysning: 'oppholdsadresse',
		gjeldendePerMaster: true,
		harGyldighetsperiode: true,
	},
	kontaktadresse: {
		opplysning: 'kontaktadresse',
		gjeldendePerMaster: true,
		harGyldighetsperiode: true,
		pdlMasterVarerEttAar: true,
		arverDatoFraEksisterende: true,
	},
	adressebeskyttelse: {
		opplysning: 'adressebeskyttelse',
		gjeldendePerMaster: true,
		harGyldighetsperiode: false,
		tilleggstekst:
			'Strengt fortrolig utland har alltid master PDL, de andre graderingene har master FREG.',
	},
}

const getMaster = (opplysning: HistorikkOpplysning, kontekst: HistorikkKontekst) =>
	kontekst.tvungenMaster || opplysning?.master || 'FREG'

const tilDato = (dato?: string | Date | null) => {
	if (!dato) {
		return null
	}
	const parsetDato = new Date(dato)
	return isValid(parsetDato) ? parsetDato : null
}

const erPassert = (dato: Date | null) => !!dato && isBefore(dato, startOfDay(new Date()))

const erPdlMasterSomVarerEttAar = (opplysning: HistorikkOpplysning, regel: HistorikkRegel) =>
	!!regel.pdlMasterVarerEttAar && opplysning?.master === 'PDL'

const erNorskAdresse = (opplysning: HistorikkOpplysning, kontekst: HistorikkKontekst) => {
	if (opplysning?.vegadresse || opplysning?.matrikkeladresse || opplysning?.ukjentBosted) {
		return true
	}
	if (opplysning?.utenlandskAdresse) {
		return false
	}
	return (
		!kontekst.utflytting &&
		!kontekst.tvungenMaster &&
		(!kontekst.identtype || kontekst.identtype === 'FNR')
	)
}

const getGenerertTilOgMed = (opplysning: HistorikkOpplysning, regel: HistorikkRegel) => {
	const tilOgMed = tilDato(opplysning?.gyldigTilOgMed)
	if (tilOgMed || !erPdlMasterSomVarerEttAar(opplysning, regel)) {
		return tilOgMed
	}
	return addYears(tilDato(opplysning?.gyldigFraOgMed) ?? new Date(), 1)
}

const getEffektivFraOgMed = (
	opplysning: HistorikkOpplysning,
	eldreTilOgMed: Date | null,
	regel: HistorikkRegel,
) => {
	const fraOgMed = tilDato(opplysning?.gyldigFraOgMed)
	if (fraOgMed) {
		return fraOgMed
	}
	if (erPdlMasterSomVarerEttAar(opplysning, regel)) {
		return new Date()
	}
	return eldreTilOgMed ? addDays(eldreTilOgMed, 1) : null
}

const getGyldigTilOgMed = (fraOgMed: Date | null, tilOgMed: Date | null) =>
	tilOgMed && fraOgMed && !isBefore(fraOgMed, tilOgMed) ? null : tilOgMed

const getEffektivTilOgMed = (
	opplysning: HistorikkOpplysning,
	eldreTilOgMed: Date | null,
	regel: HistorikkRegel,
) => {
	if (!regel.harGyldighetsperiode) {
		return null
	}
	return getGyldigTilOgMed(
		getEffektivFraOgMed(opplysning, eldreTilOgMed, regel),
		getGenerertTilOgMed(opplysning, regel),
	)
}

const getUtflyttingTilOgMed = (
	opplysning: HistorikkOpplysning,
	eldreTilOgMed: Date | null,
	regel: HistorikkRegel,
	kontekst: HistorikkKontekst,
) => {
	if (!kontekst.utflytting || !erNorskAdresse(opplysning, kontekst)) {
		return undefined
	}
	const utflyttingsdato = tilDato(kontekst.utflytting.utflyttingsdato) ?? new Date()
	return getGyldigTilOgMed(
		getEffektivFraOgMed(opplysning, eldreTilOgMed, regel),
		subDays(utflyttingsdato, 1),
	)
}

const leggerInnflyttingTilAdresse = (
	alleAdresser: Array<HistorikkOpplysning>,
	kontekst: HistorikkKontekst,
) => {
	if (!kontekst.innflytting) {
		return false
	}
	const innflyttingsdato = tilDato(kontekst.innflytting.innflyttingsdato) ?? new Date()
	return !alleAdresser.some((adresse) => {
		const tilOgMed = tilDato(adresse?.gyldigTilOgMed)
		return erNorskAdresse(adresse, kontekst) && (!tilOgMed || isAfter(tilOgMed, innflyttingsdato))
	})
}

const getEldreTilOgMed = (
	nye: Array<HistorikkOpplysning>,
	idx: number,
	eksisterende: Array<HistorikkOpplysning>,
	regel: HistorikkRegel,
) => {
	if (idx > 0) {
		return getGenerertTilOgMed(nye[idx - 1], regel)
	}
	return regel.arverDatoFraEksisterende ? tilDato(eksisterende[0]?.gyldigTilOgMed) : null
}

const harSammeGjeldendeGruppe = (
	opplysning: HistorikkOpplysning,
	annen: HistorikkOpplysning,
	regel: HistorikkRegel,
	kontekst: HistorikkKontekst,
) => !regel.gjeldendePerMaster || getMaster(opplysning, kontekst) === getMaster(annen, kontekst)

export const beregnHistorikkStatus = (
	nye: Array<HistorikkOpplysning>,
	regel: HistorikkRegel,
	eksisterende: Array<HistorikkOpplysning> = [],
	kontekst: HistorikkKontekst = {},
): Array<HistorikkStatus> => {
	const erstattesAvInnflytting =
		!!regel.paavirkesAvFlytting && leggerInnflyttingTilAdresse([...nye, ...eksisterende], kontekst)

	return nye.map((opplysning, idx) => {
		const harNyere = nye
			.slice(idx + 1)
			.some((nyere) => harSammeGjeldendeGruppe(opplysning, nyere, regel, kontekst))
		if (harNyere || erstattesAvInnflytting) {
			return 'ERSTATTET'
		}
		const eldreTilOgMed = getEldreTilOgMed(nye, idx, eksisterende, regel)
		const erNyeste = idx === nye.length - 1
		const utflyttingTilOgMed =
			erNyeste && regel.paavirkesAvFlytting
				? getUtflyttingTilOgMed(opplysning, eldreTilOgMed, regel, kontekst)
				: undefined
		const tilOgMed =
			utflyttingTilOgMed !== undefined
				? utflyttingTilOgMed
				: getEffektivTilOgMed(opplysning, eldreTilOgMed, regel)
		return erPassert(tilOgMed) ? 'UTLOEPT' : 'GJELDENDE'
	})
}

const erGjeldendeFraFoer = (
	opplysning: HistorikkOpplysning,
	regel: HistorikkRegel,
	kontekst: HistorikkKontekst,
) =>
	!erHistoriskOpplysning(opplysning, kontekst.pdlEksisterende) &&
	!(regel.harGyldighetsperiode && erPassert(tilDato(opplysning?.gyldigTilOgMed)))

export const getEksisterendeSomErstattes = (
	nye: Array<HistorikkOpplysning>,
	eksisterende: Array<HistorikkOpplysning>,
	regel: HistorikkRegel,
	kontekst: HistorikkKontekst = {},
) =>
	eksisterende.filter(
		(gammel) =>
			erGjeldendeFraFoer(gammel, regel, kontekst) &&
			nye.some((ny) => harSammeGjeldendeGruppe(gammel, ny, regel, kontekst)),
	)

const skalViseHistorikk = (
	nye: Array<HistorikkOpplysning>,
	eksisterende: Array<HistorikkOpplysning>,
	regel: HistorikkRegel,
	kontekst: HistorikkKontekst,
) =>
	beregnHistorikkStatus(nye, regel, eksisterende, kontekst).some(
		(status) => status !== 'GJELDENDE',
	) || getEksisterendeSomErstattes(nye, eksisterende, regel, kontekst).length > 0

export const getHistorikkRegeltekst = (regel: HistorikkRegel) => {
	const { opplysning } = regel
	const tekster = regel.gjeldendePerMaster
		? [
				`Personen kan ha én gjeldende ${opplysning} per master (FREG og PDL).`,
				'Den nederste for hver master blir gjeldende, og de andre med samme master blir historiske.',
				'Personer med NPID og personer fra Test-Norge får alltid master PDL.',
			]
		: [
				`Personen kan ha én gjeldende ${opplysning}, uansett master.`,
				'Den nederste blir gjeldende, og de andre blir historiske.',
			]
	if (regel.tilleggstekst) {
		tekster.push(regel.tilleggstekst)
	}
	if (regel.harGyldighetsperiode) {
		tekster.push(
			`En ${opplysning} med gyldig t.o.m. tilbake i tid blir historisk, selv om den er nyest.`,
		)
	}
	if (regel.pdlMasterVarerEttAar) {
		tekster.push(
			`En ${opplysning} med master PDL uten gyldig t.o.m. får t.o.m. ett år etter f.o.m.`,
		)
	}
	return tekster.join(' ')
}

const getTagTekst = (status: HistorikkStatus, master?: string) => {
	if (status === 'UTLOEPT') {
		return 'Blir historisk (t.o.m. passert)'
	}
	if (status === 'ERSTATTET') {
		return 'Blir historisk'
	}
	return master ? `Blir gjeldende for ${master}` : 'Blir gjeldende'
}

export const HistorikkTag = ({ status, master }: HistorikkTagProps) => (
	<Tag
		variant={status === 'GJELDENDE' ? 'moderate' : 'outline'}
		size="xsmall"
		data-color={status === 'GJELDENDE' ? 'success' : 'neutral'}
		style={{ marginLeft: '10px' }}
	>
		{getTagTekst(status, master)}
	</Tag>
)

export const getHistorikkTag =
	(
		regel: HistorikkRegel,
		eksisterende: Array<HistorikkOpplysning> = [],
		kontekst: HistorikkKontekst = {},
	) =>
	(idx: number, values: Array<HistorikkOpplysning>): ReactNode => {
		if (!skalViseHistorikk(values, eksisterende, regel, kontekst)) {
			return null
		}
		const status = beregnHistorikkStatus(values, regel, eksisterende, kontekst)[idx]
		const master = regel.gjeldendePerMaster ? getMaster(values[idx], kontekst) : undefined
		return <HistorikkTag status={status} master={master} />
	}

export const useHistorikkTag = (formMethods: UseFormReturn, regel: HistorikkRegel) => {
	const opts = useBestillingsveileder() as BestillingsveilederContextType
	const identtype = opts?.identtype || formMethods.watch('pdldata.opprettNyPerson.identtype')
	const utflytting = formMethods.watch('pdldata.person.utflytting')?.[0]
	const innflytting = formMethods.watch('pdldata.person.innflytting')?.[0]

	const kontekst: HistorikkKontekst = {
		identtype,
		tvungenMaster: identtype === 'NPID' || opts?.identMaster === 'PDL' ? 'PDL' : undefined,
		pdlEksisterende: opts?.personFoerLeggTil?.pdl?.hentPerson?.[regel.opplysning],
		utflytting: regel.paavirkesAvFlytting ? utflytting : undefined,
		innflytting: regel.paavirkesAvFlytting ? innflytting : undefined,
	}
	const eksisterende = opts?.personFoerLeggTil?.pdlforvalter?.person?.[regel.opplysning] || []

	return getHistorikkTag(regel, eksisterende, kontekst)
}
