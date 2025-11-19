import { initialValuesBasedOnMal } from '@/components/bestillingsveileder/options/malOptions'

export interface RawBestillingsveilederConfig {
	gruppeId?: number
	antall?: number
	identtype?: string
	id2032?: boolean
	mal?: any
	opprettFraIdenter?: string[]
	personFoerLeggTil?: any
	tidligereBestillinger?: any[]
	importPersoner?: any[]
	identMaster?: string
	opprettOrganisasjon?: string | null
	leggTilPaaGruppe?: any
	gruppe?: any
	timedOutFagsystemer?: string[]
}

export interface BestillingsveilederDerivedState {
	initialValues: any
	gruppeId: number | null
	antall: number
	identtype: string
	id2032: boolean
	mal: any
	opprettFraIdenter?: string[]
	personFoerLeggTil?: any
	leggTilPaaGruppe?: any
	importPersoner?: any[]
	identMaster?: string
	tidligereBestillinger?: any[]
	opprettOrganisasjon?: string | null
	gruppe?: any
	timedOutFagsystemer?: string[]
	is: {
		nyBestilling: boolean
		nyBestillingFraMal: boolean
		opprettFraIdenter: boolean
		leggTil: boolean
		leggTilPaaGruppe: boolean
		nyOrganisasjon: boolean
		nyOrganisasjonFraMal: boolean
		nyStandardOrganisasjon: boolean
		importTestnorge: boolean
	}
}

const TYPE = {
	NY_BESTILLING: 'NY_BESTILLING',
	NY_BESTILLING_FRA_MAL: 'NY_BESTILLING_FRA_MAL',
	OPPRETT_FRA_IDENTER: 'OPPRETT_FRA_IDENTER',
	LEGG_TIL: 'LEGG_TIL',
	LEGG_TIL_PAA_GRUPPE: 'LEGG_TIL_PAA_GRUPPE',
	NY_ORGANISASJON: 'NY_ORGANISASJON',
	NY_ORGANISASJON_FRA_MAL: 'NY_ORGANISASJON_FRA_MAL',
	NY_STANDARD_ORGANISASJON: 'NY_STANDARD_ORGANISASJON',
	IMPORT_TESTNORGE: 'IMPORT_TESTNORGE',
} as const

type Mode = (typeof TYPE)[keyof typeof TYPE]

const detectMode = (config: RawBestillingsveilederConfig): Mode => {
	if (config.opprettOrganisasjon) {
		if (config.opprettOrganisasjon === 'STANDARD') return TYPE.NY_STANDARD_ORGANISASJON
		if (config.mal) return TYPE.NY_ORGANISASJON_FRA_MAL
		return TYPE.NY_ORGANISASJON
	}
	if (config.opprettFraIdenter) return TYPE.OPPRETT_FRA_IDENTER
	if (config.personFoerLeggTil) return TYPE.LEGG_TIL
	if (config.leggTilPaaGruppe) return TYPE.LEGG_TIL_PAA_GRUPPE
	if (config.importPersoner) return TYPE.IMPORT_TESTNORGE
	if (config.mal) return TYPE.NY_BESTILLING_FRA_MAL
	return TYPE.NY_BESTILLING
}

export const deriveBestillingsveilederState = (
	config: RawBestillingsveilederConfig = {},
	environments: any,
): BestillingsveilederDerivedState => {
	const {
		antall = 1,
		identtype = 'FNR',
		id2032 = false,
		mal,
		opprettFraIdenter,
		personFoerLeggTil,
		tidligereBestillinger,
		importPersoner,
		identMaster,
		opprettOrganisasjon = null,
		leggTilPaaGruppe = null,
		gruppe,
		gruppeId = null,
		timedOutFagsystemer,
	} = config

	const mode = detectMode(config)

	const buildBasePerson = () => ({
		antall,
		beskrivelse: null,
		pdldata: {
			opprettNyPerson: {
				identtype,
				syntetisk: true,
				id2032,
			},
		},
		importPersoner: undefined,
	})

	const builders: Record<Mode, () => any> = {
		[TYPE.NY_BESTILLING]: () => buildBasePerson(),
		[TYPE.NY_BESTILLING_FRA_MAL]: () => ({
			...buildBasePerson(),
			...initialValuesBasedOnMal(mal, environments),
			mal: mal.id,
		}),
		[TYPE.OPPRETT_FRA_IDENTER]: () => ({
			antall: 1,
			beskrivelse: null,
			pdldata: undefined,
			importPersoner: undefined,
			opprettFraIdenter,
			...(mal ? { mal: mal.id } : {}),
		}),
		[TYPE.LEGG_TIL]: () => ({
			antall,
			beskrivelse: null,
			pdldata: undefined,
			importPersoner: undefined,
			environments: [],
			...(mal ? { mal: mal.id } : {}),
		}),
		[TYPE.LEGG_TIL_PAA_GRUPPE]: () => ({
			antall,
			beskrivelse: null,
			pdldata: undefined,
			importPersoner: undefined,
			environments: [],
			...(mal ? { mal: mal.id } : {}),
		}),
		[TYPE.IMPORT_TESTNORGE]: () => {
			const base = {
				antall: importPersoner ? importPersoner.length : antall,
				beskrivelse: null,
				importPersoner,
				...(mal ? { mal: mal.id } : {}),
			}
			if (mal?.bestilling?.pdldata?.person) {
				const malVals = initialValuesBasedOnMal(mal, environments)
				const person = malVals?.pdldata?.person
				return { ...base, pdldata: { person } }
			}
			return { ...base, pdldata: undefined }
		},
		[TYPE.NY_ORGANISASJON]: () => ({
			antall,
			organisasjon: { enhetstype: '' },
		}),
		[TYPE.NY_STANDARD_ORGANISASJON]: () => ({
			antall,
			organisasjon: {
				enhetstype: 'AS',
				naeringskode: '01.451',
				forretningsadresse: {
					adresselinjer: ['', '', ''],
					postnr: '',
					kommunenr: '',
					landkode: 'NO',
				},
				underenheter: [
					{
						enhetstype: 'BEDR',
						naeringskode: '01.451',
						forretningsadresse: {
							adresselinjer: ['', '', ''],
							postnr: '',
							kommunenr: '',
							landkode: 'NO',
						},
					},
				],
			},
		}),
		[TYPE.NY_ORGANISASJON_FRA_MAL]: () => {
			const malVals = initialValuesBasedOnMal(mal, environments)
			const { pdldata: _ignorePdldata, ...rest } = malVals || {}
			const result = {
				antall,
				...rest,
				mal: mal.id,
			}
			if (!result.organisasjon) {
				result.organisasjon = { enhetstype: '' }
			} else if (result.organisasjon.enhetstype === undefined) {
				result.organisasjon.enhetstype = ''
			}
			return result
		},
	}

	const modesWithPdldata: Mode[] = [
		TYPE.NY_BESTILLING,
		TYPE.NY_BESTILLING_FRA_MAL,
		TYPE.IMPORT_TESTNORGE,
	]
	const initialValues = builders[mode]()
	if (!modesWithPdldata.includes(mode)) {
		if (initialValues.pdldata !== undefined) {
			delete initialValues.pdldata
		}
	}
	const effectiveTimedOut = timedOutFagsystemer ?? personFoerLeggTil?.timedOutFagsystemer
	const effectiveAntall =
		mode === TYPE.IMPORT_TESTNORGE && importPersoner ? importPersoner.length : antall
	return {
		initialValues,
		gruppeId: gruppeId ?? null,
		antall: effectiveAntall,
		identtype,
		id2032,
		mal,
		opprettFraIdenter,
		personFoerLeggTil,
		leggTilPaaGruppe,
		importPersoner,
		identMaster,
		tidligereBestillinger,
		opprettOrganisasjon,
		gruppe,
		timedOutFagsystemer: effectiveTimedOut,
		is: {
			nyBestilling: mode === TYPE.NY_BESTILLING,
			nyBestillingFraMal: mode === TYPE.NY_BESTILLING_FRA_MAL,
			opprettFraIdenter: mode === TYPE.OPPRETT_FRA_IDENTER,
			leggTil: mode === TYPE.LEGG_TIL,
			leggTilPaaGruppe: mode === TYPE.LEGG_TIL_PAA_GRUPPE,
			nyOrganisasjon: mode === TYPE.NY_ORGANISASJON,
			nyOrganisasjonFraMal: mode === TYPE.NY_ORGANISASJON_FRA_MAL,
			nyStandardOrganisasjon: mode === TYPE.NY_STANDARD_ORGANISASJON,
			importTestnorge: mode === TYPE.IMPORT_TESTNORGE,
		},
	}
}
