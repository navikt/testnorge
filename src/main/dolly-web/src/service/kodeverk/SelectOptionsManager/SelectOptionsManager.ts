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
	tjeneste: [
		{ value: 'Beregnet skatt', label: 'Beregnet skatt' },
		{ value: 'Summert skattegrunnlag', label: 'Summert skattegrunnlag' }
	],

	// AAREG
	aktoertype: [
		{ value: 'ORG', label: 'Organisasjon' },
		{ value: 'PERS', label: 'Enkeltmannsforetak' }
	],

	//PDLF - kontaktinformasjon dødsbo
	adressatType: [
		{ value: 'ADVOKAT', label: 'Advokat' },
		{ value: 'ORGANISASJON', label: 'Organisasjon' },
		{ value: 'PERSON_MEDID', label: 'Kontaktperson med id' },
		{ value: 'PERSON_UTENID', label: 'Kontaktperson uten id' }
	],

	skifteform: [{ value: 'OFFENTLIG', label: 'Offentlig' }, { value: 'ANNET', label: 'Annet' }],

	// PDLF - falsk identitet
	identitetType: [
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
	oppholdsstatus: [
		{ value: 'eosEllerEFTAOpphold', label: 'EØS- eller EFTA-opphold' },
		{ value: 'oppholdSammeVilkaar', label: 'Tredjelandsborgere' }
	],

	eosEllerEFTAtypeOpphold: [
		{
			value: 'eosEllerEFTABeslutningOmOppholdsrett',
			label: 'Beslutning om oppholdsrett fra EØS eller EFTA'
		},
		{
			value: 'eosEllerEFTAVedtakOmVarigOppholdsrett',
			label: 'Vedtak om varig oppholdsrett fra EØS eller EFTA'
		},
		{ value: 'eosEllerEFTAOppholdstillatelse', label: 'Oppholdstillatelse fra EØS eller EFTA' }
	],

	//Hvordan få samme innhold i to ulike keys?
	eosEllerEFTABeslutningOmOppholdsrett: [
		{ value: 'VARIG', label: 'Varig' },
		{ value: 'INGEN_INFORMASJON', label: 'Ingen informasjon' },
		{ value: 'FAMILIE', label: 'Familie' },
		{ value: 'TJENESTEYTING_ELLER_ETABLERING', label: 'Tjenesteyting eller etablering' },
		{ value: 'UAVKLART', label: 'Uavklart' }
	],

	eosEllerEFTAVedtakOmVarigOppholdsrett: [
		{ value: 'VARIG', label: 'Varig' },
		{ value: 'INGEN_INFORMASJON', label: 'Ingen informasjon' },
		{ value: 'FAMILIE', label: 'Familie' },
		{ value: 'TJENESTEYTING_ELLER_ETABLERING', label: 'Tjenesteyting eller etablering' },
		{ value: 'UAVKLART', label: 'Uavklart' }
	],

	eosEllerEFTAOppholdstillatelse: [
		{
			value: 'EGNE_MIDLER_ELLER_FASTE_PERIODISKE_YTELSER',
			label: 'Egne midler eller faste periodiske ytelser'
		},
		{ value: 'ARBEID', label: 'Arbeid' },
		{ value: 'UTDANNING', label: 'Utdanning' },
		{ value: 'TJENESTEYTING_ELLER_ETABLERING', label: 'Tjenesteyting eller etablering' },
		{ value: 'FAMILIE', label: 'Familie' },
		{ value: 'UAVKLART', label: 'Uavklart' }
	],

	tredjelandsBorgereValg: [
		{ value: 'oppholdSammeVilkaar', label: 'Oppholdstillatelse eller opphold på samme vilkår' },
		{
			value: 'ikkeOppholdSammeVilkaar',
			label: 'Ikke oppholdstillatelse eller ikke opphold på samme vilkår'
		},
		{ value: 'UAVKLART', label: 'Uavklart' }
	],

	oppholdstillatelseType: [
		{ value: 'PERMANENT', label: 'Permanent' },
		{ value: 'MIDLERTIDIG', label: 'Midlertidig' }
	],

	jaNeiUavklart: [
		{ value: 'JA', label: 'Ja' },
		{ value: 'NEI', label: 'Nei' },
		{ value: 'UAVKLART', label: 'Uavklart' }
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
	],

	nyIdent: [{ value: 'navn', label: 'Navn' }, { value: 'idnummer', label: 'ID-nummer' }]
}

const SelectOptionsManager = attributeId => {
	return selectOptions[attributeId]
}

export default SelectOptionsManager
