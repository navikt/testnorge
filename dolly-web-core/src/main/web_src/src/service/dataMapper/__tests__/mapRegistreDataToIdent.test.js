import {
	mapSigrunData,
	mapKrrData,
	mapArenaData,
	mapAaregData,
	mapSubItemAaregData,
	mapInstData
} from '../mapRegistreDataToIdent'
import Formatters from '~/utils/DataFormatter'

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

	describe('mapArenaData', () => {
		it('should return null without data', () => {
			expect(mapArenaData()).toBeNull()
		})

		it('should return arena-data with servicebehov', () => {
			const testArenaData1 = { data: { arbeidsokerList: { 0: { servicebehov: true } } } }
			const testKvalifiseringsgruppe1 = 'BFORM'
			const testRes1 = {
				header: 'Arena',
				data: [
					{
						id: 'brukertype',
						label: 'Brukertype',
						value: 'Med servicebehov'
					},
					{
						id: 'servicebehov',
						label: 'Servicebehov',
						value: 'BFORM - Situasjonsbestemt innsats'
					},
					{
						id: 'inaktiveringDato',
						label: 'Inaktiv fra dato',
						value: undefined
					},
					{
						id: 'aap115',
						label: 'Har 11-5 vedtak',
						value: undefined
					},
					{
						id: 'aap115_fraDato',
						label: 'Fra dato',
						value: undefined
					},
					{
						id: 'aap',
						label: 'Har AAP vedtak UA - positivt utfall',
						value: undefined
					},
					{
						id: 'aap_fraDato',
						label: 'Fra dato',
						value: undefined
					},
					{
						id: 'aap_tilDato',
						label: 'Til dato',
						value: undefined
					}
				]
			}
			expect(mapArenaData(testArenaData1, testKvalifiseringsgruppe1)).toEqual(testRes1)
		})

		it('should return arena-data without servicebehov', () => {
			const testArenaData2 = { data: { arbeidsokerList: { 0: { servicebehov: false } } } }
			const testDato2 = '2019-06-04T00:00:00'
			const testKvalifiseringsgruppe2 = undefined
			const testRes2 = {
				header: 'Arena',
				data: [
					{
						id: 'brukertype',
						label: 'Brukertype',
						value: 'Uten servicebehov'
					},
					{
						id: 'servicebehov',
						label: 'Servicebehov',
						value: undefined
					},
					{
						id: 'inaktiveringDato',
						label: 'Inaktiv fra dato',
						value: '04.06.2019'
					},
					{
						id: 'aap115',
						label: 'Har 11-5 vedtak',
						value: undefined
					},
					{
						id: 'aap115_fraDato',
						label: 'Fra dato',
						value: undefined
					},
					{
						id: 'aap',
						label: 'Har AAP vedtak UA - positivt utfall',
						value: undefined
					},
					{
						id: 'aap_fraDato',
						label: 'Fra dato',
						value: undefined
					},
					{
						id: 'aap_tilDato',
						label: 'Til dato',
						value: undefined
					}
				]
			}
			expect(mapArenaData(testArenaData2, testKvalifiseringsgruppe2, testDato2)).toEqual(testRes2)
		})
	})
	describe('mapAaregData', () => {
		it('should return null without data', () => {
			expect(mapAaregData()).toBeNull()
		})

		it('should return arena-data with 11-5 vedtak and AAP vedtak', () => {
			const testArenaData3 = { data: { arbeidsokerList: { 0: { servicebehov: true } } } }
			const testKvalifiseringsgruppe3 = 'BFORM'
			const testAap115 = [{ fraDato: '2019-01-01T00:00:00' }]
			const testAap = [{ fraDato: '2019-01-01T00:00:00', tilDato: '2019-07-10T00:00:00' }]
			const testRes3 = {
				header: 'Arena',
				data: [
					{
						id: 'brukertype',
						label: 'Brukertype',
						value: 'Med servicebehov'
					},
					{
						id: 'servicebehov',
						label: 'Servicebehov',
						value: 'BFORM - Situasjonsbestemt innsats'
					},
					{
						id: 'inaktiveringDato',
						label: 'Inaktiv fra dato',
						value: undefined
					},
					{
						id: 'aap115',
						label: 'Har 11-5 vedtak',
						value: 'Ja'
					},
					{
						id: 'aap115_fraDato',
						label: 'Fra dato',
						value: '01.01.2019'
					},
					{
						id: 'aap',
						label: 'Har AAP vedtak UA - positivt utfall',
						value: 'Ja'
					},
					{
						id: 'aap_fraDato',
						label: 'Fra dato',
						value: '01.01.2019'
					},
					{
						id: 'aap_tilDato',
						label: 'Til dato',
						value: '10.07.2019'
					}
				]
			}
			expect(
				mapArenaData(testArenaData3, testKvalifiseringsgruppe3, undefined, testAap115, testAap)
			).toEqual(testRes3)
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

	describe('mapInstData', () => {
		it('should return null without data', () => {
			expect(mapInstData()).toBeNull()
		})

		it('should return inst-data ', () => {
			const testInstData = [
				{
					faktiskSluttdato: '2018-01-01T00:00:00',
					startdato: '2017-02-01T00:00:00',
					institusjonstype: 'AS',
					varighet: 'K'
				}
			]

			const res = {
				header: 'Institusjonsopphold',
				multiple: true,
				data: testInstData.map((data, i) => {
					return {
						parent: 'institusjonsopphold',
						id: data.personidentifikator,
						value: [
							{
								id: 'id',
								label: '',
								value: `#1`,
								width: 'x-small'
							},
							{
								id: 'institusjonstype',
								label: 'Institusjonstype',
								value: 'Alders- og sykehjem'
							},
							{
								id: 'varighet',
								label: 'Varighet',
								value: 'Kortvarig'
							},
							{
								id: 'startdato',
								label: 'Startdato',
								value: '01.02.2017'
							},
							{
								id: 'faktiskSluttdato',
								label: 'Sluttdato',
								value: '01.01.2018'
							}
						]
					}
				})
			}
			expect(mapInstData(testInstData)).toEqual(res)
		})
	})
})
