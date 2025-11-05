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
}

export const deriveBestillingsveilederState = (
	config: RawBestillingsveilederConfig = {},
	environments: any,
): BestillingsveilederDerivedState => {
	const {
		gruppeId = 0,
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
		timedOutFagsystemer,
	} = config

	let bestType = TYPE.NY_BESTILLING

	let initialValues: any = {
		antall: antall,
		beskrivelse: null,
		pdldata: {
			opprettNyPerson: {
				identtype,
				syntetisk: true,
				id2032,
			},
		},
		importPersoner: null,
	}

	const initialValuesLeggTil = {
		antall: antall,
		beskrivelse: null,
		// pdldata: { opprettNyPerson: null },
		importPersoner: null,
		environments: [],
	}

	const initialValuesLeggTilPaaGruppe = {
		antall: antall,
		beskrivelse: null,
		// pdldata: { opprettNyPerson: null },
		importPersoner: null,
		environments: [],
	}

	const initialValuesOpprettFraIdenter = {
		antall: antall,
		beskrivelse: null,
		// pdldata: { opprettNyPerson: {} },
		importPersoner: null,
		opprettFraIdenter,
	}

	const initialValuesOrganisasjon = {
		antall: antall,
		organisasjon: { enhetstype: '' },
	}

	const initialValuesStandardOrganisasjon = {
		antall: antall,
		importPersoner: null,
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
	}

	if (opprettFraIdenter) {
		bestType = TYPE.OPPRETT_FRA_IDENTER
		initialValues = initialValuesOpprettFraIdenter
	}

	if (personFoerLeggTil) {
		bestType = TYPE.LEGG_TIL
		initialValues = initialValuesLeggTil
	}

	if (leggTilPaaGruppe) {
		bestType = TYPE.LEGG_TIL_PAA_GRUPPE
		initialValues = initialValuesLeggTilPaaGruppe
	}

	if (importPersoner) {
		bestType = TYPE.IMPORT_TESTNORGE
		initialValues = {
			...initialValues,
			antall: importPersoner.length,
			importPersoner,
		}
	}

	if (mal) {
		if (bestType === TYPE.NY_BESTILLING) bestType = TYPE.NY_BESTILLING_FRA_MAL
		initialValues = { ...initialValues, ...initialValuesBasedOnMal(mal, environments), mal: mal.id }
	}

	if (opprettOrganisasjon) {
		if (opprettOrganisasjon === 'STANDARD') {
			bestType = TYPE.NY_STANDARD_ORGANISASJON
			initialValues = initialValuesStandardOrganisasjon
		} else if (mal) {
			bestType = TYPE.NY_ORGANISASJON_FRA_MAL
			initialValues = {
				...initialValuesOrganisasjon,
				...initialValuesBasedOnMal(mal, environments),
				mal: mal.id,
			}
		} else {
			bestType = TYPE.NY_ORGANISASJON
			initialValues = initialValuesOrganisasjon
		}
	}

	const effectiveTimedOut = timedOutFagsystemer ?? personFoerLeggTil?.timedOutFagsystemer

	return {
		initialValues,
		gruppeId: gruppeId === 0 ? null : gruppeId,
		antall,
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
			nyBestilling: bestType === TYPE.NY_BESTILLING,
			nyBestillingFraMal: bestType === TYPE.NY_BESTILLING_FRA_MAL,
			opprettFraIdenter: bestType === TYPE.OPPRETT_FRA_IDENTER,
			leggTil: bestType === TYPE.LEGG_TIL,
			leggTilPaaGruppe: bestType === TYPE.LEGG_TIL_PAA_GRUPPE,
			nyOrganisasjon: bestType === TYPE.NY_ORGANISASJON,
			nyOrganisasjonFraMal: bestType === TYPE.NY_ORGANISASJON_FRA_MAL,
			nyStandardOrganisasjon: bestType === TYPE.NY_STANDARD_ORGANISASJON,
			importTestnorge: bestType === TYPE.IMPORT_TESTNORGE,
		},
	}
}
