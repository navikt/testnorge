// const firstDataSet = {
// 	inngaarIGrunnlagForTrekk: [true, false],
// 	utloeserArbeidsgiveravgift: [true, false],
// 	fordel: ['naturalytelse', 'kontantytelse', 'utgiftsgodtgjoerelse'],
// 	skatteOgAvgiftsregel: [
// 		'<TOM>',
// 		'svalbard',
// 		'janMayenOgBilandene',
// 		'saerskiltFradragForSjoefolk',
// 		'veldedigEllerAllmennyttigOrganisasjon',
// 		'dagmammaIBarnetsHjem',
// 		'loennsarbeidIHjemmet',
// 		'nettoloenn',
// 		'nettoloennForSjoefolk',
// 		'skattefriOrganisasjon'
// 	],
// 	beskrivelse: [
// 		'kostDoegn',
// 		'losji',
// 		'fastloenn',
// 		'timeloenn',
// 		'fastTillegg',
// 		'uregelmessigeTilleggKnyttetTilArbeidetTid',
// 		'helligdagstillegg',
// 		'uregelmessigeTilleggKnyttetTilIkkeArbeidetTid',
// 		'bonus',
// 		'overtidsgodtgjoerelse',
// 		'styrehonorarOgGodtgjoerelseVerv',
// 		'kommunalOmsorgsloennOgFosterhjemsgodtgjoerelse',
// 		'sluttvederlag',
// 		'feriepenger',
// 		'annet',
// 		'ikkeSkattepliktigLoennFraUtenlandskDiplomKonsulStasjon',
// 		'skattepliktigDelForsikringer',
// 		'hyretillegg',
// 		'bil',
// 		'kostDager',
// 		'rentefordelLaan',
// 		'bolig',
// 		'fondForIdrettsutoevere',
// 		'bonusFraForsvaret',
// 		'elektroniskKommunikasjon',
// 		'opsjoner',
// 		'aksjerGrunnfondsbevisTilUnderkurs',
// 		'loennForBarnepassIBarnetsHjem',
// 		'loennUtbetaltAvVeldedigEllerAllmennyttigInstitusjonEllerOrganisasjon',
// 		'loennTilPrivatpersonerForArbeidIHjemmet',
// 		'kostbesparelseIHjemmet',
// 		'smusstillegg',
// 		'kilometergodtgjoerelseBil',
// 		'fastBilgodtgjoerelse',
// 		'reiseKost',
// 		'reiseAnnet',
// 		'besoeksreiserHjemmetAnnet',
// 		'besoeksreiserHjemmetKilometergodtgjoerelseBil',
// 		'arbeidsoppholdKost',
// 		'arbeidsoppholdLosji',
// 		'reiseLosji',
// 		'stipend',
// 		'friTransport',
// 		'bedriftsbarnehageplass',
// 		'tilskuddBarnehageplass',
// 		'reiseKostMedOvernatting',
// 		'reiseKostMedOvernattingPaaHotell',
// 		'reiseNattillegg',
// 		'reiseKostMedOvernattingPaaHotellBeordringUtover28Doegn',
// 		'reiseKostUtenOvernatting',
// 		'administrativForpleining',
// 		'reiseKostMedOvernattingPaaHybelBrakkePrivat',
// 		'losjiEgenBrakkeCampingvogn',
// 		'reiseKostMedOvernattingTilLangtransportsjaafoerForKjoeringIUtlandet',
// 		'reiseKostMedOvernattingPaaPensjonat',
// 		'besoeksreiserHjemmetKost',
// 		'kilometergodtgjoerelseElBil',
// 		'kilometergodtgjoerelsePassasjertillegg',
// 		'kilometergodtgjoerelseAndreFremkomstmidler',
// 		'flyttegodtgjoerelse',
// 		'godtgjoerelseSaeravtaleUtland',
// 		'overtidsmat',
// 		'skattefriErstatning',
// 		'loennUtenlandskArtist',
// 		'skattefrieUtbetalinger',
// 		'loennEtterDoedsfall',
// 		'kapitalInntekt',
// 		'kompensasjonstilleggBolig',
// 		'beregnetSkatt',
// 		'innbetalingTilUtenlandskPensjonsordning',
// 		'yrkebilTjenestligbehovListepris',
// 		'yrkebilTjenestligbehovKilometer',
// 		'loennTilVergeFraFylkesmannen',
// 		'trekkILoennForFerie',
// 		'betaltUtenlandskSkatt',
// 		'honorarAkkordProsentProvisjon',
// 		'skattepliktigPersonalrabatt',
// 		'tips'
// 	],
// 	antall: ['<UTFYLT>', '<TOM>'],
// 	skattemessigBosattILand: ['<TOM>', '<UTFYLT>'],
// 	opptjeningsland: ['<TOM>', '<UTFYLT>'],
// 	tilleggsinformasjonstype: [
// 		'<TOM>',
// 		'BilOgBaat',
// 		'ReiseKostOgLosji',
// 		'SpesielleInntjeningsforhold',
// 		'NorskKontinentalsokkel',
// 		'BonusFraForsvaret',
// 		'UtenlandskArtist',
// 		'Nettoloennsordning'
// 	],
// 	aaretUtbetalingenGjelderFor: ['<TOM>', '<UTFYLT>'],
// 	inntjeningsforhold: [
// 		'<TOM>',
// 		'loennVedKonkursEllerStatsgarantiOsv',
// 		'utenlandskeSjoefolkSomIkkeErSkattepliktig',
// 		'loennUtbetaltFraDenNorskeStatOpptjentIUtlandet',
// 		'loennVedArbeidsmarkedstiltak',
// 		'hyreTilMannskapPaaFiskeSmaahvalfangstOgSelfangstfartoey',
// 		'skattefriArbeidsinntektBarnUnderTrettenAar',
// 		'loennOgAnnenGodtgjoerelseSomIkkeErSkattepliktig',
// 		'statsansattUtlandet'
// 	],
// 	persontype: [
// 		'<TOM>',
// 		'sokkelarbeider',
// 		'utlendingMedOppholdINorge',
// 		'norskPendler',
// 		'utenlandskPendler',
// 		'utenlandskPendlerMedSkattekortUtenStandardFradrag'
// 	]
// }

// const secondData = {
// 	inngaarIGrunnlagForTrekk: [true],
// 	utloeserArbeidsgiveravgift: [true, false],
// 	fordel: ['naturalytelse', 'kontantytelse', 'utgiftsgodtgjoerelse'],
// 	skatteOgAvgiftsregel: [
// 		'<TOM>',
// 		'svalbard',
// 		'janMayenOgBilandene',
// 		'saerskiltFradragForSjoefolk',
// 		'veldedigEllerAllmennyttigOrganisasjon',
// 		'dagmammaIBarnetsHjem',
// 		'loennsarbeidIHjemmet',
// 		'nettoloenn',
// 		'nettoloennForSjoefolk',
// 		'skattefriOrganisasjon'
// 	],
// 	beskrivelse: [
// 		'kostDoegn',
// 		'losji',
// 		'fastloenn',
// 		'timeloenn',
// 		'fastTillegg',
// 		'uregelmessigeTilleggKnyttetTilArbeidetTid',
// 		'helligdagstillegg',
// 		'uregelmessigeTilleggKnyttetTilIkkeArbeidetTid',
// 		'bonus',
// 		'overtidsgodtgjoerelse',
// 		'styrehonorarOgGodtgjoerelseVerv',
// 		'kommunalOmsorgsloennOgFosterhjemsgodtgjoerelse',
// 		'sluttvederlag',
// 		'feriepenger',
// 		'annet',
// 		'skattepliktigDelForsikringer',
// 		'hyretillegg',
// 		'bil',
// 		'kostDager',
// 		'rentefordelLaan',
// 		'bolig',
// 		'fondForIdrettsutoevere',
// 		'bonusFraForsvaret',
// 		'elektroniskKommunikasjon',
// 		'opsjoner',
// 		'aksjerGrunnfondsbevisTilUnderkurs',
// 		'loennForBarnepassIBarnetsHjem',
// 		'loennUtbetaltAvVeldedigEllerAllmennyttigInstitusjonEllerOrganisasjon',
// 		'loennTilPrivatpersonerForArbeidIHjemmet',
// 		'kostbesparelseIHjemmet',
// 		'smusstillegg',
// 		'kilometergodtgjoerelseBil',
// 		'fastBilgodtgjoerelse',
// 		'reiseKost',
// 		'reiseAnnet',
// 		'besoeksreiserHjemmetAnnet',
// 		'besoeksreiserHjemmetKilometergodtgjoerelseBil',
// 		'arbeidsoppholdKost',
// 		'arbeidsoppholdLosji',
// 		'reiseLosji',
// 		'stipend',
// 		'kompensasjonstilleggBolig',
// 		'beregnetSkatt',
// 		'yrkebilTjenestligbehovListepris',
// 		'yrkebilTjenestligbehovKilometer',
// 		'loennTilVergeFraFylkesmannen',
// 		'trekkILoennForFerie',
// 		'innbetalingTilUtenlandskPensjonsordning',
// 		'betaltUtenlandskSkatt',
// 		'honorarAkkordProsentProvisjon',
// 		'skattepliktigPersonalrabatt',
// 		'tips'
// 	],
// 	antall: ['<UTFYLT>', '<TOM>'],
// 	skattemessigBosattILand: ['<TOM>', '<UTFYLT>'],
// 	opptjeningsland: ['<TOM>', '<UTFYLT>'],
// 	tilleggsinformasjonstype: [
// 		'<TOM>',
// 		'BilOgBaat',
// 		'ReiseKostOgLosji',
// 		'SpesielleInntjeningsforhold',
// 		'NorskKontinentalsokkel',
// 		'BonusFraForsvaret',
// 		'Nettoloennsordning'
// 	],
// 	aaretUtbetalingenGjelderFor: ['<TOM>', '<UTFYLT>'],
// 	inntjeningsforhold: [
// 		'<TOM>',
// 		'loennVedKonkursEllerStatsgarantiOsv',
// 		'utenlandskeSjoefolkSomIkkeErSkattepliktig',
// 		'loennUtbetaltFraDenNorskeStatOpptjentIUtlandet',
// 		'loennVedArbeidsmarkedstiltak',
// 		'hyreTilMannskapPaaFiskeSmaahvalfangstOgSelfangstfartoey',
// 		'loennOgAnnenGodtgjoerelseSomIkkeErSkattepliktig',
// 		'statsansattUtlandet'
// 	],
// 	persontype: ['<TOM>', 'sokkelarbeider', 'utlendingMedOppholdINorge']
// }

// const thirdData = {
// 	inngaarIGrunnlagForTrekk: [true],
// 	utloeserArbeidsgiveravgift: [true, false],
// 	fordel: ['kontantytelse'],
// 	skatteOgAvgiftsregel: [
// 		'<TOM>',
// 		'saerskiltFradragForSjoefolk',
// 		'svalbard',
// 		'janMayenOgBilandene',
// 		'veldedigEllerAllmennyttigOrganisasjon',
// 		'dagmammaIBarnetsHjem',
// 		'loennsarbeidIHjemmet',
// 		'skattefriOrganisasjon'
// 	],
// 	beskrivelse: ['fastTillegg'],
// 	antall: ['<TOM>'],
// 	skattemessigBosattILand: ['<TOM>', '<UTFYLT>'],
// 	opptjeningsland: ['<TOM>', '<UTFYLT>'],
// 	tilleggsinformasjonstype: ['<TOM>', 'SpesielleInntjeningsforhold', 'NorskKontinentalsokkel'],
// 	aaretUtbetalingenGjelderFor: ['<TOM>'],
// 	inntjeningsforhold: [
// 		'<TOM>',
// 		'loennVedKonkursEllerStatsgarantiOsv',
// 		'utenlandskeSjoefolkSomIkkeErSkattepliktig',
// 		'loennUtbetaltFraDenNorskeStatOpptjentIUtlandet',
// 		'loennVedArbeidsmarkedstiltak',
// 		'hyreTilMannskapPaaFiskeSmaahvalfangstOgSelfangstfartoey',
// 		'loennOgAnnenGodtgjoerelseSomIkkeErSkattepliktig'
// 	],
// 	persontype: ['<TOM>']
// }

export const validate = values =>
	window
		.fetch('https://dolly-t2.nais.preprod.local/api/v1/inntektstub', {
			method: 'POST',
			mode: 'cors',
			credentials: 'include',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(values)
		})
		.then(response => response.json())
		.catch(error => {
			console.error('error :', error)
			return error
		})

// new Promise((reslove, reject) => {
// 	if (values['inngaarIGrunnlagForTrekk'] === true) {
// 		if (values['beskrivelse'] === 'fastTillegg') {
// 			return setTimeout(() => reslove(thirdData), 25)
// 		}

// 		return setTimeout(() => reslove(secondData), 25)
// 	}

// 	return setTimeout(() => reslove(firstDataSet), 25)
// })
