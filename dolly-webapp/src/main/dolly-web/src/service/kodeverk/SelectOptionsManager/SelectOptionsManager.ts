const selectOptions = {
	identtype: [
		{ value: 'FNR', label: 'FNR' },
		{ value: 'DNR', label: 'DNR' },
		{ value: 'BOST', label: 'BOST' }
	],
	kjonn: [{ value: 'K', label: 'Kvinne' }, { value: 'M', label: 'Mann' }],
	kjonnBarn: [{ value: 'K', label: 'Jente' }, { value: 'M', label: 'Gutt' }],
	sivilstand: [
		{ value: 'GIFT', label: 'GIFT' }
		// { value: 'SKILT', label: 'SKILT' }
		//{ value: null, label: 'Tilfeldig' }
	],

	boolean: [{ value: true, label: 'Ja' }, { value: false, label: 'Nei' }],
	stringBoolean: [{ value: 'true', label: 'Ja' }, { value: 'false', label: 'Nei' }],

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

	//PDLF - kontaktinformasjon dødsbo
	adressattype: [
		{ value: 'ADVOKAT', label: 'Advokat' },
		{ value: 'ORGANISASJON', label: 'Organisasjon' },
		{ value: 'PERSON_MEDID', label: 'Kontaktperson med id' },
		{ value: 'PERSON_UTENID', label: 'Kontaktperson uten id' }
	],

	skifteform: [{ value: 'OFFENTLIG', label: 'Offentlig' }, { value: 'ANNET', label: 'Annet' }],

	// PDLF - falsk identitet
	rettIdentitet: [
		{ value: 'UKJENT', label: 'Ukjent' },
		{ value: 'ENTYDIG', label: 'Ved identifikasjonsnummer' },
		{ value: 'OMTRENTLIG', label: 'Ved personopplysninger' }
	],

	kjonnFalskIdentitet: [
		{ value: 'KVINNE', label: 'Kvinne' },
		{ value: 'MANN', label: 'Mann' },
		{ value: 'UBESTEMT', label: 'Ubestemt' }
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
	],

	// INST
	institusjonstype: [
		{ value: 'AS', label: 'Alders- og sykehjem' },
		{ value: 'HS', label: 'Helseinstitusjon' },
		{ value: 'FO', label: 'Fengsel' }
	],

	varighet: [
		{ value: 'L', label: 'Langvarig' },
		{ value: 'K', label: 'Kortvarig' },
		{ value: 'U', label: 'Ubestemt' }
	]
}

const SelectOptionsManager = attributeId => {
	return selectOptions[attributeId]
}

export default SelectOptionsManager
