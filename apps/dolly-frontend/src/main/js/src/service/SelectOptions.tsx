interface SelectOptions {
	[name: string]: Array<{ value: boolean | string | number; label: string }>
}

const adresser = {
	vegadresse: 'Vegadresse',
	matrikkeladresse: 'Matrikkeladresse',
	utenlandskAdresse: 'Utenlandsk adresse',
}

const relasjoner = {
	mor: 'Mor',
	far: 'Far',
	medmor: 'Medmor',
	forelder: 'Forelder',
	barn: 'Barn',
}

const ingenInfo = 'Ingen informasjon'
const tjenesteytingEllerEtablering = 'Tjenesteyting eller etablering'

const selectOptions = {
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
		{ value: 'MOR', label: relasjoner.mor },
		{ value: 'FAR', label: relasjoner.far },
	],

	foreldreTypePDL: [
		{ value: 'FORELDER', label: relasjoner.forelder },
		{ value: 'MOR', label: relasjoner.mor },
		{ value: 'MEDMOR', label: relasjoner.medmor },
		{ value: 'FAR', label: relasjoner.far },
	],

	pdlRelasjonTyper: [
		{ value: 'FORELDER', label: relasjoner.forelder },
		{ value: 'MOR', label: relasjoner.mor },
		{ value: 'MEDMOR', label: relasjoner.medmor },
		{ value: 'FAR', label: relasjoner.far },
		{ value: 'BARN', label: relasjoner.barn },
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
		{ value: 'UTAD', label: adresser.utenlandskAdresse },
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
		{ value: 'VEGADRESSE', label: adresser.vegadresse },
		{ value: 'MATRIKKELADRESSE', label: adresser.matrikkeladresse },
		{ value: 'UTENLANDSK_ADRESSE', label: adresser.utenlandskAdresse },
		{ value: 'UKJENT_BOSTED', label: 'Ukjent bosted' },
	],
	//DNR og BOST kan kun ha utenlandsk adresse
	adressetypeUtenlandskBostedsadresse: [
		{ value: 'UTENLANDSK_ADRESSE', label: adresser.utenlandskAdresse },
	],

	adressetypeOppholdsadresse: [
		{ value: 'VEGADRESSE', label: adresser.vegadresse },
		{ value: 'MATRIKKELADRESSE', label: adresser.matrikkeladresse },
		{ value: 'UTENLANDSK_ADRESSE', label: adresser.utenlandskAdresse },
	],

	adressetypeKontaktadresse: [
		{ value: 'VEGADRESSE', label: adresser.vegadresse },
		{ value: 'UTENLANDSK_ADRESSE', label: adresser.utenlandskAdresse },
		{ value: 'POSTBOKSADRESSE', label: 'Postboksadresse' },
	],

	adressetypeDeltBosted: [
		{ value: 'PARTNER_ADRESSE', label: 'Adresse fra partner' },
		{ value: 'VEGADRESSE', label: adresser.vegadresse },
		{ value: 'MATRIKKELADRESSE', label: adresser.matrikkeladresse },
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
		{ value: 'SAMBOER', label: 'Samboer' },
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
		{ value: 'MOR', label: relasjoner.mor },
		{ value: 'FAR', label: relasjoner.far },
		{ value: 'MEDMOR', label: relasjoner.medmor },
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
		{ value: 'INGEN_INFORMASJON', label: ingenInfo },
		{ value: 'FAMILIE', label: 'Familie' },
		{ value: 'TJENESTEYTING_ELLER_ETABLERING', label: tjenesteytingEllerEtablering },
		{ value: 'UAVKLART', label: 'Uavklart' },
	],

	eosEllerEFTAVedtakOmVarigOppholdsrett: [
		{ value: 'VARIG', label: 'Varig' },
		{ value: 'INGEN_INFORMASJON', label: ingenInfo },
		{ value: 'FAMILIE', label: 'Familie' },
		{ value: 'TJENESTEYTING_ELLER_ETABLERING', label: tjenesteytingEllerEtablering },
		{ value: 'UAVKLART', label: 'Uavklart' },
	],

	eosEllerEFTAOppholdstillatelse: [
		{
			value: 'EGNE_MIDLER_ELLER_FASTE_PERIODISKE_YTELSER',
			label: 'Egne midler eller faste periodiske ytelser',
		},
		{ value: 'ARBEID', label: 'Arbeid' },
		{ value: 'UTDANNING', label: 'Utdanning' },
		{ value: 'TJENESTEYTING_ELLER_ETABLERING', label: tjenesteytingEllerEtablering },
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
		{ value: 'INGEN_INFORMASJON', label: ingenInfo },
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

	sakstype: [
		{ value: 'GENERELL_SAK', label: 'Generell sak' },
		{ value: 'FAGSAK', label: 'Fagsak' },
	],

	fagsaksystem: [
		{ value: 'AO01', label: 'Arena (AO01)' },
		{ value: 'BARNEBRILLER', label: 'Barnebriller' },
		{ value: 'BA', label: 'Barnetrygd (BA)' },
		{ value: 'BISYS', label: 'Bisys' },
		{ value: 'EF', label: 'Enslig forsørger (EF)' },
		{ value: 'EY', label: 'Etterlatteytelser (EY)' },
		{ value: 'FS36', label: 'Foreldrepengeløsningen (FS36)' },
		{ value: 'AO11', label: 'Grisen (AO11)' },
		{ value: 'HJELPEMIDLER', label: 'Hjelpemidler' },
		{ value: 'IT01', label: 'Infotrygd (IT01)' },
		{ value: 'KELVIN', label: 'Kelvin' },
		{ value: 'KONT', label: 'Kontantstøtte (KONT)' },
		{ value: 'FS38', label: 'Melosys (FS38)' },
		{ value: 'OMSORGSPENGER', label: 'Omsorgspenger' },
		{ value: 'OEBS', label: 'Oracle E-Business Suite (OEBS)' },
		{ value: 'PP01', label: 'Pesys (PP01)' },
		{ value: 'SUPSTONAD', label: 'Supplerende stønad (SUPSTONAD)' },
		{ value: 'K9', label: 'Sykdom i familien (K9)' },
		{ value: 'UFM', label: 'Unntak fra medlemskap (UFM)' },
		{ value: 'OB36', label: 'Utbetalingsreskontro (OB36)' },
	],

	tjenestepensjonYtelseType: [
		{ value: 'ALDER', label: 'Alderspensjon' },
		{ value: 'UFORE', label: 'Uførepensjon' },
		{ value: 'GJENLEVENDE', label: 'Gjenlevendepensjon' },
		{ value: 'BARN', label: 'Barnepensjon' },
		{ value: 'AFP', label: 'AFP-pensjon' },
		{ value: 'BETINGET_TP', label: 'Betinget TP' },
		{ value: 'LIVSVARIG_AFP ', label: 'Livsvarig AFP' },
		{ value: 'OPPSATT_BTO_PEN', label: 'Oppsatt bruttopensjon' },
		{ value: 'OVERGANGSTILLEGG', label: 'Overgangstillegg' },
		{ value: 'PAASLAGSPENSJON', label: 'Påslagspensjon' },
		{ value: 'SAERALDER', label: 'Særalder' },
	],

	tpOrdninger: [],

	//AFP
	statusAfp: [
		{ value: 'UKJENT', label: 'Ukjent' },
		{ value: 'INNVILGET', label: 'Innvilget' },
		{ value: 'SOKT', label: 'Søkt' },
		{ value: 'AVSLAG', label: 'Avslag' },
		{ value: 'IKKE_SOKT', label: 'Ikke søkt' },
	],

	afpPrivatResultat: [
		{ value: 'INNVILGET', label: 'Innvilget' },
		{ value: 'AVSLATT', label: 'Avslått' },
		{ value: 'TRUKKET', label: 'Trukket' },
		{ value: 'VENTER_PAA_FELLESORDNINGEN', label: 'Venter på fellesordningen' },
	],

	// PDL vergemaal:
	pdlVergemaalType: [
		{ value: 'ensligMindreaarigAsylsoeker', label: 'Enslig mindreårig asylsøker' },
		{
			value: 'ensligMindreaarigFlyktning',
			label: 'Enslig mindreårig flyktning (EMF) inklusive midlertidige saker for denne gruppen',
		},
		{ value: 'voksen', label: 'Voksen' },
		{ value: 'midlertidigForVoksen', label: 'Voksen midlertidig' },
		{ value: 'mindreaarig', label: 'Mindreårig (unntatt EMF)' },
		{ value: 'midlertidigForMindreaarig', label: 'Mindreårig midlertidig (unntatt EMF)' },
		{ value: 'forvaltningUtenforVergemaal', label: 'Forvaltning utenfor vergemål' },
		{ value: 'stadfestetFremtidsfullmakt', label: 'Fremtidsfullmakt' },
	],

	pdlVergemaalOmfang: [
		{
			value: 'utlendingssakerPersonligeOgOekonomiskeInteresser',
			label:
				'Ivareta personens interesser innenfor det personlige og økonomiske området herunder utlendingssaken (kun for EMA)',
		},
		{
			value: 'personligeOgOekonomiskeInteresser',
			label: 'Ivareta personens interesser innenfor det personlige og økonomiske området',
		},
		{
			value: 'oekonomiskeInteresser',
			label: 'Ivareta personens interesser innenfor det økonomiske området',
		},
		{
			value: 'personligeInteresser',
			label: 'Ivareta personens interesser innenfor det personlige område',
		},
	],

	// Alderspensjon:
	apUttaksgrad: [
		{ value: 100, label: '100%' },
		{ value: 80, label: '80%' },
		{ value: 60, label: '60%' },
		{ value: 50, label: '50%' },
		{ value: 40, label: '40%' },
		{ value: 20, label: '20%' },
	],

	// Uføretrygd:
	minimumInntektForUforhetType: [
		{ value: 'UNGUFOR', label: 'Minimum IFU fastsatt til sats for ung ufør' },
		{ value: 'GIFT', label: 'Minimum IFU fastsatt til sats for gift' },
		{ value: 'ENSLIG', label: 'Minimum IFU fastsatt til sats for enslig' },
	],

	barnetilleggType: [
		{ value: 'FELLESBARN', label: 'Fellesbarn' },
		{ value: 'SAERKULLSBARN', label: 'Særkullsbarn' },
	],

	inntektType: [
		{ value: 'ARBEIDSINNTEKT', label: 'Forventet arbeidsinntekt' },
		{ value: 'NAERINGSINNTEKT', label: 'Forventet næringsinntekt' },
		{ value: 'PENSJON_FRA_UTLANDET', label: 'Forventet pensjon fra utlandet' },
		{ value: 'UTENLANDS_INNTEKT', label: 'Forventet utenlandsinntekt' },
		{ value: 'ANDRE_PENSJONER_OG_YTELSER', label: 'Forventet andre pensjoner og ytelser' },
	],

	// Pensjonsavtale:
	avtaleKategori: [
		{ value: 'NONE', label: 'Ingenting' },
		{ value: 'UNKNOWN', label: 'Ukjent' },
		{ value: 'INDIVIDUELL_ORDNING', label: 'Individuell ordning' },
		{ value: 'PRIVAT_AFP', label: 'Privat AFP' },
		{ value: 'PRIVAT_TJENESTEPENSJON', label: 'Privat tjenestepensjon' },
		{ value: 'OFFENTLIG_TJENESTEPENSJON', label: 'Offentlig tjenestepensjon' },
		{ value: 'FOLKETRYGD', label: 'Folketrygden' },
	],

	maanedsvelger: [
		{ value: 1, label: 'Januar' },
		{ value: 2, label: 'Februar' },
		{ value: 3, label: 'Mars' },
		{ value: 4, label: 'April' },
		{ value: 5, label: 'Mai' },
		{ value: 6, label: 'Juni' },
		{ value: 7, label: 'Juli' },
		{ value: 8, label: 'August' },
		{ value: 9, label: 'September' },
		{ value: 10, label: 'Oktober' },
		{ value: 11, label: 'November' },
		{ value: 12, label: 'Desember' },
	],

	// Arbeidsplassen / Nav CV:
	nusKoder: [
		{ value: '2', label: 'Grunnskole' },
		{ value: '3', label: 'Folkehøgskole' },
		{ value: '4', label: 'Videregående/yrkesskole' },
		{ value: '5', label: 'Fagskole' },
		{ value: '6', label: 'Høyere utdanning, 1-4 år' },
		{ value: '7', label: 'Høyere utdanning, 4+ år' },
		{ value: '8', label: 'Doktorgrad' },
	],

	oppstart: [
		{ value: 'LEDIG_NAA', label: 'Kan begynne nå' },
		{ value: 'ETTER_TRE_MND', label: 'Har 3 måneders oppsigelse' },
		{ value: 'ETTER_AVTALE', label: 'Kan begynne etter nærmere avtale' },
	],

	fullmaktHandling: [
		{ value: '*', label: 'Alle' },
		{ value: 'LES', label: 'Les' },
		{ value: 'KOMMUNISER', label: 'Kommuniser' },
		{ value: 'SKRIV', label: 'Skriv' },
	],

	arbeidsmengde: [
		{ value: 'HELTID', label: 'Heltid' },
		{ value: 'DELTID', label: 'Deltid' },
	],

	arbeidstid: [
		{ value: 'DAGTID', label: 'Dagtid' },
		{ value: 'KVELD', label: 'Kveld' },
		{ value: 'NATT', label: 'Natt' },
		{ value: 'UKEDAGER', label: 'Ukedager' },
		{ value: 'LOERDAG', label: 'Lørdag' },
		{ value: 'SOENDAG', label: 'Søndag' },
		{ value: 'SKIFT', label: 'Skift' },
		{ value: 'VAKT', label: 'Vakt' },
		{ value: 'TURNUS', label: 'Turnus' },
	],

	ansettelsestype: [
		{ value: 'FAST', label: 'Fast' },
		{ value: 'VIKARIAT', label: 'Vikariat' },
		{ value: 'ENGASJEMENT', label: 'Engasjement' },
		{ value: 'PROSJEKT', label: 'Prosjekt' },
		{ value: 'SESONG', label: 'Sesong' },
		{ value: 'TRAINEE', label: 'Trainee' },
		{ value: 'LAERLING', label: 'Lærling' },
		{ value: 'SELVSTENDIG_NAERINGSDRIVENDE', label: 'Selvstendig næringsdrivende' },
		{ value: 'FERIEJOBB', label: 'Feriejobb' },
		{ value: 'ANNET', label: 'Annet' },
	],

	spraakNivaa: [
		{ value: 'IKKE_OPPGITT', label: 'Ikke oppgitt' },
		{ value: 'NYBEGYNNER', label: 'Nybegynner' },
		{ value: 'GODT', label: 'Godt' },
		{ value: 'VELDIG_GODT', label: 'Veldig godt' },
		{ value: 'FOERSTESPRAAK', label: 'Førstespråk (morsmål)' },
	],

	foererkortTyper: [
		{ value: 'A - Motorsykkel', label: 'A - Motorsykkel' },
		{ value: 'A1 - Lett motorsykkel', label: 'A1 - Lett motorsykkel' },
		{ value: 'A2 - Mellomtung motorsykkel', label: 'A2 - Mellomtung motorsykkel' },
		{ value: 'AM - Moped', label: 'AM - Moped' },
		{ value: 'B - Personbil', label: 'B - Personbil' },
		{ value: 'BE - Personbil med tilhenger', label: 'BE - Personbil med tilhenger' },
		{ value: 'C - Lastebil', label: 'C - Lastebil' },
		{ value: 'C1 - Lett lastebil', label: 'C1 - Lett lastebil' },
		{ value: 'C1E - Lett lastebil med tilhenger', label: 'C1E - Lett lastebil med tilhenger' },
		{ value: 'CE - Lastebil med tilhenger', label: 'CE - Lastebil med tilhenger' },
		{ value: 'D - Buss', label: 'D - Buss' },
		{ value: 'D1 - Minibuss', label: 'D1 - Minibuss' },
		{ value: 'T - Traktor', label: 'T - Traktor' },
		{ value: 'D1E - Minibuss med tilhenger', label: 'D1E - Minibuss med tilhenger' },
		{ value: 'DE - Buss med tilhenger', label: 'DE - Buss med tilhenger' },
		{ value: 'S - Snøscooter', label: 'S - Snøscooter' },
	],

	kursLengde: [
		{ value: 'UKJENT', label: 'Ukjent' },
		{ value: 'TIME', label: 'Timer' },
		{ value: 'DAG', label: 'Dager' },
		{ value: 'UKE', label: 'Uker' },
		{ value: 'MND', label: 'Måneder' },
	],

	jobbYrke: [
		{ value: '2611', label: 'Advokat' },
		{ value: '2341', label: 'Allmennlærer' },
		{ value: '7213', label: 'Bilskadereparatør' },
		{ value: '5223', label: 'Butikkmedarbeider' },
		{ value: '7115', label: 'Bygningssnekker' },
		{ value: '5141', label: 'Frisør' },
		{ value: '2223', label: 'Sykepleier' },
		{ value: '9212', label: 'Sauegjeter' },
		{ value: '6121', label: 'Saueklipper' },
		{ value: '7321', label: 'Typograf' },
		{ value: '7543', label: 'Ullklassifisør' },
		{ value: '9999', label: 'Utvikler' },
	],

	medlKilder: [
		{ value: 'srvmelosys', label: 'Melosys' },
		{ value: 'srvgosys', label: 'Gosys' },
		{ value: 'AVGSYS', label: 'Avgiftsystemet' },
		{ value: 'LAANEKASSEN', label: 'Lånekassen' },
	],

	omraade: [
		{ value: 'NO20.2012', label: 'Alta' },
		{ value: 'NO46.4601', label: 'Bergen' },
		{ value: 'NO18.1804', label: 'Bodø' },
		{ value: 'NO06.0602', label: 'Drammen' },
		{ value: 'NO01.0106', label: 'Fredrikstad' },
		{ value: 'NO04.0403', label: 'Hamar' },
		{ value: 'NO10.1001', label: 'Kristiansand' },
		{ value: 'NO02.0230', label: 'Lørenskog' },
		{ value: 'NO03', label: 'Oslo' },
		{ value: 'NO38.3806', label: 'Porsgrunn' },
		{ value: 'NO30.3017', label: 'Råde' },
		{ value: 'NO11.1103', label: 'Stavanger' },
		{ value: 'NO54.5401', label: 'Tromsø' },
		{ value: 'NO50.5001', label: 'Trondheim' },
		{ value: 'NO02.0211', label: 'Vestby' },
		{ value: 'NO15.1504', label: 'Ålesund' },
	],

	fagbrev: [
		{
			value: 'Fagbrev anleggsgartner',
			label: 'Fagbrev anleggsgartner',
			type: 'SVENNEBREV_FAGBREV',
		},
		{
			value: 'Fagbrev barne- og ungdomsarbeider',
			label: 'Fagbrev barne- og ungdomsarbeider',
			type: 'SVENNEBREV_FAGBREV',
		},
		{ value: 'Fagbrev butikkfag', label: 'Fagbrev butikkfag', type: 'SVENNEBREV_FAGBREV' },
		{ value: 'Fagbrev IKT-driftsfag', label: 'Fagbrev IKT-driftsfag', type: 'SVENNEBREV_FAGBREV' },
		{ value: 'Mesterbrev baker', label: 'Mesterbrev baker', type: 'MESTERBREV' },
		{ value: 'Mesterbrev billakkerer', label: 'Mesterbrev billakkerer', type: 'MESTERBREV' },
		{ value: 'Mesterbrev snekker', label: 'Mesterbrev snekker', type: 'MESTERBREV' },
		{ value: 'Svennebrev frisør', label: 'Svennebrev frisør', type: 'SVENNEBREV_FAGBREV' },
		{ value: 'Svennebrev gullsmed', label: 'Svennebrev gullsmed', type: 'SVENNEBREV_FAGBREV' },
		{
			value: 'Svennebrev mediegrafiker',
			label: 'Svennebrev mediegrafiker',
			type: 'SVENNEBREV_FAGBREV',
		},
		{
			value: 'Yrkeskompetanse interiørkonsulent',
			label: 'Yrkeskompetanse interiørkonsulent',
			type: 'SVENNEBREV_FAGBREV',
		},
		{
			value: 'Yrkeskompetanse tannhelsesekretær',
			label: 'Yrkeskompetanse tannhelsesekretær',
			type: 'SVENNEBREV_FAGBREV',
		},
	],

	kompetanse: [
		{ value: 'Arbeide selvstendig og effektivt', label: 'Arbeide selvstendig og effektivt' },
		{ value: 'Borhammer', label: 'Borhammer' },
		{ value: 'Design av romfartsprodukter', label: 'Design av romfartsprodukter' },
		{ value: 'Fange dyr i feller', label: 'Fange dyr i feller' },
		{ value: 'Flink til å samarbeide', label: 'Flink til å samarbeide' },
		{ value: 'God fysikk', label: 'God fysikk' },
		{ value: 'Gode norsk- og engelskkunnskaper', label: 'Gode norsk- og engelskkunnskaper' },
		{ value: 'Husdyrhold', label: 'Husdyrhold' },
		{ value: 'Java (Programmeringsspråk)', label: 'Java (Programmeringsspråk)' },
		{ value: 'Kunne arbeide ut fra egne ideer', label: 'Kunne arbeide ut fra egne ideer' },
		{ value: 'Muskelbygging', label: 'Muskelbygging' },
	],

	offentligGodkjenning: [
		{
			value: 'Arbeids - og oppholdstillatelse i Norge',
			label: 'Arbeids - og oppholdstillatelse i Norge',
		},
		{ value: 'Autorisasjon som fysioterapeut', label: 'Autorisasjon som fysioterapeut' },
		{
			value: 'Bevis for yrkessjåførkompetanse (YSK)',
			label: 'Bevis for yrkessjåførkompetanse (YSK)',
		},
		{ value: 'Båtførerbevis', label: 'Båtførerbevis' },
		{ value: 'Flygersertifikat', label: 'Flygersertifikat' },
		{
			value: 'Godkjenning som profesjonell pyrotekniker',
			label: 'Godkjenning som profesjonell pyrotekniker',
		},
		{
			value: 'Godkjenning/autorisasjon som brannkonstabel',
			label: 'Godkjenning/autorisasjon som brannkonstabel',
		},
		{
			value: 'Godkjenning/autorisasjon som revisor',
			label: 'Godkjenning/autorisasjon som revisor',
		},
		{ value: 'Helseattest', label: 'Helseattest' },
		{ value: 'Sertifikat varmearbeider', label: 'Sertifikat varmearbeider' },
		{ value: 'Statsautorisert eiendomsmegler', label: 'Statsautorisert eiendomsmegler' },
	],

	annenGodkjenning: [
		{ value: 'Autorisasjon som håndballtrener', label: 'Autorisasjon som håndballtrener' },
		{ value: 'Ballongførerbevis', label: 'Ballongførerbevis' },
		{ value: 'Bannerannonsering', label: 'Bannerannonsering' },
		{ value: 'Datakort IT-kompetanse', label: 'Datakort IT-kompetanse' },
		{ value: 'Kompetansebevis for dyretransport', label: 'Kompetansebevis for dyretransport' },
		{ value: 'Legeattest', label: 'Legeattest' },
		{ value: 'Sertifikat personlig trener', label: 'Sertifikat personlig trener' },
		{ value: 'Sertifisert førstehjelpsinstruktør', label: 'Sertifisert førstehjelpsinstruktør' },
		{
			value: 'Sertifisert Lekeplassutstyrsinspektør LEK',
			label: 'Sertifisert Lekeplassutstyrsinspektør LEK',
		},
		{ value: 'Testdrevet utvikling', label: 'Testdrevet utvikling' },
	],

	spraak: [
		{ value: 'Arabisk', label: 'Arabisk' },
		{ value: 'Engelsk', label: 'Engelsk' },
		{ value: 'Fransk', label: 'Fransk' },
		{ value: 'Hindi', label: 'Hindi' },
		{ value: 'Kinesisk', label: 'Kinesisk' },
		{ value: 'Norsk', label: 'Norsk' },
		{ value: 'Norsk tegnspråk', label: 'Norsk tegnspråk' },
		{ value: 'Nynorsk', label: 'Nynorsk' },
		{ value: 'Russisk', label: 'Russisk' },
		{ value: 'Samisk', label: 'Samisk' },
		{ value: 'Spansk', label: 'Spansk' },
		{ value: 'Tysk', label: 'Tysk' },
	],

	// Yrkesskader:
	klassifisering: [
		{ value: 'BAGATELLMESSIGE_SKADER', label: 'Bagatellmessige skader' },
		{ value: 'IMPORT', label: 'Import' },
		{ value: 'MANUELL', label: 'Manuell' },
		{ value: 'MELLOMSKADER', label: 'Mellomskader' },
		{ value: 'MULIG_NULLSKADE', label: 'Mulig nullskade' },
	],

	tidstype: [
		{ value: 'tidspunkt', label: 'Tidspunkt (skade)' },
		{ value: 'periode', label: 'Periode (sykdom)' },
		{ value: 'ukjent', label: 'Ukjent' },
	],

	ferdigstillSak: [
		{ value: 'GODKJENT', label: 'Godkjent' },
		{ value: 'AVSLAG', label: 'Avslag' },
	],

	miljoer: [
		{ value: 'q1', label: 'Q1' },
		{ value: 'q2', label: 'Q2' },
		{ value: 'q4', label: 'Q4' },
	],
}

export const SelectOptionsManager = (attributeId) => selectOptions[attributeId]
