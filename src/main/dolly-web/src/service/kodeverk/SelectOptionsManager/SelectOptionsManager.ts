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
	yrke: [
		{ value: '2310114', label: '2310114 - HØYSKOLELÆRER' },
		{ value: '2320102', label: '2320102 - LÆRER (VIDEREGÅENDE SKOLE)' },
		{ value: '2521106', label: '2521106 - ADVOKAT' },
		{ value: '3231109', label: '3231109 - SYKEPLEIER' },
		{ value: '3310101', label: '3310101 - ALLMENNLÆRER' },
		{ value: '5141103', label: '5141103 - FRISØR' },
		{ value: '5221126', label: '5221126 - BUTIKKMEDARBEIDER' },
		{ value: '7125102', label: '7125102 - BYGNINGSSNEKKER' },
		{ value: '7216108', label: '7216108 - KAMMEROPERATØR' },
		{ value: '7217102', label: '7217102 - BILSKADEREPARATØR' },
		{ value: '7233108', label: '7233108 - SPESIALARBEIDER (LANDBRUKS- OG ANLEGGSMASKINMEKANIKK)' },
		{ value: '7421118', label: '7421118 - SNEKKER' }
	],

	aktoertype: [
		{ value: 'ORG', label: 'Organisasjon' },
		{ value: 'PERS', label: 'Enkeltmannsforetak' }
	]
}

const SelectOptionsManager = attributeId => {
	return selectOptions[attributeId]
}

export default SelectOptionsManager
