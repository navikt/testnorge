interface SelectOptions {
	[name: string]: Array<{ value: boolean | string; label: string }>
}

const selectOptions: SelectOptions = {
	identtype: [
		{ value: 'FNR', label: 'FNR' },
		{ value: 'DNR', label: 'DNR' },
		{ value: 'BOST', label: 'BOST' }
	],
	identtypeBarn: [
		{ value: 'FNR', label: 'FNR' },
		{ value: 'DNR', label: 'DNR' },
		{ value: 'BOST', label: 'BOST' },
		{ value: 'FDAT', label: 'FDAT (dødfødt)' }
	],

	kjonnBarn: [
		{ value: 'K', label: 'Jente' },
		{ value: 'M', label: 'Gutt' },
		{ value: 'U', label: 'Ukjent' }
	],

	barnType: [
		{ value: 'MITT', label: 'Hovedperson' },
		{ value: 'DITT', label: 'Partner' },
		{ value: 'FELLES', label: 'Begge' }
	],

	barnBorHos: [
		{ value: 'MEG', label: 'Hovedperson' },
		{ value: 'DEG', label: 'Partner' },
		{ value: 'OSS', label: 'Begge (sammen)' },
		{ value: 'BEGGE', label: 'Begge (delt bosted)' }
	],

	boolean: [
		{ value: true, label: 'Ja' },
		{ value: false, label: 'Nei' }
	],

	adresseNrType: [
		{ value: 'POSTNR', label: 'postnummer' },
		{ value: 'KOMMUNENR', label: 'kommunenummer' }
	],

	adresseType: [
		{ value: 'GATE', label: 'Norsk gateadresse' },
		{ value: 'STED', label: 'Norsk stedsadresse' },
		{ value: 'PBOX', label: 'Norsk postboksadresse' },
		{ value: 'UTAD', label: 'Utenlandsk adresse' }
	],

	// SIGRUN
	tjeneste: [
		{ value: 'BEREGNET_SKATT', label: 'Beregnet skatt' },
		{ value: 'SUMMERT_SKATTEGRUNNLAG', label: 'Summert skattegrunnlag' }
	],

	inntektssted: [
		{ value: 'Fastlandet', label: 'Fastlandet' },
		{ value: 'Svalbard', label: 'Svalbard' }
	],

	// AAREG
	aktoertype: [
		{ value: 'ORG', label: 'Organisasjon' },
		{ value: 'PERS', label: 'Privat' }
	],

	orgnummer: [
		{ value: '972674812', label: '972674812 - Pengeløs Sparebank' },
		{ value: '900668490', label: '900668490 - Lama Utleiren' },
		{ value: '907670200', label: '907670200 - Klonelabben' },
		{ value: '824771334', label: '824771334 - Sjokkerende Elektriker' },
		{ value: '839942902', label: '839942902 - Hårreisende Frisør' },
		{ value: '896929113', label: '896929113 - Sauefabrikk' },
		{ value: '967170234', label: '967170234 - Snill Torpedo' },
		{ value: '805824354', label: '805824354 - Vegansk Slakteri' },
		{ value: '980477643', label: '980477643 - Sofakroken Treningssenter' },
		{ value: '985675143', label: '985675143 - Stumtjener Butlerservice' }
	],

	//PDLF - kontaktinformasjon dødsbo
	adressatType: [
		{ value: 'ADVOKAT', label: 'Advokat' },
		{ value: 'ORGANISASJON', label: 'Organisasjon' },
		{ value: 'PERSON_MEDID', label: 'Kontaktperson med id' },
		{ value: 'PERSON_UTENID', label: 'Kontaktperson uten id' }
	],

	skifteform: [
		{ value: 'OFFENTLIG', label: 'Offentlig' },
		{ value: 'ANNET', label: 'Annet' }
	],

	// PDLF - falsk identitet
	identitetType: [
		{ value: 'UKJENT', label: 'Ukjent' },
		{ value: 'ENTYDIG', label: 'Ved identifikasjonsnummer' },
		{ value: 'OMTRENTLIG', label: 'Ved personopplysninger' }
	],

	kjoennFalskIdentitet: [
		{ value: 'KVINNE', label: 'Kvinne' },
		{ value: 'MANN', label: 'Mann' },
		{ value: 'UKJENT', label: 'Ukjent' }
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

	//KRRSTUB
	spraaktype: [
		{ value: 'nb', label: 'Norsk Bokmål' },
		{ value: 'nn', label: 'Norsk Nynorsk' },
		{ value: 'en', label: 'Engelsk' },
		{ value: 'se', label: 'Nordsamisk' }
	],

	// INST
	institusjonstype: [
		{ value: 'AS', label: 'Alders- og sykehjem' },
		{ value: 'HS', label: 'Helseinstitusjon' },
		{ value: 'FO', label: 'Fengsel' }
	],

	// UDI
	oppholdsstatus: [
		{ value: 'eosEllerEFTAOpphold', label: 'EØS- eller EFTA-opphold' },
		{ value: 'tredjelandsBorgere', label: 'Tredjelandsborgere' }
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

	avslagGrunnlagOverig: [
		{ value: 'PERMANENT', label: 'Permanent' },
		{ value: 'BESKYTTELSE', label: 'Beskyttelse' },
		{
			value: 'STERKE_MENNESKELIGE_HENSYN_ELLER_SAERLIG_TILKNYTNING_TIL_NORGE',
			label: 'Sterke menneskelige hensyn'
		},
		{ value: 'ARBEID', label: 'Arbeid' },
		{ value: 'UTDANNING', label: 'Utdanning' },
		{ value: 'FAMILIE', label: 'Familie' },
		{ value: 'SELVSTENDIG_NAERINGSVIRKSOMHET', label: 'Selvstendig næringsvirksomhet' },
		{ value: 'ANNET', label: 'Annet' },
		{ value: 'UAVKLART', label: 'Uavklart' }
	],

	avslagGrunnlagTillatelseGrunnlagEOS: [
		{
			value: 'EGNE_MIDLER_ELLER_FASTE_PERIODISKE_YTELSER',
			label: 'Egne midler/faste ytelser'
		},
		{ value: 'ARBEID', label: 'Arbeid' },
		{ value: 'UTDANNING', label: 'Utdanning' },
		{ value: 'FAMILIE', label: 'Familie' },
		{ value: 'TJENESTEYTING_ELLER_ETABLERING', label: 'Tjenesteyting/etablering' },
		{ value: 'UAVKLART', label: 'Uavklart' }
	],

	avslagOppholdsrettBehandlet: [
		{ value: 'VARIG', label: 'Varig' },
		{ value: 'INGEN_INFORMASJON', label: 'Ingen informasjon' },
		{ value: 'FAMILIE', label: 'Familie' },
		{ value: 'TJENESTEYTING_ELLER_ETABLERING', label: 'Tjenesteyting/etablering' },
		{ value: 'UAVKLART', label: 'Uavklart' }
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

	nyIdent: [
		{ value: false, label: 'Navn' },
		{ value: true, label: 'ID-nummer' }
	],

	identtypeUtenBost: [
		{ value: 'FNR', label: 'FNR' },
		{ value: 'DNR', label: 'DNR' }
	],

	// Brregstub:
	rolleEgenskap: [
		{ value: 'Deltager', label: 'Deltager' },
		{ value: 'Komplementar', label: 'Komplementar' },
		{ value: 'Kontaktperson', label: 'Kontaktperson' },
		{ value: 'Sameier', label: 'Sameier' },
		{ value: 'Styre', label: 'Styre' }
	],

	tilleggstype: [
		{ value: 'CO_NAVN', label: 'CO Navn' },
		{ value: 'LEILIGHET_NR', label: 'Leilighet nummer' },
		{ value: 'SEKSJON_NR', label: 'Seksjon nummer' },
		{ value: 'BOLIG_NR', label: 'Bolig nummer' }
	],

	tilleggstypeMidlertidig: [
		{ value: 'CO_NAVN', label: 'CO Navn' },
		{ value: 'BOLIG_NR', label: 'Bolig nummer' }
	],

	// Sykdom:
	aktivitet: [
		{ value: 'INGEN', label: 'Ingen' },
		{ value: 'AVVENTENDE', label: 'Avventende' }
	],

	// Organisasjon:
	maalform: [
		{ value: 'B', label: 'Bokmål' },
		{ value: 'N', label: 'Nynorsk' }
	]
}

export const SelectOptionsManager = (attributeId: keyof SelectOptions) => selectOptions[attributeId]
