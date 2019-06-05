import {
	mapSigrunData,
	mapKrrData,
	mapAaregData,
	mapSubItemAaregData
} from '../mapRegistreDataToIdent'

describe('mapDetailedData.js', () => {
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
				data: testSigrunData.map((data, i) => {
					return {
						parent: 'inntekter',
						id: data.personidentifikator,
						value: [
							{
								id: 'id',
								label: '',
								value: `#${i + 1}`,
								width: 'x-small'
							},
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

	describe('mapAaregData', () => {
		it('should return null without data', () => {
			expect(mapAaregData()).toBeNull()
		})

		const testAaregData = [
			{
				arbeidsforholdId: '010101987654',
				arbeidsavtaler: [
					{
						yrke: 'Bonde',
						stillingsprosent: '100'
					}
				],
				ansettelsesperiode: {
					periode: {
						fom: '2019-02-10',
						tom: '2019-12-10'
					}
				},
				arbeidsgiver: {
					type: 'ORG',
					organisasjonsnummer: '991606416',
					offentligIdent: ''
				}
			}
		]

		it('should return null without subdata', () => {
			expect(mapSubItemAaregData(testAaregData[0])).toEqual([])
		})

		const aaregRes = {
			header: 'Arbeidsforhold',
			multiple: true,
			data: testAaregData.map((data, i) => {
				return {
					parent: 'arbeidsforhold',
					id: data.arbeidsforholdId,
					label: 'Arbeidsforhold',
					value: [
						{
							id: 'id',
							label: '',
							value: `#${i + 1}`,
							width: 'x-small'
						},
						{
							id: 'yrke',
							label: 'Yrke',
							value: 'Bonde',
							apiKodeverkId: 'Yrker'
						},
						{
							id: 'startdato',
							label: 'Startdato',
							value: '2019-02-10'
						},
						{
							id: 'sluttdato',
							label: 'Sluttdato',
							value: '2019-12-10'
						},
						{
							id: 'stillingprosent',
							label: 'Stillingprosent',
							value: '100'
						},
						{
							id: 'typearbeidsgiver',
							label: 'Type av arbeidsgiver',
							value: 'ORG'
						},

						{
							id: 'orgnr',
							label: 'Orgnummer',
							value: '991606416'
						},
						{
							id: 'orgnr',
							label: 'Arbeidsgiver Ident',
							value: ''
						}
					]
				}
			})
		}

		expect(mapAaregData(testAaregData)).toEqual(aaregRes)

		const testAaregDataMedSubItem = [
			{
				arbeidsforholdId: '010101987654',
				arbeidsavtaler: [
					{
						yrke: 'Bonde',
						stillingsprosent: '90'
					}
				],
				ansettelsesperiode: {
					periode: {
						fom: '2019-02-10',
						tom: '2019-12-10'
					}
				},
				arbeidsgiver: {
					type: 'ORG',
					organisasjonsnummer: '991606416',
					offentligIdent: ''
				},
				permisjonPermitteringer: [
					{
						type: 'Foreldrepenger',
						periode: {
							fom: '2019-03-10',
							tom: '2019-04-30'
						}
					}
				]
			}
		]

		const aaregResSubItem = {
			header: 'Arbeidsforhold',
			multiple: true,
			data: testAaregData.map((data, i) => {
				return {
					parent: 'arbeidsforhold',
					label: 'Arbeidsforhold',
					id: data.arbeidsforholdId,
					value: [
						{
							id: 'id',
							label: '',
							value: `#${i + 1}`,
							width: 'x-small'
						},
						{
							id: 'yrke',
							label: 'Yrke',
							value: 'Bonde',
							apiKodeverkId: 'Yrker'
						},
						{
							id: 'startdato',
							label: 'Startdato',
							value: '2019-02-10'
						},
						{
							id: 'sluttdato',
							label: 'Sluttdato',
							value: '2019-12-10'
						},
						{
							id: 'stillingprosent',
							label: 'Stillingprosent',
							value: '90'
						},
						{
							id: 'typearbeidsgiver',
							label: 'Type av arbeidsgiver',
							value: 'ORG'
						},

						{
							id: 'orgnr',
							label: 'Orgnummer',
							value: '991606416'
						},
						{
							id: 'orgnr',
							label: 'Arbeidsgiver Ident',
							value: ''
						},
						{
							id: 'permisjon',
							label: 'Permisjon',
							subItem: true,
							value: [
								[
									{
										id: 'id',
										label: '',
										value: `#1`,
										width: 'x-small'
									},
									{
										id: 'permisjonOgPermittering',
										label: 'Permisjonstype',
										value: 'Foreldrepenger',
										width: 'medium'
									},
									{
										id: 'fom',
										label: 'Startdato',
										value: '2019-03-10'
									},
									{
										id: 'tom',
										label: 'Sluttdato',
										value: '2019-04-30'
									}
								]
							]
						}
					]
				}
			})
		}

		expect(mapAaregData(testAaregDataMedSubItem)).toEqual(aaregResSubItem)
	})
})
