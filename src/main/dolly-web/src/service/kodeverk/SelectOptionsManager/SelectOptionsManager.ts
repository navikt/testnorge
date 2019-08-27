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
	],

	// UDI
	//! Hent riktige values
	opphold: [
		{ value: 'UAV', label: 'Uavklart' },
		{ value: 'EØS', label: 'EØS- eller EFTA-opphold' },
		{ value: 'OPP', label: 'Oppholdstillatelse eller opphold på samme vilkår' },
		{ value: 'IKK', label: 'Ikke oppholdstillatelse' }
	],

	//! Hent riktige values
	oppholdstype: [
		{ value: 'BES', label: 'Beslutning om oppholdsrett fra EØS eller EFTA' },
		{ value: 'VED', label: 'Vedtak om varig oppholdsrett fra EØS eller EFTA' },
		{ value: 'OPP', label: 'Oppholdstillatelse fra EØS eller EFTA' }
	],

	//! Hent riktige values
	ikkeOppholdGrunn: [
		{ value: 'UTV', label: 'Utvist med innreiseforbud' },
		{
			value: 'AVS',
			label: 'Avslag eller bortfall av PO-BOS eller tilbakekall eller formelt vedtak'
		},
		{ value: 'ØVR', label: 'Øvrig ikke opphold' }
	],

	harArbeidsadgang: [
		{ value: 'JA', label: 'Ja' },
		{ value: 'NEI', label: 'Nei' },
		{ value: 'UAV', label: 'Uavklart' }
	],

	typeArbeidsadgang: [
		{
			value: 'BESTEMT_ARBEIDSGIVER_ELLER_OPPDRAGSGIVER',
			label: 'Bestemt arbeidsgiver eller oppdragsgiver'
		},
		{ value: 'BESTEMT_ARBEID_ELLER_OPPDRAG', label: 'Bestemt arbeid eller oppdrag' },
		{
			value: 'BESTEMT_ARBEIDSGIVER_OG_ARBEID_ELLER_BESTEMT_OPPDRAGSGIVER_OG_OPPDRAG',
			label: 'Bestemt arbeidsgiver og arbeid eller bestemt oppdragsgiver og oppdrag'
		},
		{ value: 'GENERELL', label: 'Generell' },
		{ value: 'UAVKLART', label: 'Uavklart' }
	],

	arbeidsOmfang: [
		{ value: 'INGEN_KRAV_TIL_STILLINGSPROSENT', label: 'Ingen krav til stillingsprosent' },
		{ value: 'KUN_ARBEID_HELTID', label: 'Kun arbeid heltid' },
		{ value: 'KUN_ARBEID_DELTID', label: 'Kun arbeid deltid' },
		{ value: 'DELTID_SAMT_FERIER_HELTID', label: 'Deltid, samt ferier heltid' },
		{ value: 'UAVKLART', label: 'Uavklart' }
	]
}

const SelectOptionsManager = attributeId => {
	return selectOptions[attributeId]
}

export default SelectOptionsManager
