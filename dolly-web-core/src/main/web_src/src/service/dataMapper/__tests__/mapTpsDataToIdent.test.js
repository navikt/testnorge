import { mapTpsfData } from '../mapTpsDataToIdent'

describe('mapTpsDataToIdent.js', () => {
	describe('mapTpsfData', () => {
		const testTpsfData = {
			identtype: 'FNR',
			ident: '010101456789',
			fornavn: 'OLA',
			mellomnavn: 'MELLOMNAVN',
			etternavn: 'NORDMANN',
			gtRegel: 'A',
			gtType: 'BYDEL',
			gtVerdi: '030103',
			kjonn: 'MENN',
			alder: 20,
			personStatus: 'UTVA',
			sivilstand: 'ENKE',
			spesreg: 'KODE6',
			utenFastBopel: 'JA',
			relasjoner: [],
			tknr: '0314',
			egenAnsattDatoFom: '2019-04-10T12:55:14.896',
			sprakKode: 'English',
			statsborgerskap: 'NOR',
			utvandretTilLand: 'AFG',
			utvandretTilLandFlyttedato: '2019-08-06T00:00:00',
			innvandretFraLand: 'BWA',
			innvandretFraLandFlyttedato: '2019-08-05T00:00:00'
		}

		const testPdlfData = {
			utenlandskeIdentifikasjonsnummere: {
				0: {
					idNummer: '1234567890',
					kilde: 'Dolly',
					registrertOpphoertINAV: '2019-06-25',
					utstederland: 'JAPAN'
				}
			}
		}

		const testTpsfRes = [
			{
				header: 'Persondetaljer',
				data: [
					{
						id: 'ident',
						label: 'FNR',
						value: '010101456789'
					},
					{
						id: 'fornavn',
						label: 'Fornavn',
						value: 'OLA'
					},
					{
						id: 'mellomnavn',
						label: 'Mellomnavn',
						value: 'MELLOMNAVN'
					},
					{
						id: 'etternavn',
						label: 'Etternavn',
						value: 'NORDMANN'
					},
					{
						id: 'kjonn',
						label: 'Kjønn',
						value: 'MENN'
					},
					{
						id: 'alder',
						label: 'Alder',
						value: '20'
					},
					{
						id: 'personStatus',
						label: 'Personstatus',
						value: 'UTVA',
						apiKodeverkId: 'Personstatuser'
					},
					{
						id: 'sivilstand',
						label: 'Sivilstand',
						value: 'ENKE'
					},
					{
						id: 'miljoer',
						label: 'Miljøer',
						value: 't0, t1'
					},
					{
						id: 'spesreg',
						label: 'Diskresjonskoder',
						value: 'KODE6'
					},
					{
						id: 'utenFastBopel',
						label: 'Uten fast bopel',
						value: 'JA'
					},
					{
						apiKodeverkId: 'Bydeler',
						extraLabel: 'Bydel',
						id: 'gtVerdi',
						label: 'Geo. Tilhør',
						value: '030103'
					},
					{
						id: 'tknr',
						label: 'TK nummer',
						tknr: '0314'
					},
					{
						id: 'egenAnsattDatoFom',
						label: 'Egenansatt',
						value: 'JA'
					}
				]
			}
		]

		const testNasjonalitetRes = [
			...testTpsfRes,
			{
				header: 'Nasjonalitet',
				data: [
					{
						id: 'statsborgerskap',
						label: 'Statsborgerskap',
						value: 'NOR'
					},
					{
						id: 'sprakKode',
						label: 'Språk',
						value: 'English'
					},
					{
						id: 'innvandretFraLand',
						label: 'Innvandret fra land',
						value: undefined,
						apiKodeverkId: 'Landkoder'
					},
					{
						id: 'innvandretFraLandFlyttedato',
						label: 'Innvandret dato',
						value: undefined
					},
					{
						apiKodeverkId: 'Landkoder',
						id: 'utvandretTilLand',
						label: 'Utvandret til land',
						value: 'AFG'
					},
					{
						id: 'utvandretTilLandFlyttedato',
						label: 'Utvandret dato',
						value: '06.08.2019'
					}
				]
			}
		]

		const testPdlfRes = [
			...testNasjonalitetRes,
			{
				header: 'Utenlands-ID',
				data: [
					{
						id: 'idNummer',
						label: 'Identifikasjonsnummer',
						value: '1234567890'
					},
					{
						id: 'kilde',
						label: 'Kilde',
						value: 'Dolly'
					},
					{
						id: 'opphoert',
						label: 'Opphørt',
						value: 'Ja'
					},
					{
						id: 'utstederland',
						label: 'Utstederland',
						value: 'JAPAN',
						apiKodeverkId: 'Landkoder'
					}
				]
			}
		]

		const testIdent = {
			ident: '123456789'
		}

		const tpsfKriterier = {}

		it('should return null without data', () => {
			expect(mapTpsfData()).toBeNull()
		})

		it('should return tpsf-data, med undefined innvandretinfo', () => {
			expect(mapTpsfData(testTpsfData, testIdent, tpsfKriterier)).toEqual(testNasjonalitetRes)
		})

		it('should return tpsf-data with matrikkeladresse', () => {
			const testTpsfDataAdresseValues = {
				...testTpsfData,
				boadresse: {
					adressetype: 'MATR',
					postnr: '2603',
					kommunenr: '0501',
					mellomnavn: 'Min gård',
					gardsnr: '658',
					bruksnr: '745',
					festenr: '5684',
					undernr: '11',
					flyttedato: '1903-11-03T00:00:00'
				}
			}

			const res = [
				...testNasjonalitetRes,
				{
					header: 'Bostedadresse',
					data: [
						{
							parent: 'boadresse',
							id: 'adressetype',
							label: 'Adressetype',
							value: 'Matrikkeladresse'
						},
						{
							parent: 'boadresse',
							id: 'gateadresse',
							label: 'Gatenavn',
							value: undefined
						},
						{
							parent: 'boadresse',
							id: 'husnummer',
							label: 'Husnummer',
							value: undefined
						},
						{
							parent: 'boadresse',
							id: 'mellomnavn',
							label: 'Stedsnavn',
							value: 'Min gård'
						},
						{
							parent: 'boadresse',
							id: 'gardsnr',
							label: 'Gårdsnummer',
							value: '658'
						},
						{
							parent: 'boadresse',
							id: 'bruksnr',
							label: 'Bruksnummer',
							value: '745'
						},
						{
							parent: 'boadresse',
							id: 'festenr',
							label: 'Festenummer',
							value: '5684'
						},
						{
							parent: 'boadresse',
							id: 'undernr',
							label: 'Undernummer',
							value: '11'
						},
						{
							parent: 'boadresse',
							id: 'postnr',
							label: 'Postnummer',
							extraLabel: '2603',
							apiKodeverkId: 'Postnummer',
							value: '2603'
						},
						{
							parent: 'boadresse',
							id: 'flyttedato',
							label: 'Flyttedato',
							value: '03.11.1903'
						}
					]
				}
			]
			expect(mapTpsfData(testTpsfDataAdresseValues, testIdent, tpsfKriterier)).toEqual(res)
		})

		it('should return tpsf-data and pdlf-data', () => {
			expect(mapTpsfData(testTpsfData, testIdent, tpsfKriterier, testPdlfData)).toEqual(testPdlfRes)
		})
	})
})
