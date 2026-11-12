import { ReactNode } from 'react'
import { UseFormReturn } from 'react-hook-form'
import { Tag } from '@navikt/ds-react'
import {
	addDays,
	addYears,
	differenceInDays,
	isAfter,
	isBefore,
	isValid,
	startOfDay,
	subDays,
	subYears,
} from 'date-fns'
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
	genereresVedUtflytting?: boolean
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
	postboksadresse?: object | null
	postadresseIFrittFormat?: object | null
	utenlandskAdresseIFrittFormat?: object | null
}

export type HistorikkKontekst = {
	identtype?: string
	tvungenMaster?: string
	foedselsdato?: string | Date | null
	pdlEksisterende?: Array<PdlOpplysning>
	utflytting?: {
		utflyttingsdato?: string | Date | null
		tilflyttingsland?: string | null
	}
	innflytting?: { innflyttingsdato?: string | Date | null }
	bostedsadresse?: {
		nye: Array<HistorikkOpplysning>
		eksisterende: Array<HistorikkOpplysning>
	}
}

export type HistorikkStatus = 'GJELDENDE' | 'ERSTATTET' | 'UTLOEPT' | 'FJERNET'

interface HistorikkTagProps {
	status: HistorikkStatus
	master?: string
}

type SimulertOpplysning = {
	opplysning: HistorikkOpplysning
	nyIdx?: number
	erNy: boolean
	master: string
	fraOgMed: Date | null
	tilOgMed: Date | null
}

type SimuleringResultat = {
	opplysninger: Array<SimulertOpplysning>
	fjernet: Array<SimulertOpplysning>
}

const LANDKODE_UKJENT = 'XUK'
const ANTATT_ALDER_UTEN_FOEDSELSDATO = 30

export const historikkRegler: Record<string, HistorikkRegel> = {
	bostedsadresse: {
		opplysning: 'bostedsadresse',
		gjeldendePerMaster: false,
		harGyldighetsperiode: true,
		arverDatoFraEksisterende: true,
		paavirkesAvFlytting: true,
		tilleggstekst:
			'Utflytting avslutter den nyeste norske bostedsadressen dagen før utflyttingsdatoen, og norske bostedsadresser med f.o.m. etter utflyttingsdatoen blir fjernet. Innflytting gir en ny norsk bostedsadresse hvis personen ikke har en gyldig norsk bostedsadresse på innflyttingsdatoen.',
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
		genereresVedUtflytting: true,
		tilleggstekst:
			'Utflytting til et kjent land gir en ny utenlandsk kontaktadresse hvis personen ikke har en gyldig utenlandsk bostedsadresse eller kontaktadresse på utflyttingsdatoen.',
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

const erGyldigEtter = (simulert: SimulertOpplysning, dato: Date) =>
	!simulert.tilOgMed || isAfter(simulert.tilOgMed, dato)

const harAdressetype = (opplysning: HistorikkOpplysning) =>
	!!(
		opplysning?.vegadresse ||
		opplysning?.matrikkeladresse ||
		opplysning?.ukjentBosted ||
		opplysning?.utenlandskAdresse ||
		opplysning?.postboksadresse ||
		opplysning?.postadresseIFrittFormat ||
		opplysning?.utenlandskAdresseIFrittFormat
	)

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

const erUtenlandskBostedsadresse = (opplysning: HistorikkOpplysning, kontekst: HistorikkKontekst) =>
	!!opplysning?.utenlandskAdresse ||
	(!harAdressetype(opplysning) && !erNorskAdresse(opplysning, kontekst))

const getFoedselsdato = (kontekst: HistorikkKontekst) =>
	tilDato(kontekst.foedselsdato) ?? subYears(new Date(), ANTATT_ALDER_UTEN_FOEDSELSDATO)

const tilSimulert = (
	opplysning: HistorikkOpplysning,
	kontekst: HistorikkKontekst,
	erNy: boolean,
	nyIdx?: number,
): SimulertOpplysning => ({
	opplysning,
	nyIdx,
	erNy,
	master: getMaster(opplysning, kontekst),
	fraOgMed: tilDato(opplysning?.gyldigFraOgMed),
	tilOgMed: tilDato(opplysning?.gyldigTilOgMed),
})

const tilSimulertListe = (
	nye: Array<HistorikkOpplysning>,
	eksisterende: Array<HistorikkOpplysning>,
	kontekst: HistorikkKontekst,
) => [
	...nye.map((opplysning, idx) => tilSimulert(opplysning, kontekst, true, idx)).reverse(),
	...eksisterende.map((opplysning) => tilSimulert(opplysning, kontekst, false)),
]

const settPdlMasterGyldighet = (simulert: SimulertOpplysning) => {
	if (simulert.opplysning?.master !== 'PDL') {
		return
	}
	simulert.fraOgMed = simulert.fraOgMed ?? new Date()
	simulert.tilOgMed = simulert.tilOgMed ?? addYears(simulert.fraOgMed, 1)
}

const finnNesteMedFraOgMed = (adresser: Array<SimulertOpplysning>, index: number) => {
	for (let i = index - 1; i >= 0; i--) {
		if (adresser[i].fraOgMed) {
			return i
		}
	}
	return -1
}

const interpolerFraOgMed = (adresser: Array<SimulertOpplysning>) => {
	let startIndex = adresser.length - 1
	while (startIndex !== -1) {
		const nesteIndex = finnNesteMedFraOgMed(adresser, startIndex)
		const startDato = adresser[startIndex].fraOgMed as Date
		const sluttDato = nesteIndex === -1 ? new Date() : (adresser[nesteIndex].fraOgMed as Date)
		const intervall = differenceInDays(sluttDato, startDato)
		const antall = startIndex - nesteIndex
		const dagerMellom = Math.trunc((intervall - Math.trunc(intervall / antall)) / antall)
		for (let i = startIndex - 1; i > nesteIndex; i--) {
			adresser[i].fraOgMed = addDays(adresser[i + 1].fraOgMed as Date, dagerMellom)
		}
		startIndex = nesteIndex
	}
}

const oppdaterAdressedatoer = (
	adresser: Array<SimulertOpplysning>,
	kontekst: HistorikkKontekst,
) => {
	if (adresser.length === 0) {
		return
	}
	const eldste = adresser[adresser.length - 1]
	eldste.fraOgMed = eldste.fraOgMed ?? getFoedselsdato(kontekst)
	for (let i = adresser.length - 1; i > 0; i--) {
		const tilOgMed = adresser[i].tilOgMed
		if (!adresser[i - 1].fraOgMed && tilOgMed) {
			adresser[i - 1].fraOgMed = addDays(tilOgMed, 1)
		}
	}
	interpolerFraOgMed(adresser)
}

const fjernUgyldigTilOgMed = (adresser: Array<SimulertOpplysning>) =>
	adresser.forEach((adresse) => {
		if (!adresse.fraOgMed || !adresse.tilOgMed || !isBefore(adresse.fraOgMed, adresse.tilOgMed)) {
			adresse.tilOgMed = null
		}
	})

const simulerBostedsadresser = (
	nye: Array<HistorikkOpplysning>,
	eksisterende: Array<HistorikkOpplysning>,
	kontekst: HistorikkKontekst,
): SimuleringResultat => {
	let adresser = tilSimulertListe(nye, eksisterende, kontekst)
	oppdaterAdressedatoer(adresser, kontekst)
	const erNorsk = (adresse: SimulertOpplysning) => erNorskAdresse(adresse.opplysning, kontekst)

	if (kontekst.innflytting) {
		const innflyttingsdato = tilDato(kontekst.innflytting.innflyttingsdato) ?? new Date()
		if (!adresser.some((adresse) => erNorsk(adresse) && erGyldigEtter(adresse, innflyttingsdato))) {
			adresser = [
				tilSimulert({ vegadresse: {}, gyldigFraOgMed: innflyttingsdato }, kontekst, true),
				...adresser,
			]
		}
	}

	let fjernet: Array<SimulertOpplysning> = []
	if (kontekst.utflytting) {
		const utflyttingsdato = tilDato(kontekst.utflytting.utflyttingsdato) ?? new Date()
		fjernet = adresser.filter(
			(adresse) =>
				erNorsk(adresse) && !!adresse.fraOgMed && isAfter(adresse.fraOgMed, utflyttingsdato),
		)
		adresser = adresser.filter((adresse) => !fjernet.includes(adresse))
		if (adresser.length > 0 && erNorsk(adresser[0])) {
			adresser[0].tilOgMed = subDays(utflyttingsdato, 1)
		}
	}
	return { opplysninger: adresser, fjernet }
}

const skalGenerereKontaktadresseVedUtflytting = (
	kontaktadresser: Array<SimulertOpplysning>,
	utflyttingsdato: Date,
	kontekst: HistorikkKontekst,
) => {
	if (kontekst.utflytting?.tilflyttingsland === LANDKODE_UKJENT) {
		return false
	}
	const bostedsadresser = simulerBostedsadresser(
		kontekst.bostedsadresse?.nye ?? [],
		kontekst.bostedsadresse?.eksisterende ?? [],
		kontekst,
	).opplysninger
	const harGyldigUtenlandskBostedsadresse = bostedsadresser.some(
		(adresse) =>
			erUtenlandskBostedsadresse(adresse.opplysning, kontekst) &&
			erGyldigEtter(adresse, utflyttingsdato),
	)
	const harGyldigUtenlandskKontaktadresse = kontaktadresser.some(
		(adresse) => !!adresse.opplysning?.utenlandskAdresse && erGyldigEtter(adresse, utflyttingsdato),
	)
	return !harGyldigUtenlandskBostedsadresse && !harGyldigUtenlandskKontaktadresse
}

const simulerKontaktadresser = (
	nye: Array<HistorikkOpplysning>,
	eksisterende: Array<HistorikkOpplysning>,
	kontekst: HistorikkKontekst,
): SimuleringResultat => {
	let adresser = tilSimulertListe(nye, eksisterende, kontekst)

	if (kontekst.utflytting) {
		const utflyttingsdato = tilDato(kontekst.utflytting.utflyttingsdato) ?? new Date()
		if (skalGenerereKontaktadresseVedUtflytting(adresser, utflyttingsdato, kontekst)) {
			adresser = [
				tilSimulert({ utenlandskAdresse: {}, gyldigFraOgMed: utflyttingsdato }, kontekst, true),
				...adresser,
			]
		}
	}

	adresser.filter((adresse) => adresse.erNy).forEach(settPdlMasterGyldighet)
	oppdaterAdressedatoer(adresser, kontekst)
	return { opplysninger: adresser, fjernet: [] }
}

const simulerOppholdsadresser = (
	nye: Array<HistorikkOpplysning>,
	eksisterende: Array<HistorikkOpplysning>,
	kontekst: HistorikkKontekst,
): SimuleringResultat => {
	const nyeAdresser = tilSimulertListe(nye, [], kontekst)
	oppdaterAdressedatoer(nyeAdresser, kontekst)
	return {
		opplysninger: [...nyeAdresser, ...tilSimulertListe([], eksisterende, kontekst)],
		fjernet: [],
	}
}

const simuler = (
	nye: Array<HistorikkOpplysning>,
	regel: HistorikkRegel,
	eksisterende: Array<HistorikkOpplysning>,
	kontekst: HistorikkKontekst,
): SimuleringResultat => {
	if (!regel.harGyldighetsperiode) {
		return { opplysninger: tilSimulertListe(nye, eksisterende, kontekst), fjernet: [] }
	}
	const resultat = regel.paavirkesAvFlytting
		? simulerBostedsadresser(nye, eksisterende, kontekst)
		: regel.genereresVedUtflytting
			? simulerKontaktadresser(nye, eksisterende, kontekst)
			: simulerOppholdsadresser(nye, eksisterende, kontekst)
	fjernUgyldigTilOgMed(resultat.opplysninger)
	return resultat
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
	const { opplysninger, fjernet } = simuler(nye, regel, eksisterende, kontekst)
	const status: Array<HistorikkStatus> = nye.map(() => 'GJELDENDE')

	fjernet
		.filter((simulert) => simulert.nyIdx !== undefined)
		.forEach((simulert) => {
			status[simulert.nyIdx as number] = 'FJERNET'
		})

	opplysninger.forEach((simulert, posisjon) => {
		if (simulert.nyIdx === undefined) {
			return
		}
		const harNyere = opplysninger
			.slice(0, posisjon)
			.some((nyere) =>
				harSammeGjeldendeGruppe(simulert.opplysning, nyere.opplysning, regel, kontekst),
			)
		if (harNyere) {
			status[simulert.nyIdx] = 'ERSTATTET'
		} else if (erPassert(simulert.tilOgMed)) {
			status[simulert.nyIdx] = 'UTLOEPT'
		}
	})
	return status
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
	if (status === 'FJERNET') {
		return 'Blir fjernet (utflytting)'
	}
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

type FoedselOpplysning = {
	foedselsdato?: string | null
	foedselsaar?: number | null
}

const finnFoedselsdato = (personer: Array<Record<string, any> | undefined>) => {
	for (const person of personer) {
		const foedsel: FoedselOpplysning | undefined = person?.foedselsdato?.[0] ?? person?.foedsel?.[0]
		if (foedsel?.foedselsdato) {
			return foedsel.foedselsdato
		}
		if (foedsel?.foedselsaar) {
			return new Date(foedsel.foedselsaar, 0, 1)
		}
	}
	return null
}

export const useHistorikkTag = (formMethods: UseFormReturn, regel: HistorikkRegel) => {
	const opts = useBestillingsveileder() as BestillingsveilederContextType
	const identtype = opts?.identtype || formMethods.watch('pdldata.opprettNyPerson.identtype')
	const [utflytting, innflytting, foedselsdato, foedsel, bostedsadresse] = formMethods.watch([
		'pdldata.person.utflytting',
		'pdldata.person.innflytting',
		'pdldata.person.foedselsdato',
		'pdldata.person.foedsel',
		'pdldata.person.bostedsadresse',
	])
	const eksisterendePerson = opts?.personFoerLeggTil?.pdlforvalter?.person
	const paavirkesAvFlytting = regel.paavirkesAvFlytting || regel.genereresVedUtflytting

	const kontekst: HistorikkKontekst = {
		identtype,
		tvungenMaster: identtype === 'NPID' || opts?.identMaster === 'PDL' ? 'PDL' : undefined,
		foedselsdato: finnFoedselsdato([{ foedselsdato, foedsel }, eksisterendePerson]),
		pdlEksisterende: opts?.personFoerLeggTil?.pdl?.hentPerson?.[regel.opplysning],
		utflytting: paavirkesAvFlytting ? utflytting?.[0] : undefined,
		innflytting: paavirkesAvFlytting ? innflytting?.[0] : undefined,
		bostedsadresse: regel.genereresVedUtflytting
			? {
					nye: bostedsadresse ?? [],
					eksisterende: eksisterendePerson?.bostedsadresse ?? [],
				}
			: undefined,
	}
	const eksisterende = eksisterendePerson?.[regel.opplysning] || []

	return getHistorikkTag(regel, eksisterende, kontekst)
}
