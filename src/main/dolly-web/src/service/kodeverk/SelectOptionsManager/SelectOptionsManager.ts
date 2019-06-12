const selectOptions = {
	identtype: [{ value: 'FNR', label: 'FNR' }, { value: 'DNR', label: 'DNR' }],
	kjonn: [{ value: 'K', label: 'Kvinne' }, { value: 'M', label: 'Mann' }],
	kjonnBarn: [{ value: 'K', label: 'Jente' }, { value: 'M', label: 'Gutt' }],
	sivilstand: [
		{ value: 'GIFT', label: 'GIFT' },
		{ value: 'SKILT', label: 'SKILT' }
		//{ value: null, label: 'Tilfeldig' }
	],

	boolean: [{ value: true, label: 'Ja' }, { value: false, label: 'Nei' }],

	// SIGRUN
	inntektTjeneste: [
		{ value: 'Beregnet skatt', label: 'Beregnet skatt' },
		{ value: 'Summert skattegrunnlag', label: 'Summert skattegrunnlag' }
	],

	// AAREG
	aktoertype: [
		{ value: 'ORG', label: 'Organisasjon' },
		{ value: 'PERS', label: 'Enkeltmannsforetak' }
	],

	// Arena
	arenaBrukertype: [
		{ value: 'UTEN_SERVICEBEHOV', label: 'Uten servicebehov' },
		{ value: 'MED_SERVICEBEHOV', label: 'Med servicebehov' }
	],

	kvalifiseringsgruppe: [
		{ value: 'IKVAL', label: 'IKVAL - Standardinnsats' },
		{ value: 'BFORM', label: 'BFORM - Situasjonsbestemt innsats' },
		{ value: 'BATT', label: 'BATT - Spesielt tilpasset innsats' },
		{ value: 'VARIG', label: 'VARIG - Varig tilpasset innsats' }
	]
}

const SelectOptionsManager = attributeId => {
	return selectOptions[attributeId]
}

export default SelectOptionsManager
