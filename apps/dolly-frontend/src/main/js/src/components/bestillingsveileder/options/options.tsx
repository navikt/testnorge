import { initialValuesBasedOnMal } from '@/components/bestillingsveileder/options/malOptions'

const TYPE = Object.freeze({
	NY_BESTILLING: 'NY_BESTILLING',
	NY_BESTILLING_FRA_MAL: 'NY_BESTILLING_FRA_MAL',
	OPPRETT_FRA_IDENTER: 'OPPRETT_FRA_IDENTER',
	LEGG_TIL: 'LEGG_TIL',
	LEGG_TIL_PAA_GRUPPE: 'LEGG_TIL_PAA_GRUPPE',
	NY_ORGANISASJON: 'NY_ORGANISASJON',
	NY_ORGANISASJON_FRA_MAL: 'NY_ORGANISASJON_FRA_MAL',
	NY_STANDARD_ORGANISASJON: 'NY_STANDARD_ORGANISASJON',
	IMPORT_TESTNORGE: 'IMPORT_TESTNORGE',
})

export const BVOptions = (
	{
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
	}: any = {},
	gruppeId: number,
	dollyEnvironments: any,
) => {
	let initialValues = {
		antall: antall || 1,
		beskrivelse: null,
		pdldata: {
			opprettNyPerson: {
				identtype: identtype || 'FNR',
				syntetisk: true,
				id2032: id2032 || false,
			},
		},
		importPersoner: null,
	}

	const initialValuesLeggTil = Object.freeze({
		antall: antall || 1,
		environments: [],
		beskrivelse: null,
		pdldata: {
			opprettNyPerson: null,
		},
	})

	const initialValuesLeggTilPaaGruppe = Object.freeze({
		environments: [],
		pdldata: {
			opprettNyPerson: null,
		},
	})

	const initialValuesOpprettFraIdenter = Object.freeze({
		pdldata: {
			opprettNyPerson: {},
		},
		opprettFraIdenter: opprettFraIdenter,
	})

	const initialValuesOrganisasjon = Object.freeze({
		organisasjon: {
			enhetstype: '',
		},
	})

	const initialValuesStandardOrganisasjon = Object.freeze({
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
	})

	let bestType = TYPE.NY_BESTILLING

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
		initialValues.antall = importPersoner.length
		initialValues.pdldata = undefined
		initialValues.importPersoner = importPersoner
		antall = importPersoner.length
	}

	if (mal) {
		if (bestType === TYPE.NY_BESTILLING) {
			bestType = TYPE.NY_BESTILLING_FRA_MAL
		}
		initialValues = {
			...initialValues,
			...initialValuesBasedOnMal(mal, dollyEnvironments),
		}
	}

	if (opprettOrganisasjon) {
		if (opprettOrganisasjon === 'STANDARD') {
			bestType = TYPE.NY_STANDARD_ORGANISASJON
			initialValues = initialValuesStandardOrganisasjon
		} else if (mal) {
			bestType = TYPE.NY_ORGANISASJON_FRA_MAL
			initialValues = {
				...initialValuesOrganisasjon,
				...initialValuesBasedOnMal(mal, dollyEnvironments),
			}
		} else {
			bestType = TYPE.NY_ORGANISASJON
			initialValues = { ...initialValuesOrganisasjon }
		}
	}

	return {
		initialValues,
		gruppeId,
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
