import { initialValuesBasedOnMal } from '~/components/bestillingsveileder/options/malOptions'

const TYPE = Object.freeze({
	NY_BESTILLING: 'NY_BESTILLING',
	NY_BESTILLING_FRA_MAL: 'NY_BESTILLING_FRA_MAL',
	OPPRETT_FRA_IDENTER: 'OPPRETT_FRA_IDENTER',
	LEGG_TIL: 'LEGG_TIL',
	NY_ORGANISASJON: 'NY_ORGANISASJON',
	NY_STANDARD_ORGANISASJON: 'NY_STANDARD_ORGANISASJON',
	IMPORT_TESTNORGE: 'IMPORT_TESTNORGE',
})

export const BVOptions = (
	{
		antall = 1,
		identtype = 'FNR',
		mal,
		opprettFraIdenter,
		personFoerLeggTil,
		tidligereBestillinger,
		importPersoner,
		identMaster,
		opprettOrganisasjon = null,
	} = {},
	gruppeId
) => {
	let initialValues = {
		antall: antall,
		navSyntetiskIdent: false,
		beskrivelse: null,
		pdldata: {
			opprettNyPerson: {
				identtype,
				syntetisk: false,
			},
		},
		importPersoner: null,
	}

	let initialValuesLeggTil = {
		antall,
		environments: [],
		beskrivelse: null,
		pdldata: {
			opprettNyPerson: null,
		},
	}

	let initialValuesOpprettFraIdenter = {
		pdldata: {
			opprettNyPerson: {},
		},
		opprettFraIdenter: opprettFraIdenter,
	}

	let initialValuesOrganisasjon = {
		organisasjon: {
			enhetstype: '',
		},
	}

	let initialValuesStandardOrganisasjon = {
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

	let bestType = TYPE.NY_BESTILLING

	if (mal) {
		bestType = TYPE.NY_BESTILLING_FRA_MAL
		initialValues = Object.assign(initialValues, initialValuesBasedOnMal(mal))
	}

	if (opprettFraIdenter) {
		bestType = TYPE.OPPRETT_FRA_IDENTER
		initialValues = initialValuesOpprettFraIdenter
	}

	if (personFoerLeggTil) {
		bestType = TYPE.LEGG_TIL
		initialValues = initialValuesLeggTil
	}

	if (importPersoner) {
		bestType = TYPE.IMPORT_TESTNORGE
		initialValues.antall = importPersoner.length
		initialValues.pdldata = undefined
		initialValues.importPersoner = importPersoner
		antall = importPersoner.length
	}

	if (opprettOrganisasjon) {
		if (opprettOrganisasjon === 'STANDARD') {
			bestType = TYPE.NY_STANDARD_ORGANISASJON
			initialValues = initialValuesStandardOrganisasjon
		} else {
			bestType = TYPE.NY_ORGANISASJON
			initialValues = initialValuesOrganisasjon
		}
	}

	return {
		initialValues,
		gruppeId,
		antall,
		identtype,
		mal,
		opprettFraIdenter,
		personFoerLeggTil,
		importPersoner,
		identMaster,
		tidligereBestillinger,
		is: {
			nyBestilling: bestType === TYPE.NY_BESTILLING,
			nyBestillingFraMal: bestType === TYPE.NY_BESTILLING_FRA_MAL,
			opprettFraIdenter: bestType === TYPE.OPPRETT_FRA_IDENTER,
			leggTil: bestType === TYPE.LEGG_TIL,
			nyOrganisasjon: bestType === TYPE.NY_ORGANISASJON,
			nyStandardOrganisasjon: bestType === TYPE.NY_STANDARD_ORGANISASJON,
			importTestnorge: bestType === TYPE.IMPORT_TESTNORGE,
		},
	}
}
