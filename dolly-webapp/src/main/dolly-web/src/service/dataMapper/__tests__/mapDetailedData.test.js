import { mapTpsfData, mapSigrunData, mapKrrData } from '../mapDetailedData'

describe('mapDetailedData.js', () => {
	describe('mapTpsfData', () => {
		const testTpsfData = {
			identtype: 'FNR',
			ident: '010101456789',
			fornavn: 'OLA',
			mellomnavn: 'MELLOMNAVN',
			etternavn: 'NORDMANN',
			kjonn: 'MENN',
			alder: 20,
			sivilstand: 'ENKE',
			spesreg: 'KODE6',
			relasjoner: []
		}

		const testTpsfRes = [
			{
				header: 'Personlig informasjon',
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
						id: 'sivilstand',
						label: 'Sivilstand',
						value: 'ENKE'
					},
					{
						id: 'miljoer',
						label: 'Miljøer',
						value: 'T0, T1'
					},
					{
						id: 'spesreg',
						label: 'Diskresjonskoder',
						value: 'KODE6'
					}
				]
			}
		]

		const bestillingData = {
			environments: ['t0', 't1']
		}

		it('should return null without data', () => {
			expect(mapTpsfData()).toBeNull()
		})

		it('should return tpsf-data', () => {
			expect(mapTpsfData(testTpsfData, bestillingData)).toEqual(testTpsfRes)
		})

		it('should return tpsf-data with alle values', () => {
			const testTpsfDataAllValues = {
				...testTpsfData,
				statsborgerskap: 'NOR',
				innvandretFra: 'VIE',
				boadresse: {
					gateadresse: 'SANNERGATA',
					husnummer: 'H0101',
					gatekode: '2',
					postnr: '1234',
					flyttedato: '1903-11-03T00:00:00'
				},
				relasjoner: [
					{
						id: '1',
						relasjonTypeNavn: 'EKTEFELLE',
						personRelasjonMed: {
							identtype: 'FNR',
							ident: '101010456789',
							fornavn: 'LISA',
							mellomnavn: 'MELLOMNAVN',
							etternavn: 'BERG',
							kjonn: 'KVINNE'
						}
					}
				]
			}

			const res = [
				...testTpsfRes,
				{
					header: 'Nasjonalitet',
					data: [
						{
							id: 'innvandretFra',
							label: 'Innvandret fra',
							value: 'VIE'
						},
						{
							id: 'statsborgerskap',
							label: 'Statsborgerskap',
							value: 'NOR'
						}
					]
				},
				{
					header: 'Bostedadresse',
					data: [
						{
							parent: 'boadresse',
							id: 'gateadresse',
							label: 'Gatenavn',
							value: 'SANNERGATA'
						},
						{
							parent: 'boadresse',
							id: 'husnummer',
							label: 'Husnummer',
							value: 'H0101'
						},
						{
							parent: 'boadresse',
							id: 'gatekode',
							label: 'Gatekode',
							value: '2'
						},
						{
							parent: 'boadresse',
							id: 'postnr',
							label: 'Postnummer',
							value: '1234'
						},
						{
							parent: 'boadresse',
							id: 'flyttedato',
							label: 'Flyttedato',
							value: '03.11.1903'
						}
					]
				},
				{
					header: 'Familierelasjoner',
					multiple: true,
					data: testTpsfDataAllValues.relasjoner.map(relasjon => {
						return {
							parent: 'relasjoner',
							id: '1',
							label: 'Partner',
							value: [
								{
									id: 'ident',
									label: 'FNR',
									value: '101010456789'
								},
								{
									id: 'fornavn',
									label: 'Fornavn',
									value: 'LISA'
								},
								{
									id: 'mellomnavn',
									label: 'Mellomnavn',
									value: 'MELLOMNAVN'
								},
								{
									id: 'etternavn',
									label: 'Etternavn',
									value: 'BERG'
								},
								{
									id: 'kjonn',
									label: 'Kjønn',
									value: 'KVINNE'
								}
							]
						}
					})
				}
			]
			expect(mapTpsfData(testTpsfDataAllValues, bestillingData)).toEqual(res)
		})
	})

	describe('mapSigrunData', () => {
		it('should return null without data', () => {
			expect(mapSigrunData()).toBeNull()
		})

		it('should return sigrun-data ', () => {
			const testSigrunData = [
				{
					personidentifikator: '010101987654',
					inntektsaar: '2019',
					tjeneste: 'testTjeneste',
					grunnlag: 'testGrunnlag',
					verdi: '999'
				}
			]
			const res = {
				header: 'Inntekter',
				multiple: true,
				data: testSigrunData.map(data => {
					return {
						parent: 'inntekter',
						id: data.personidentifikator,
						label: data.inntektsaar,
						value: [
							{
								id: 'aar',
								label: 'År',
								value: '2019'
							},
							{
								id: 'verdi',
								label: 'Beløp',
								value: '999'
							},
							,
							{
								id: 'tjeneste',
								label: 'Tjeneste',
								width: 'medium',
								value: 'testTjeneste'
							},

							{
								id: 'grunnlag',
								label: 'Grunnlag',
								width: 'xlarge',
								value: 'Test Grunnlag'
							}
						]
					}
				})
			}

			expect(mapSigrunData(testSigrunData)).toEqual(res)
		})
	})

	describe('mapKrrData', () => {
		it('should return null without data', () => {
			expect(mapKrrData()).toBeNull()
		})

		const testKrrData = { mobil: '987654312', epost: 'nav@nav.no', reservert: true }

		it('should return krr-data with reservert', () => {
			const testRes = {
				header: 'Kontaktinformasjon og reservasjon',
				data: [
					{
						id: 'mobil',
						label: 'Mobilnummer',
						value: '987654312'
					},
					{
						id: 'epost',
						label: 'Epost',
						value: 'nav@nav.no'
					},
					{
						id: 'reservert',
						label: 'Reservert mot digitalkommunikasjon',
						value: 'JA'
					}
				]
			}
			expect(mapKrrData(testKrrData)).toEqual(testRes)
		})

		it('should return krr-data without reservert', () => {
			const testKrr2 = { ...testKrrData, reservert: false }
			const testRes2 = {
				header: 'Kontaktinformasjon og reservasjon',
				data: [
					{
						id: 'mobil',
						label: 'Mobilnummer',
						value: '987654312'
					},
					{
						id: 'epost',
						label: 'Epost',
						value: 'nav@nav.no'
					},
					{
						id: 'reservert',
						label: 'Reservert mot digitalkommunikasjon',
						value: 'NEI'
					}
				]
			}
			expect(mapKrrData(testKrr2)).toEqual(testRes2)
		})
	})
})
