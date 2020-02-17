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
	SEPA: ['REPA', 'SKPA', 'GJPA']
}

// Gyldige statuser før oppretting av ny partner
export const gyldigSisteStatus = ['ENKE', 'SKIL', 'GJPA', 'SKPA']

// Hjelpe funksjoner for uthenting
export const nesteGyldigStatuser = kode => {
	const nesteOpts = gyldigNesteStatus[kode] || gyldigNesteStatus.init
	return nesteOpts.map(statusKode => statuser[statusKode])
}

export const erOpprettNyPartnerGyldig = kode => gyldigSisteStatus.includes(kode)
