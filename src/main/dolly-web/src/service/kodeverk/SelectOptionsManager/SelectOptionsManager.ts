const selectOptions = {
	identtype: [{ value: 'FNR', label: 'FNR' }, { value: 'DNR', label: 'DNR' }],
	kjonn: [
		{ value: 'K', label: 'Kvinne' },
		{ value: 'M', label: 'Mann' }
		//{ value: null, label: 'Tilfeldig' }
	],
	kjonnBarn: [
		{ value: 'K', label: 'Jente' },
		{ value: 'M', label: 'Gutt' }
		//{ value: null, label: 'Tilfeldig' }
	],
	sivilstand: [
		{ value: 'GIFT', label: 'GIFT' },
		{ value: 'SKILT', label: 'SKILT' }
		//{ value: null, label: 'Tilfeldig' }
	],

	// SIGRUN
	inntektTjeneste: [
		{ value: 'Beregnet skatt', label: 'Beregnet skatt' },
		{ value: 'Summert skattegrunnlag', label: 'Summert skattegrunnlag' }
	],

	boolean: [{ value: 'JA', label: 'Ja' }, { value: 'NEI', label: 'Nei' }]
}

const SelectOptionsManager = attributeId => {
	return selectOptions[attributeId]
}

export default SelectOptionsManager
