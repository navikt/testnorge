import _get from 'lodash/get'

export const statuser = {
	ENKE: {
		label: 'Enke/-mann',
		value: 'ENKE'
	},
	GIFT: {
		label: 'Gift',
		value: 'GIFT'
	},
	GJPA: {
		label: 'Gjenlevende partner',
		value: 'GJPA'
	},
	REPA: {
		label: 'Registrert partner',
		value: 'REPA'
	},
	SAMB: {
		label: 'Samboer',
		value: 'SAMB'
	},
	SEPR: {
		label: 'Separert',
		value: 'SEPR'
	},
	SEPA: {
		label: 'Separert partner',
		value: 'SEPA'
	},
	SKIL: {
		label: 'Skilt',
		value: 'SKIL'
	},
	SKPA: {
		label: 'Skilt partner',
		value: 'SKPA'
	}
}

// Gyldige statuser før oppretting av nytt forhold
export const gyldigNesteStatus = {
	init: ['GIFT', 'REPA', 'SAMB'],
	SAMB: ['GIFT', 'REPA', 'SAMB'],
	GIFT: ['ENKE', 'SEPR', 'SKIL'],
	REPA: ['GJPA', 'SEPA', 'SKPA'],
	SEPR: ['GIFT', 'SKIL', 'ENKE'],
	SEPA: ['REPA', 'SKPA', 'GJPA'],
	ALLE: ['ENKE', 'GIFT', 'GJPA', 'REPA', 'SAMB', 'SEPR', 'SEPA', 'SKIL', 'SKPA']
}

// Gyldige statuser før oppretting av ny partner
export const gyldigSisteStatus = ['ENKE', 'SKIL', 'GJPA', 'SKPA', 'SAMB']

// Hjelpefunksjoner for uthenting
const sisteSivilstandKode = sivilstander =>
	sivilstander.length > 1 ? sivilstander[sivilstander.length - 2].sivilstand : null

export const nesteGyldigStatuser = sivilstander => {
	const kode = sisteSivilstandKode(sivilstander)
	const nesteOpts = gyldigNesteStatus[kode] || gyldigNesteStatus.init
	return nesteOpts.map(statusKode => statuser[statusKode])
}

export const erOpprettNyPartnerGyldig = kode => gyldigSisteStatus.includes(kode)

export const tomSisteSivilstand = (formikBag, sivilstandBasePath) => {
	const sivilstander = _get(formikBag.values, sivilstandBasePath, [])
	if (sivilstander.length < 1) return false

	const antallSivilstander = sivilstander.length
	return sivilstander[antallSivilstander - 1].sivilstand.length < 1
}
