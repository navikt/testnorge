const texts = {
	// INNTEKTSTYPE
	inntektstype: 'Inntektstype',
	LOENNSINNTEKT: 'Lønnsinntekt',
	YTELSE_FRA_OFFENTLIGE: 'Ytelse fra offentige',
	PENSJON_ELLER_TRYGD: 'Pensjon eller trygd',
	NAERINGSINNTEKT: 'Næringsinntekt',

	// BOOLEANS
	inngaarIGrunnlagForTrekk: 'Inngår i grunnlag for trekk',
	utloeserArbeidsgiveravgift: 'Utløser arbeidsgiveravgift',
	true: 'Ja',
	false: 'Nei',

	// FORDEL
	fordel: 'Fordel',
	naturalytelse: 'Naturalytelse',
	kontantytelse: 'Kontantytelse',
	utgiftsgodtgjoerelse: 'Utgiftsgodtgjørelse',

	// SKATTE- OG AVGIFTSEREGEL
	skatteOgAvgiftsregel: 'Skatte- og avgiftsregel',
	svalbard: 'Svalbard',
	janMayenOgBilandene: 'Jan Mayen og bilandene',
	saerskiltFradragForSjoefolk: 'Særskilt fradrag for sjøfolk',
	veldedigEllerAllmennyttigOrganisasjon: 'Veldedig eller allmennyttig organisasjon',
	dagmammaIBarnetsHjem: 'Dagmamma i barnets hjem',
	loennsarbeidIHjemmet: 'Lønnsarbeid i hjemmet',
	nettoloenn: 'Nettolønn',
	nettoloennForSjoefolk: 'Nettolønn for sjøfolk',
	skattefriOrganisasjon: 'Skattefri organisasjon',
	kildeskattPaaPensjoner: 'Kildeskatt på pensjoner',

	// LAND
	skattemessigBosattILand: 'Skattemessig bosatt i land',
	opptjeningsland: 'Opptjeningsland',

	// BESKRIVELSE LØNNSINNTEKT
	beskrivelse: 'Beskrivelse',
	kostDoegn: 'Kost (døgn)',
	losji: 'Losji',
	fastloenn: 'Fastlønn',
	timeloenn: 'Timelønn',
	fastTillegg: 'Fast tillegg',
	uregelmessigeTilleggKnyttetTilArbeidetTid: 'Uregelmessige tillegg knyttet til arbeidet tid',
	helligdagstillegg: 'Helligdagstillegg',
	uregelmessigeTilleggKnyttetTilIkkeArbeidetTid:
		'Uregelmessige tillegg knyttet til ikke-arbeidet tid',
	bonus: 'Bonus',
	overtidsgodtgjoerelse: 'Overtidsgodtgjørelse',
	styrehonorarOgGodtgjoerelseVerv: 'Styrehonorar og godtgjoerelse i forbindelse med verv',
	kommunalOmsorgsloennOgFosterhjemsgodtgjoerelse: 'Kommunal omsorgslønn og fosterhjemsgodtgjørelse',
	sluttvederlag: 'Sluttvederlag',
	feriepenger: 'Feriepenger',
	annet: 'Annet',
	ikkeSkattepliktigLoennFraUtenlandskDiplomKonsulStasjon:
		'Ikke skattepliktig lønn, utenlandsk diplomatisk/konsulær stasjon',
	skattepliktigDelForsikringer: 'Skattepliktig del av forsikringer',
	hyretillegg: 'Hyretillegg',
	bil: 'Bil',
	kostDager: 'Kost (dager)',
	rentefordelLaan: 'Rentefordel lån',
	bolig: 'Bolig',
	fondForIdrettsutoevere: 'Fond for idrettsutøvere',
	elektroniskKommunikasjon: 'Elektronisk kommunikasjon',
	opsjoner: 'Opsjoner',
	aksjerGrunnfondsbevisTilUnderkurs: 'Aksjer/grunnfondsbevis til underkurs',
	loennForBarnepassIBarnetsHjem: 'Lønn for barnepass i barnets hjem',
	loennUtbetaltAvVeldedigEllerAllmennyttigInstitusjonEllerOrganisasjon:
		'Lønn utbetalt av veldedig/allmennyttig institusjon/organisasjon',
	loennTilPrivatpersonerForArbeidIHjemmet: 'Lønn til privatpersoner for arbeid i hjemmet',
	kostbesparelseIHjemmet: 'Kostbesparelse i hjemmet',
	smusstillegg: 'Smusstillegg',
	kilometergodtgjoerelseBil: 'Kilometergodtgjørelse bil',
	fastBilgodtgjoerelse: 'Fast bilgodtgjørelse',
	reiseKost: 'Reise kost',
	reiseAnnet: 'Reise annet',
	besoeksreiserHjemmetAnnet: 'Besøksreiser hjemmet annet',
	besoeksreiserHjemmetKilometergodtgjoerelseBil: 'Besøksreiser hjemmet kilometergodtgjørelse bil',
	arbeidsoppholdKost: 'Arbeidsopphold kost',
	arbeidsoppholdLosji: 'Arbeidsopphold losji',
	reiseLosji: 'Reise losji',
	stipend: 'Stipend',
	friTransport: 'Fri transport',
	bedriftsbarnehageplass: 'Bedriftsbarnehageplass',
	tilskuddBarnehageplass: 'Tilskudd barnehageplass',
	reiseKostMedOvernatting: 'Reise kost med overnatting',
	reiseKostMedOvernattingPaaHotell: 'Reise kost med overnatting på hotell',
	reiseNattillegg: 'Reise nattillegg',
	reiseKostMedOvernattingPaaHotellBeordringUtover28Doegn:
		'Reise kost med overnatting på hotell beordring utover 28 døgn',
	reiseKostUtenOvernatting: 'Reise kost uten overnatting',
	administrativForpleining: 'Administrativ forpleining',
	reiseKostMedOvernattingPaaHybelBrakkePrivat: 'Reise kost med overnatting på hybel/brakke/privat',
	losjiEgenBrakkeCampingvogn: 'Losji egen brakke/campingvogn',
	reiseKostMedOvernattingTilLangtransportsjaafoerForKjoeringIUtlandet:
		'Reise kost med overnatting, langtransportsjåfør i utlandet',
	reiseKostMedOvernattingPaaPensjonat: 'Reise kost med overnatting på pensjonat',
	besoeksreiserHjemmetKost: 'Besoeksreiser hjemmet kost',
	kilometergodtgjoerelseElBil: 'Kilometergodtgjørelse el-bil',
	kilometergodtgjoerelsePassasjertillegg: 'Kilometergodtgjørelse passasjertillegg',
	kilometergodtgjoerelseAndreFremkomstmidler: 'Kilometergodtgjørelse andre remkomstmidler',
	flyttegodtgjoerelse: 'Flyttegodtgjørelse',
	godtgjoerelseSaeravtaleUtland: 'Godtgjørelse særavtale utland',
	overtidsmat: 'Overtidsmat',
	skattefriErstatning: 'Skattefri erstatning',
	loennUtenlandskArtist: 'Lønn utenlandsk artist',
	skattefrieUtbetalinger: 'Skattefrie utbetalinger',
	loennEtterDoedsfall: 'Lønn etter dødsfall',
	kapitalInntekt: 'Kapital inntekt',
	kompensasjonstilleggBolig: 'Kompensasjonstillegg bolig',
	beregnetSkatt: 'Beregnet skatt',
	innbetalingTilUtenlandskPensjonsordning: 'Innbetaling til utenlandsk pensjonsordning',
	yrkebilTjenestligbehovListepris: 'Yrkebil tjenestligbehov listepris',
	yrkebilTjenestligbehovKilometer: 'Yrkebil tjenestligbehov kilometer',
	loennTilVergeFraFylkesmannen: 'Lønn til verge fra fylkesmannen',
	trekkILoennForFerie: 'Trekk i lønn for ferie',
	betaltUtenlandskSkatt: 'Betalt utenlandsk skatt',
	honorarAkkordProsentProvisjon: 'Honorar/akkord/prosent/provisjon',
	skattepliktigPersonalrabatt: 'Skattepliktig personalrabatt',
	tips: 'Tips',
	reiseKostMedOvernattingTilLangtransportsjaafoerForKjoeringInnenlands:
		'Reise kost m/overnatting til langtransportsjåfør, kjøring innenlands',
	loennTilVergeFraStatsforvalteren: 'Lønn til verge fra statsforvalteren',
	reiseKostMedOvernattingPaaHybelMedKokEllerPrivat:
		'Reise kost m/overnatting på hybel med kok/privat',
	reiseKostMedOvernattingPaaHybelUtenKokEllerPensjonatEllerBrakke:
		'Reise kost m/overnatting på hybel uten kok/pensjonat/brakke',
	skattepliktigGodtgjoerelseSaeravtaleUtland: 'Skattepliktig godtgjørelse særavtale utland',

	// BESKRIVELSE YTELSE FRA OFFENTLIGE
	foreldrepenger: 'Foreldrepenger',
	venteloenn: 'Ventelønn',
	dagpengerTilFiskerSomBareHarHyre: 'Dagpenger til fisker som bare har hyre',
	sykepengerTilFiskerSomBareHarHyre: 'Sykepenger til fisker som bare har hyre',
	dagpengerVedArbeidsloeshet: 'Dagpenger ved arbeidsløshet',
	sykepenger: 'Sykepenger',
	underholdsbidragTilBarn: 'Underholdsbidrag til barn',
	skattefrieTilleggsstoenader: 'Skattefrie tilleggsstønader',
	arbeidsavklaringspenger: 'Arbeidsavklaringspenger',
	svangerskapspenger: 'Svangerskapspenger',
	skattefriStoenadTilBarnetilsyn: 'Skattefri stønad til barnetilsyn',
	overgangsstoenadTilEnsligMorEllerFarSomBegynteAaLoepe1April2014EllerSenere:
		'Overgangsstønad enslig mor eller far, fra 1. april 2014 eller senere',
	ufoeretrygd: 'Ufoeretrygd',
	ektefelletilleggUfoeretrygd: 'Ektefelletillegg uføretrygd',
	ufoereytelseEtteroppgjoer: 'Uføreytelse etteroppgjør',
	pleiepenger: 'Pleiepenger',
	opplaeringspenger: 'Opplæringspenger',
	omsorgspenger: 'Omsorgspenger',
	omstillingsstoenad: 'Omstillingsstønad',
	feriepengerForeldrepenger: 'Feriepenger foreldrepenger',
	ferietilleggDagpengerVedArbeidsloeshet: 'Ferietillegg dagpenger ved arbeidsløshet',
	ferietilleggDagpengerTilFiskerSomBareHarHyre:
		'Ferietillegg dagpenger til fisker som bare har hyre',
	feriepengerSykepenger: 'Feriepenger sykepenger',
	feriepengerSykepengerTilFiskerSomBareHarHyre:
		'Feriepenger sykepenger til fisker som bare har hyre',
	feriepengerSvangerskapspenger: 'Feriepenger svangerskapspenger',
	feriepengerPleiepenger: 'Feriepenger pleiepenger',
	feriepengerOpplaeringspenger: 'Feriepenger opplæringspenger',
	feriepengerOmsorgspenger: 'Feriepenger omsorgspenger',

	// BESKRIVELSE PENSJON ELLER TRYGD
	pensjonOgLivrenterIArbeidsforhold: 'Pensjon og livrenter i arbeidsforhold',
	etterloennOgEtterpensjon: 'Etterlønn og etterpensjon',
	pensjonIDoedsmaaneden: 'Pensjon i dødsmåneden',
	foederaad: 'Føderåd',
	alderspensjon: 'Alderspensjon',
	ufoerepensjon: 'Uførepensjon',
	ektefelletillegg: 'Ektefelletillegg',
	barnepensjon: 'Barnepensjon',
	overgangsstoenad: 'Overgangsstønad',
	avtalefestetPensjon: 'Avtalefestet pensjon',
	barnepensjonFraAndreEnnFolketrygden: 'Barnepensjon fra andre enn folketrygden',
	introduksjonsstoenad: 'Introduksjonsstønad',
	supplerendeStoenadTilPersonKortBotidNorge: 'Supplerende stønad til person med kort botid i Norge',
	kvalifiseringstoenad: 'Kvalifiseringstønad',
	nyeLivrenterIArbeidsforholdOgLivrenterFortsettelsesforsikringer:
		'Nye livrenter i arb.forhold og livrenter (fortsettelsesforsikringer)',
	krigspensjon: 'Krigspensjon',
	etterlattepensjon: 'Etterlattepensjon',
	nyAvtalefestetPensjonPrivatSektor: 'Ny avtalefestet pensjon privat sektor',
	ufoerepensjonFraAndreEnnFolketrygden: 'Uførepensjon fra andre enn folketrygden',
	underholdsbidragTilTidligereEktefelle: 'Underholdsbidrag til tidligere ektefelle',
	overgangsstoenadTilEnsligMorEllerFarSomBegynteAaLoepe31Mars2014EllerTidligere:
		'Overgangsstønad enslig mor eller far, fra 31. mars 2014 eller tidligere',
	overgangsstoenadTilGjenlevendeEktefelle: 'Overgangsstønad til gjenlevende ektefelle',
	ipaEllerIpsEngangsutbetaling: 'Ipa eller ips engangsutbetaling',
	ipaEllerIpsUfoerepensjon: 'Ipa eller ips uførepensjon',
	ipaEllerIpsBarnepensjon: 'Ipa eller ips barnepensjon',
	ipaEllerIpsPeriodiskeYtelser: 'Ipa eller ips periodiske ytelser',
	engangsutbetalingInnskuddspensjon: 'Engangsutbetaling innskuddspensjon',
	ufoereytelseEtteroppgjoerFraAndreEnnFolketrygden:
		'Uføreytelse etteroppgjør fra andre enn folketrygden',
	etterloenn: 'Etterlønn',
	slitertillegg: 'Slitertillegg',
	supplerendeStoenadTilUfoerFlyktning: 'Supplerende stønad til ufør flyktning',
	alderspensjonSkjermingstillegg: 'Alderspesnjon skjermingstillegg',
	barnepensjonFra2024: 'Barnepensjon fra 2024',
	barnepensjonFraAndreEnnFolketrygdenFra2024: 'Barnepensjon fra andre enn folketrygden fra 2024',
	ipaEllerIpsBarnepensjonFra2024: 'Ipa eller ips barnepensjon fra 2024',

	// BESKRIVELSE NÆRINGSINNTEKT
	vederlagDagmammaIEgetHjem: 'Vederlag til dagmamma i eget hjem',
	sykepengerTilDagmamma: 'Sykepenger til dagmamma',
	sykepengerTilJordOgSkogbrukere: 'Sykepenger til jord- og skogbrukere',
	vederlag: 'Vederlag',
	dagpengerTilFisker: 'Dagpenger til fisker',
	sykepengerTilFisker: 'Sykepenger til fisker',
	lottKunTrygdeavgift: 'Lott det skal beregnes trygdeavgift av',
	kompensasjonForTaptPersoninntekt: 'Kompensasjon for tapt personinntekt',
	foreldrepengerTilDagmamma: 'Foreldrepenger til dagmamma',
	foreldrepengerTilJordOgSkogbrukere: 'Foreldrepenger til jord- og skogbrukere',
	foreldrepengerTilFisker: 'Foreldrepenger til fisker',
	svangerskapspengerTilDagmamma: 'Svangerskapspenger til dagmamma',
	svangerskapspengerTilJordOgSkogbrukere: 'Svangerskapspenger til jord- og skogbrukere',
	svangerskapspengerTilFisker: 'Svangerskapspenger til fisker',
	pleiepengerTilDagmamma: 'Pleiepenger til dagmamma',
	pleiepengerTilJordOgSkogbrukere: 'Pleiepenger til jord- og skogbrukere',
	pleiepengerTilFisker: 'Pleiepenger til fisker',
	opplaeringspengerTilDagmamma: 'Opplæringspenger til dagmamma',
	opplaeringspengerTilJordOgSkogbrukere: 'Opplæringspenger til jord- og skogbrukere',
	opplaeringspengerTilFisker: 'Opplæringspenger til fisker',
	omsorgspengerTilDagmamma: 'Omsorgspenger til dagmamma',
	omsorgspengerTilJordOgSkogbrukere: 'Omsorgspenger til jord- og skogbrukere',
	omsorgspengerTilFisker: 'Omsorgspenger til fisker',

	// TILLEGGSINFORMASJON
	tilleggsinformasjon: 'Tilleggsinformasjon',
	tilleggsinformasjonstype: 'Tilleggsinformasjonstype',
	BilOgBaat: 'Bil og båt',
	bilOgBaat: 'Bil og båt',
	BonusFraForsvaret: 'Bonus fra forsvaret',
	bonusFraForsvaret: 'Bonus fra forsvaret',
	DagmammaIEgenBolig: 'Dagmamma i egen bolig',
	dagmammaIEgenBolig: 'Dagmamma i egen bolig',
	Periode: 'Etterbetalingsperiode',
	periode: 'Etterbetalingsperiode',
	etterbetalingsperiode: 'Etterbetalingsperiode',
	NorskKontinentalsokkel: 'Inntekt på norsk kontinentalsokkel',
	norskKontinentalsokkel: 'Inntekt på norsk kontinentalsokkel',
	Livrente: 'Livrente',
	livrente: 'Livrente',
	LottOgPartInnenFiske: 'Lott og part',
	lottOgPartInnenFiske: 'Lott og part',
	Nettoloennsordning: 'Nettolønn',
	nettoloennsordning: 'Nettolønn',
	AldersUfoereEtterlatteAvtalefestetOgKrigspensjon: 'Pensjon',
	aldersUfoereEtterlatteAvtalefestetOgKrigspensjon: 'Pensjon',
	pensjon: 'Pensjon',
	ReiseKostOgLosji: 'Reise kost og losji',
	reiseKostOgLosji: 'Reise kost og losji',
	SpesielleInntjeningsforhold: 'Spesielle inntjeningsforhold',
	spesielleInntjeningsforhold: 'Spesielle inntjeningsforhold',
	UtenlandskArtist: 'Utenlandsk artist',
	utenlandskArtist: 'Utenlandsk artist',

	// ÅR
	aaretUtbetalingenGjelderFor: 'Året utbetalingen gjelder for',

	// INNTJENINGSFORHOLD
	inntjeningsforhold: 'Inntjeningsforhold',
	loennVedKonkursEllerStatsgarantiOsv: 'Lønn ved konkurs eller statsgaranti, osv',
	utenlandskeSjoefolkSomIkkeErSkattepliktig: 'Utenlandske sjøfolk som ikke er skattepliktig',
	loennUtbetaltFraDenNorskeStatOpptjentIUtlandet:
		'Lønn utbetalt fra den norske stat opptjent i utlandet',
	loennVedArbeidsmarkedstiltak: 'Lønn ved arbeidsmarkedstiltak',
	hyreTilMannskapPaaFiskeSmaahvalfangstOgSelfangstfartoey:
		'Hyre til mannskap på fiske-, smaahvalfangst-, og selfangstfartøy',
	skattefriArbeidsinntektBarnUnderTrettenAar: 'Skattefri arbeidsinntekt barn under tretten år',
	loennOgAnnenGodtgjoerelseSomIkkeErSkattepliktig:
		'Lønn og annen godtgjørelse som ikke er skattepliktig',
	statsansattUtlandet: 'Statsansatt utlandet',

	// PERSONTYPE
	persontype: 'Persontype',
	sokkelarbeider: 'Sokkelarbeider',
	utlendingMedOppholdINorge: 'Utlending med opphold i Norge',
	norskPendler: 'Norsk pendler',
	utenlandskPendler: 'Utenlandsk pendler',
	utenlandskPendlerMedSkattekortUtenStandardFradrag:
		'Utenlandsk pendler med skattekort uten standard fradrag',

	// PERIODER
	etterbetalingsperiodeStart: 'Etterbetalingsperiode start',
	etterbetalingsperiodeSlutt: 'Etterbetalingsperiode slutt',
	pensjonTidsromStart: 'Pensjon tidsrom start',
	pensjonTidsromSlutt: 'Pensjon tidsrom slutt',

	// ANNET
	antall: 'Antall',
	grunnpensjonsbeloep: 'Grunnpensjonsbeløp',
	tilleggspensjonsbeloep: 'Tilleggspensjonsbeløp',
	ufoeregrad: 'Uføregrad',
	pensjonsgrad: 'Pensjonsgrad',
	heravEtterlattepensjon: 'Herav etterlattepensjon',

	//FELLES
	beloep: 'Beløp',
	startOpptjeningsperiode: 'Start opptjeningsperiode',
	sluttOpptjeningsperiode: 'Slutt opptjeningsperiode',
}

export default (key) => (texts[key] ? texts[key] : key)
