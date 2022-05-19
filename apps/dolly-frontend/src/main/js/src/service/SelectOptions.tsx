interface SelectOptions {
	[name: string]: Array<{ value: boolean | string; label: string }>
}

const selectOptions: SelectOptions = {
	identtype: [
		{ value: 'FNR', label: 'FNR' },
		{ value: 'DNR', label: 'DNR' },
		{ value: 'NPID', label: 'NPID' },
	],

	kjonnBarn: [
		{ value: 'K', label: 'Jente' },
		{ value: 'M', label: 'Gutt' },
		{ value: 'U', label: 'Ukjent' },
	],

	barnType: [
		{ value: 'MITT', label: 'Hovedperson' },
		{ value: 'DITT', label: 'Partner' },
		{ value: 'FELLES', label: 'Begge' },
	],

	foreldreType: [
		{ value: 'MOR', label: 'Mor' },
		{ value: 'FAR', label: 'Far' },
	],

	foreldreTypePDL: [
		{ value: 'FORELDER', label: 'Forelder' },
		{ value: 'MOR', label: 'Mor' },
		{ value: 'MEDMOR', label: 'Medmor' },
		{ value: 'FAR', label: 'Far' },
	],

	pdlRelasjonTyper: [
		{ value: 'FORELDER', label: 'Forelder' },
		{ value: 'MOR', label: 'Mor' },
		{ value: 'MEDMOR', label: 'Medmor' },
		{ value: 'FAR', label: 'Far' },
		{ value: 'BARN', label: 'Barn' },
	],

	barnBorHos: [
		{ value: 'MEG', label: 'Hovedperson' },
		{ value: 'DEG', label: 'Partner' },
		{ value: 'OSS', label: 'Begge (sammen)' },
		{ value: 'BEGGE', label: 'Begge (delt bosted)' },
	],

	boolean: [
		{ value: true, label: 'Ja' },
		{ value: false, label: 'Nei' },
	],

	adresseNrType: [
		{ value: 'POSTNR', label: 'postnummer' },
		{ value: 'KOMMUNENR', label: 'kommunenummer' },
	],

	adresseType: [
		{ value: 'GATE', label: 'Norsk gateadresse' },
		{ value: 'STED', label: 'Norsk stedsadresse' },
		{ value: 'PBOX', label: 'Norsk postboksadresse' },
		{ value: 'UTAD', label: 'Utenlandsk adresse' },
	],

	sikkerhetstiltakType: [
		{ value: 'OPPH', label: 'Opphørt' },
		{ value: 'FYUS', label: 'FYUS - Fysisk utestengelse' },
		{ value: 'TFUS', label: 'TFUS - Telefonisk utestengelse' },
		{ value: 'FTUS', label: 'FTUS - Fysisk/telefonisk utestengelse' },
		{ value: 'DIUS', label: 'DIUS - Digital utestengelse' },
		{ value: 'TOAN', label: 'TOAN - To ansatte i samtale' },
	],

	// SIGRUN
	tjeneste: [
		{ value: 'BEREGNET_SKATT', label: 'Beregnet skatt' },
		{ value: 'SUMMERT_SKATTEGRUNNLAG', label: 'Summert skattegrunnlag' },
	],

	inntektssted: [
		{ value: 'Fastlandet', label: 'Fastlandet' },
		{ value: 'Svalbard', label: 'Svalbard' },
	],

	// AAREG
	aktoertype: [
		{ value: 'ORG', label: 'Organisasjon' },
		{ value: 'PERS', label: 'Privat' },
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
		{ value: '985675143', label: '985675143 - Stumtjener Butlerservice' },
	],

	//PDL
	master: [
		{ value: 'FREG', label: 'FREG' },
		{ value: 'PDL', label: 'PDL' },
	],

	kjoenn: [
		{ value: 'KVINNE', label: 'Kvinne' },
		{ value: 'MANN', label: 'Mann' },
		{ value: 'UKJENT', label: 'Ukjent' },
	],

	gradering: [
		{ value: 'STRENGT_FORTROLIG_UTLAND', label: 'Strengt fortrolig utland' },
		{ value: 'STRENGT_FORTROLIG', label: 'Strengt fortrolig' },
		{ value: 'FORTROLIG', label: 'Fortrolig' },
		{ value: 'UGRADERT', label: 'Ugradert' },
	],

	personstatus: [
		{ value: 'BOSATT', label: 'Bosatt' },
		{ value: 'UTFLYTTET', label: 'Utflyttet' },
		{ value: 'FORSVUNNET', label: 'Forsvunnet' },
		{ value: 'DOED', label: 'Død' },
		{ value: 'OPPHOERT', label: 'Opphørt' },
		{ value: 'FOEDSELSREGISTRERT', label: 'Fødselsregistert' },
		{ value: 'IKKE_BOSATT', label: 'Ikke bosatt' },
		{ value: 'MIDLERTIDIG', label: 'Midlertidig' },
		{ value: 'INAKTIV', label: 'Inaktiv' },
	],

	//PDLF - adresser
	adressetypeBostedsadresse: [
		{ value: 'VEGADRESSE', label: 'Vegadresse' },
		{ value: 'MATRIKKELADRESSE', label: 'Matrikkeladresse' },
		{ value: 'UTENLANDSK_ADRESSE', label: 'Utenlandsk adresse' },
		{ value: 'UKJENT_BOSTED', label: 'Ukjent bosted' },
	],
	//DNR og BOST kan kun ha utenlandsk adresse
	adressetypeUtenlandskBostedsadresse: [
		{ value: 'UTENLANDSK_ADRESSE', label: 'Utenlandsk adresse' },
	],

	adressetypeOppholdsadresse: [
		{ value: 'VEGADRESSE', label: 'Vegadresse' },
		{ value: 'MATRIKKELADRESSE', label: 'Matrikkeladresse' },
		{ value: 'UTENLANDSK_ADRESSE', label: 'Utenlandsk adresse' },
		{ value: 'OPPHOLD_ANNET_STED', label: 'Opphold annet sted' },
	],

	adressetypeKontaktadresse: [
		{ value: 'VEGADRESSE', label: 'Vegadresse' },
		{ value: 'UTENLANDSK_ADRESSE', label: 'Utenlandsk adresse' },
		{ value: 'POSTBOKSADRESSE', label: 'Postboksadresse' },
	],

	adressetypeDeltBosted: [
		{ value: 'PARTNER_ADRESSE', label: 'Adresse fra partner' },
		{ value: 'VEGADRESSE', label: 'Vegadresse' },
		{ value: 'MATRIKKELADRESSE', label: 'Matrikkeladresse' },
		{ value: 'UKJENT_BOSTED', label: 'Ukjent bosted' },
	],

	oppholdAnnetSted: [
		{ value: 'MILITAER', label: 'Militær' },
		{ value: 'UTENRIKS', label: 'Utenriks' },
		{ value: 'PAA_SVALBARD', label: 'På Svalbard' },
		{ value: 'PENDLER', label: 'Pendler' },
	],

	//PDLF - kontaktinformasjon dødsbo
	kontaktType: [
		{ value: 'ADVOKAT', label: 'Advokat' },
		{ value: 'ORGANISASJON', label: 'Organisasjon' },
		{ value: 'PERSON_FDATO', label: 'Person med identifikasjon' },
		{ value: 'NY_PERSON', label: 'Ny person' },
	],

	skifteform: [
		{ value: 'OFFENTLIG', label: 'Offentlig' },
		{ value: 'ANNET', label: 'Annet' },
	],

	// PDLF - falsk identitet
	identitetType: [
		{ value: 'UKJENT', label: 'Ukjent' },
		{ value: 'ENTYDIG', label: 'Ved identifikasjonsnummer' },
		{ value: 'OMTRENTLIG', label: 'Ved personopplysninger' },
	],

	// PDL - sivilstand
	sivilstandType: [
		{ value: 'UOPPGITT', label: 'Uoppgitt' },
		{ value: 'UGIFT', label: 'Ugift' },
		{ value: 'GIFT', label: 'Gift' },
		{ value: 'ENKE_ELLER_ENKEMANN', label: 'Enke eller enkemann' },
		{ value: 'SKILT', label: 'Skilt' },
		{ value: 'SEPARERT', label: 'Separert' },
		{ value: 'REGISTRERT_PARTNER', label: 'Registrert partner' },
		{ value: 'SEPARERT_PARTNER', label: 'Separtert partner' },
		{ value: 'SKILT_PARTNER', label: 'Skilt partner' },
		{ value: 'GJENLEVENDE_PARTNER', label: 'Gjenlevende partner' },
	],

	// PDL - foreldreansvar
	foreldreansvar: [
		{ value: 'FELLES', label: 'Felles' },
		{ value: 'MOR', label: 'Mor' },
		{ value: 'FAR', label: 'Far' },
		{ value: 'MEDMOR', label: 'Medmor' },
		{ value: 'ANDRE', label: 'Andre' },
		{ value: 'UKJENT', label: 'Ukjent' },
	],

	typeAnsvarlig: [
		{ value: 'EKSISTERENDE', label: 'Eksisterende person' },
		{ value: 'NY', label: 'Ny person' },
		{ value: 'UTEN_ID', label: 'Person uten identifikator' },
	],

	// Arena
	arenaBrukertype: [
		{ value: 'UTEN_SERVICEBEHOV', label: 'Uten servicebehov' },
		{ value: 'MED_SERVICEBEHOV', label: 'Med servicebehov' },
	],

	kvalifiseringsgruppe: [
		{ value: 'IKVAL', label: 'IKVAL - Standardinnsats' },
		{ value: 'BFORM', label: 'BFORM - Situasjonsbestemt innsats' },
		{ value: 'BATT', label: 'BATT - Spesielt tilpasset innsats' },
		{ value: 'VARIG', label: 'VARIG - Varig tilpasset innsats' },
	],

	rettighetKode: [
		{ value: 'DAGO', label: 'DAGO - Ordinære dagpenger' },
		{ value: 'PERM', label: 'PERM - Dagpenger under permittering' },
		{ value: 'FISK', label: 'FISK - Dagpenger under permittering fra fiskeindustri' },
		{ value: 'LONN', label: 'LONN - Lønnsgarantimidler' },
		{ value: 'DEKS', label: 'DEKS - Eksport - dagpenger' },
	],

	//KRRSTUB
	spraaktype: [
		{ value: 'nb', label: 'Norsk Bokmål' },
		{ value: 'nn', label: 'Norsk Nynorsk' },
		{ value: 'en', label: 'Engelsk' },
		{ value: 'se', label: 'Nordsamisk' },
	],

	// INST
	institusjonstype: [
		{ value: 'AS', label: 'Alders- og sykehjem' },
		{ value: 'HS', label: 'Helseinstitusjon' },
		{ value: 'FO', label: 'Fengsel' },
	],

	// UDI
	oppholdsstatus: [
		{ value: 'eosEllerEFTAOpphold', label: 'EØS- eller EFTA-opphold' },
		{ value: 'tredjelandsBorgere', label: 'Tredjelandsborgere' },
	],

	eosEllerEFTAtypeOpphold: [
		{
			value: 'eosEllerEFTABeslutningOmOppholdsrett',
			label: 'Beslutning om oppholdsrett fra EØS eller EFTA',
		},
		{
			value: 'eosEllerEFTAVedtakOmVarigOppholdsrett',
			label: 'Vedtak om varig oppholdsrett fra EØS eller EFTA',
		},
		{ value: 'eosEllerEFTAOppholdstillatelse', label: 'Oppholdstillatelse fra EØS eller EFTA' },
	],

	eosEllerEFTABeslutningOmOppholdsrett: [
		{ value: 'VARIG', label: 'Varig' },
		{ value: 'INGEN_INFORMASJON', label: 'Ingen informasjon' },
		{ value: 'FAMILIE', label: 'Familie' },
		{ value: 'TJENESTEYTING_ELLER_ETABLERING', label: 'Tjenesteyting eller etablering' },
		{ value: 'UAVKLART', label: 'Uavklart' },
	],

	eosEllerEFTAVedtakOmVarigOppholdsrett: [
		{ value: 'VARIG', label: 'Varig' },
		{ value: 'INGEN_INFORMASJON', label: 'Ingen informasjon' },
		{ value: 'FAMILIE', label: 'Familie' },
		{ value: 'TJENESTEYTING_ELLER_ETABLERING', label: 'Tjenesteyting eller etablering' },
		{ value: 'UAVKLART', label: 'Uavklart' },
	],

	eosEllerEFTAOppholdstillatelse: [
		{
			value: 'EGNE_MIDLER_ELLER_FASTE_PERIODISKE_YTELSER',
			label: 'Egne midler eller faste periodiske ytelser',
		},
		{ value: 'ARBEID', label: 'Arbeid' },
		{ value: 'UTDANNING', label: 'Utdanning' },
		{ value: 'TJENESTEYTING_ELLER_ETABLERING', label: 'Tjenesteyting eller etablering' },
		{ value: 'FAMILIE', label: 'Familie' },
		{ value: 'UAVKLART', label: 'Uavklart' },
	],

	tredjelandsBorgereValg: [
		{ value: 'oppholdSammeVilkaar', label: 'Oppholdstillatelse eller opphold på samme vilkår' },
		{
			value: 'ikkeOppholdSammeVilkaar',
			label: 'Ikke oppholdstillatelse eller ikke opphold på samme vilkår',
		},
		{ value: 'UAVKLART', label: 'Uavklart' },
	],

	oppholdstillatelseType: [
		{ value: 'PERMANENT', label: 'Permanent' },
		{ value: 'MIDLERTIDIG', label: 'Midlertidig' },
	],

	avslagGrunnlagOverig: [
		{ value: 'PERMANENT', label: 'Permanent' },
		{ value: 'BESKYTTELSE', label: 'Beskyttelse' },
		{
			value: 'STERKE_MENNESKELIGE_HENSYN_ELLER_SAERLIG_TILKNYTNING_TIL_NORGE',
			label: 'Sterke menneskelige hensyn',
		},
		{ value: 'ARBEID', label: 'Arbeid' },
		{ value: 'UTDANNING', label: 'Utdanning' },
		{ value: 'FAMILIE', label: 'Familie' },
		{ value: 'SELVSTENDIG_NAERINGSVIRKSOMHET', label: 'Selvstendig næringsvirksomhet' },
		{ value: 'ANNET', label: 'Annet' },
		{ value: 'UAVKLART', label: 'Uavklart' },
	],

	avslagGrunnlagTillatelseGrunnlagEOS: [
		{
			value: 'EGNE_MIDLER_ELLER_FASTE_PERIODISKE_YTELSER',
			label: 'Egne midler/faste ytelser',
		},
		{ value: 'ARBEID', label: 'Arbeid' },
		{ value: 'UTDANNING', label: 'Utdanning' },
		{ value: 'FAMILIE', label: 'Familie' },
		{ value: 'TJENESTEYTING_ELLER_ETABLERING', label: 'Tjenesteyting/etablering' },
		{ value: 'UAVKLART', label: 'Uavklart' },
	],

	avslagOppholdsrettBehandlet: [
		{ value: 'VARIG', label: 'Varig' },
		{ value: 'INGEN_INFORMASJON', label: 'Ingen informasjon' },
		{ value: 'FAMILIE', label: 'Familie' },
		{ value: 'TJENESTEYTING_ELLER_ETABLERING', label: 'Tjenesteyting/etablering' },
		{ value: 'UAVKLART', label: 'Uavklart' },
	],

	jaNeiUavklart: [
		{ value: 'JA', label: 'Ja' },
		{ value: 'NEI', label: 'Nei' },
		{ value: 'UAVKLART', label: 'Uavklart' },
	],

	typeArbeidsadgang: [
		{
			value: 'BESTEMT_ARBEIDSGIVER_ELLER_OPPDRAGSGIVER',
			label: 'Bestemt arbeidsgiver eller oppdragsgiver',
		},
		{ value: 'BESTEMT_ARBEID_ELLER_OPPDRAG', label: 'Bestemt arbeid eller oppdrag' },
		{
			value: 'BESTEMT_ARBEIDSGIVER_OG_ARBEID_ELLER_BESTEMT_OPPDRAGSGIVER_OG_OPPDRAG',
			label: 'Bestemt arbeidsgiver og arbeid eller bestemt oppdragsgiver og oppdrag',
		},
		{ value: 'GENERELL', label: 'Generell' },
		{ value: 'UAVKLART', label: 'Uavklart' },
	],

	arbeidsOmfang: [
		{ value: 'INGEN_KRAV_TIL_STILLINGSPROSENT', label: 'Ingen krav til stillingsprosent' },
		{ value: 'KUN_ARBEID_HELTID', label: 'Kun arbeid heltid' },
		{ value: 'KUN_ARBEID_DELTID', label: 'Kun arbeid deltid' },
		{ value: 'DELTID_SAMT_FERIER_HELTID', label: 'Deltid, samt ferier heltid' },
		{ value: 'UAVKLART', label: 'Uavklart' },
	],

	nyIdent: [
		{ value: false, label: 'Navn' },
		{ value: true, label: 'ID-nummer' },
	],

	identtypeUtenBost: [
		{ value: 'FNR', label: 'FNR' },
		{ value: 'DNR', label: 'DNR' },
	],

	// Brregstub:
	rolleEgenskap: [
		{ value: 'Deltager', label: 'Deltager' },
		{ value: 'Komplementar', label: 'Komplementar' },
		{ value: 'Kontaktperson', label: 'Kontaktperson' },
		{ value: 'Sameier', label: 'Sameier' },
		{ value: 'Styre', label: 'Styre' },
	],

	tilleggstype: [
		{ value: 'CO_NAVN', label: 'CO Navn' },
		{ value: 'LEILIGHET_NR', label: 'Leilighet nummer' },
		{ value: 'SEKSJON_NR', label: 'Seksjon nummer' },
		{ value: 'BOLIG_NR', label: 'Bolig nummer' },
	],

	tilleggstypeMidlertidig: [
		{ value: 'CO_NAVN', label: 'CO Navn' },
		{ value: 'BOLIG_NR', label: 'Bolig nummer' },
	],

	// Sykdom:
	aktivitet: [
		{ value: 'INGEN', label: 'Ingen' },
		{ value: 'AVVENTENDE', label: 'Avventende' },
	],

	// Organisasjon:
	maalform: [
		{ value: 'B', label: 'Bokmål' },
		{ value: 'N', label: 'Nynorsk' },
	],

	// Dokarkiv:
	avsenderType: [
		{ value: 'FNR', label: 'FNR' },
		{ value: 'ORGNR', label: 'ORGNR' },
	],

	tjenestepensjonYtelseType : [
		{ value: 'ALDER', label: 'Alderspensjon' },
		{ value: 'UFORE', label: 'Uførepensjon' },
		{ value: 'GJENLEVENDE', label: 'Gjenlevendepensjon' },
		{ value: 'BARN', label: 'Barnepensjon' },
		{ value: 'AFP', label: 'AFP-pensjon' },
	],

	tpOrdninger: [],
}

export const SelectOptionsManager = (attributeId: keyof SelectOptions) => selectOptions[attributeId]
