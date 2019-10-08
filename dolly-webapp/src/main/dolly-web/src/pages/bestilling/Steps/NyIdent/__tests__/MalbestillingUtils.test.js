import { getAttributesFromMal, getValuesFromMal } from '../MalbestillingUtils'

describe('MalbestillingUtils.js', () => {
	describe('getAttributesFromMal()', () => {
		// KUN TEST PÅ NOE TPSF, UDI, PDL, INNTEKT. MANGLER INST, AAREG, KRR, ARENA
		const tpsfKriterier = '{"regdato": "2019-09-19T06:20:01.97", "identtype": "FNR"}'

		const mal = {
			tpsfKriterier: tpsfKriterier,
			bestKriterier: '{}'
		}

		it('skal returnere ingen attributt', () => {
			expect(getAttributesFromMal(mal)).toEqual([])
		})

		const udiKriterier =
			'"udistub":{' +
			'"avgjoerelser":null,' +
			'"aliaser":[{"nyIdent":true,"identtype":"FNR"}],' +
			'"arbeidsadgang": {"arbeidsOmfang":null, "harArbeidsAdgang": "NEI", "periode":null, "typeArbeidsAdgang":null},' +
			'"oppholdStatus":{' +
			'"eosEllerEFTABeslutningOmOppholdsrett":"FAMILIE", "eosEllerEFTABeslutningOmOppholdsrettEffektuering": "2019-09-19T06:20:01.97", "eosEllerEFTABeslutningOmOppholdsrettPeriode": {"fra":"2019-09-03","til":"2019-09-04"},' +
			'"eosEllerEFTAOppholdstillatelse":null, "eosEllerEFTAOppholdstillatelseEffektuering": null, "eosEllerEFTAOppholdstillatelsePeriode": null,' +
			'"eosEllerEFTAVedtakOmVarigOppholdsrett":null, "eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering": null, "eosEllerEFTAVedtakOmVarigOppholdsrettPeriode": null,' +
			'"ikkeOppholdstilatelseIkkeVilkaarIkkeVisum": null,"udiOppholdSammeVilkaar": null,"uavklart": null},' +
			'"soeknadOmBeskyttelseUnderBehandling": "UAVKLART","flyktning": true,"harOppholdsTillatelse": null,' +
			'"avgjoerelseUavklart":null}'

		const malUdi = {
			tpsfKriterier: tpsfKriterier,
			bestKriterier: '{' + udiKriterier + '}'
		}

		it('skal returnere 5 checked UDI attributt ', () => {
			expect(getAttributesFromMal(malUdi).length).toEqual(5)
		})

		const pdlKriterier =
			'"pdlforvalter":{' +
			'"kontaktinformasjonForDoedsbo":{"adresselinje1":"Solveien 5","adressat":{"adressatType":"PERSON_MEDID","idnummer":"12345678954"},"gyldigFom":"2019-09-03T00:00:00","landkode":"NOR","postnummer":"7318","poststedsnavn":"AGDENES","skifteform":"OFFENTLIG","utstedtDato":"2019-09-03T00:00:00"},' +
			'"utenlandskIdentifikasjonsnummer":{"identifikasjonsnummer":"87854453333333333","kilde":"123","opphoert":false,"utstederland":"ALB"},' +
			'"falskIdentitet":{"rettIdentitet":{"identitetType":"ENTYDIG","rettIdentitetVedIdentifikasjonsnummer":"12345678987"}}}'

		const malPdlf = {
			tpsfKriterier: tpsfKriterier,
			bestKriterier: '{' + pdlKriterier + '}'
		}
		it('skal returnere 3 checked PDLF attributt ', () => {
			expect(getAttributesFromMal(malPdlf).length).toEqual(3)
		})

		const inntektKriterier =
			'"sigrunstub":[{"testdataEier":null,"personidentifikator":null,"inntektsaar":"2030","tjeneste":"Beregnet skatt","grunnlag":[{"tekniskNavn":"antallMaanederPensjon","verdi":"65132"}]}]'
		const malInntekt = {
			tpsfKriterier: tpsfKriterier,
			bestKriterier: '{' + inntektKriterier + '}'
		}
		it('skal returnere 1 checked INNTEKT attributt ', () => {
			expect(getAttributesFromMal(malInntekt).length).toEqual(1)
		})

		const innvandretUtvandretFamilieKriterier =
			'{"regdato":"2019-08-29T12:19:56.916","utvandretTilLand":"AND","utvandretTilLandFlyttedato":"2019-08-02T00:00:00","innvandretFraLand":"DZA","innvandretFraLandFlyttedato":"2019-08-01T00:00:00","relasjoner":{"partner":{"identtype":"FNR","kjonn":null,"foedtEtter":null,"foedtFoer":null,"sprakKode":null,"datoSprak":null,"spesreg":null,"spesregDato":null,"statsborgerskap":null,"statsborgerskapRegdato":null,"egenAnsattDatoFom":null,"egenAnsattDatoTom":null,"utenFastBopel":null,"boadresse":null,"postadresse":null,"utvandretTilLand":"ALB","utvandretTilLandFlyttedato":"2019-08-04T00:00:00","harMellomnavn":null,"innvandretFraLand":"AIA","innvandretFraLandFlyttedato":"2019-08-03T00:00:00","erForsvunnet":null,"forsvunnetDato":null},"barn":[{"identtype":"FNR","kjonn":null,"foedtEtter":null,"foedtFoer":null,"sprakKode":null,"datoSprak":null,"spesreg":null,"spesregDato":null,"statsborgerskap":null,"statsborgerskapRegdato":null,"egenAnsattDatoFom":null,"egenAnsattDatoTom":null,"utenFastBopel":null,"boadresse":null,"postadresse":null,"utvandretTilLand":"BLZ","utvandretTilLandFlyttedato":"2019-08-06T00:00:00","harMellomnavn":null,"innvandretFraLand":"AIA","innvandretFraLandFlyttedato":"2019-08-05T00:00:00","erForsvunnet":null,"forsvunnetDato":null},{"identtype":"FNR","kjonn":null,"foedtEtter":null,"foedtFoer":null,"sprakKode":null,"datoSprak":null,"spesreg":null,"spesregDato":null,"statsborgerskap":null,"statsborgerskapRegdato":null,"egenAnsattDatoFom":null,"egenAnsattDatoTom":null,"utenFastBopel":null,"boadresse":null,"postadresse":null,"utvandretTilLand":"BRA","utvandretTilLandFlyttedato":"2019-08-08T00:00:00","harMellomnavn":null,"innvandretFraLand":"BIH","innvandretFraLandFlyttedato":"2019-08-07T00:00:00","erForsvunnet":null,"forsvunnetDato":null}]},"identtype":"FNR"}'
		const malInnvandretUtvandretFamilie = {
			tpsfKriterier: innvandretUtvandretFamilieKriterier,
			bestKriterier: '{}'
		}
		it('skal returnere 4 checked innvandret, utvandret, partner, barn attributt ', () => {
			expect(getAttributesFromMal(malInnvandretUtvandretFamilie).length).toEqual(4)
		})
	})

	describe('getValuesFromMal()', () => {
		//Kun test på PDL. Mangler TPS-data, INST, AAREG, SIGRUN, KRR, ARENA, UDI
		const pdlresultat = {
			falskIdentitet: [
				{
					identitetType: 'ENTYDIG',
					rettIdentitetVedIdentifikasjonsnummer: '12345678987'
				}
			],
			kontaktinformasjonForDoedsbo: [
				{
					adressatType: 'PERSON_MEDID',
					adresselinje1: 'Solveien 5',
					gyldigFom: '03.09.2019',
					idnummer: '12345678954',
					landkode: 'NOR',
					postnummer: '7318',
					poststedsnavn: 'AGDENES',
					skifteform: 'OFFENTLIG',
					utstedtDato: '03.09.2019'
				}
			],
			utenlandskIdentifikasjonsnummer: [
				{
					identifikasjonsnummer: '87854453333333333',
					kilde: '123',
					opphoert: false,
					utstederland: 'ALB'
				}
			]
		}

		const pdlKriterier =
			'"pdlforvalter":{' +
			'"kontaktinformasjonForDoedsbo":{"adresselinje1":"Solveien 5","adressat":{"adressatType":"PERSON_MEDID","idnummer":"12345678954"},"gyldigFom":"2019-09-03T00:00:00","landkode":"NOR","postnummer":"7318","poststedsnavn":"AGDENES","skifteform":"OFFENTLIG","utstedtDato":"2019-09-03T00:00:00"},' +
			'"utenlandskIdentifikasjonsnummer":{"identifikasjonsnummer":"87854453333333333","kilde":"123","opphoert":false,"utstederland":"ALB"},' +
			'"falskIdentitet":{"rettIdentitet":{"identitetType":"ENTYDIG","rettIdentitetVedIdentifikasjonsnummer":"12345678987"}}}'

		const malPdlf = {
			tpsfKriterier: '{}',
			bestKriterier: '{' + pdlKriterier + '}'
		}

		it('skal returnere 3 checked PDLF attributt ', () => {
			expect(getValuesFromMal(malPdlf)).toMatchObject(pdlresultat)
		})
	})
})
