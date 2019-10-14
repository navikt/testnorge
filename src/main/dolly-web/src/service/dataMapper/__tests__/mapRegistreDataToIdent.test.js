import {
	mapSigrunData,
	mapKrrData,
	mapArenaData,
	mapAaregData,
	mapSubItemAaregData,
	mapInstData,
	mapUdiData,
	mapAliasData
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
								value: 'Test grunnlag'
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

		const testKrrData = {
			mobil: '987654312',
			epost: 'nav@nav.no',
			reservert: true,
			gyldigFra: '2019-10-10T00:00:00',
			registrert: false
		}

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
					},
					{
						id: 'gyldigFra',
						label: 'Gyldig fra',
						value: '10.10.2019'
					},
					{
						id: 'registrert',
						label: 'Registrert i DKIF',
						value: 'NEI'
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
					},
					{
						id: 'gyldigFra',
						label: 'Gyldig fra',
						value: '10.10.2019'
					},
					{
						id: 'registrert',
						label: 'Registrert i DKIF',
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
					faktiskSluttdato: '2018-01-01',
					startdato: '2017-02-01',
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

	describe('mapUdiData', () => {
		it('should return null without data', () => {
			expect(mapUdiData()).toBeNull()
		})

		it('sholud return udi-data with oppholdsstatus, arbeidsadgang, flyktningstatus and asylsøker', () => {
			const testUdiData = {
				oppholdStatus: {
					eosEllerEFTABeslutningOmOppholdsrett: 'VARIG',
					eosEllerEFTABeslutningOmOppholdsrettEffektuering: '2019-09-02',
					eosEllerEFTABeslutningOmOppholdsrettPeriode: { fra: '2019-09-01', til: '2019-09-30' }
				},
				arbeidsadgang: {
					arbeidsOmfang: 'INGEN_KRAV_TIL_STILLINGSPROSENT',
					harArbeidsAdgang: 'JA',
					periode: { fra: '2019-09-01', til: '2019-09-30' },
					typeArbeidsadgang: 'BESTEMT_ARBEIDSGIVER_ELLER_OPPDRAGSGIVER'
				},
				flyktning: true,
				soeknadOmBeskyttelseUnderBehandling: 'JA'
			}
			const testRes = {
				header: 'UDI',
				data: [
					{
						id: 'oppholdsstatus',
						label: 'Oppholdsstatus',
						value: 'EØS- eller EFTA-opphold'
					},
					{
						id: 'typeOpphold',
						label: 'Type opphold',
						value: 'Beslutning om oppholdsrett fra EØS eller EFTA'
					},
					{
						id: 'status',
						label: 'Status',
						value: null
					},
					{
						id: 'oppholdFraDato',
						label: 'Oppholdstillatelse fra dato',
						value: '01.09.2019'
					},
					{
						id: 'oppholdTilDato',
						label: 'Oppholdstillatelse til dato',
						value: '30.09.2019'
					},
					{
						id: 'effektueringsdato',
						label: 'Effektueringsdato',
						value: '02.09.2019'
					},
					{
						id: 'typeOppholdstillatelse',
						label: 'Type oppholdstillatelse',
						value: undefined
					},
					{
						id: 'vedtaksdato',
						label: 'Vedtaksdato',
						value: undefined
					},
					{
						id: 'grunnlagForOpphold',
						label: 'Grunnlag for opphold',
						value: 'Varig'
					},
					{
						id: 'uavklart',
						label: 'Uavklart',
						value: undefined
					},
					{
						id: 'harArbeidsadgang',
						label: 'Har arbeidsadgang',
						value: 'Ja'
					},
					{
						id: 'typeArbeidsadgang',
						label: 'Type arbeidsadgang',
						value: 'Bestemt arbeidsgiver eller oppdragsgiver'
					},
					{
						id: 'arbeidsOmfang',
						label: 'Arbeidsomfang',
						value: 'Ingen krav til stillingsprosent'
					},
					{
						id: 'arbeidsadgangFraDato',
						label: 'Arbeidsadgang fra dato',
						value: '01.09.2019'
					},
					{
						id: 'arbeidsadgangTilDato',
						label: 'Arbeidsadgang til dato',
						value: '30.09.2019'
					},
					{
						id: 'flyktningstatus',
						label: 'Flyktningstatus',
						value: 'Ja'
					},
					{
						id: 'asylsøker',
						label: 'Asylsøker',
						value: 'Ja'
					}
				]
			}
			expect(mapUdiData(testUdiData)).toEqual(testRes)
		})
	})

	describe('mapAliasData', () => {
		it('should return null without data', () => {
			expect(mapAliasData()).toBeNull()
		})

		it('should return udi-data with aliaser', () => {
			const testUdiData = [
				{
					fnr: '07028229601',
					navn: { etternavn: 'Maskin', fornavn: 'Frodig', mellomnavn: 'Glitrende' }
				},
				{
					fnr: '07028200263',
					navn: { etternavn: 'Tøffeldyr', fornavn: 'Slapp', mellomnavn: 'Utmattende' }
				}
			]

			const testRes = {
				header: 'Alias',
				multiple: true,
				data: [
					{
						id: 0,
						parent: 'aliaser',
						value: [
							{
								id: 'id',
								label: '',
								value: '#1',
								width: 'x-small'
							},
							{
								id: 'fnr',
								label: 'FNR/DNR',
								value: '07028229601'
							},
							{
								id: 'fornavn',
								label: 'Fornavn',
								value: 'Frodig'
							},
							{
								id: 'mellomnavn',
								label: 'Mellomnavn',
								value: 'Glitrende'
							},
							{
								id: 'etternavn',
								label: 'Etternavn',
								value: 'Maskin'
							}
						]
					},
					{
						id: 1,
						parent: 'aliaser',
						value: [
							{
								id: 'id',
								label: '',
								value: '#2',
								width: 'x-small'
							},
							{
								id: 'fnr',
								label: 'FNR/DNR',
								value: '07028200263'
							},
							{
								id: 'fornavn',
								label: 'Fornavn',
								value: 'Slapp'
							},
							{
								id: 'mellomnavn',
								label: 'Mellomnavn',
								value: 'Utmattende'
							},
							{
								id: 'etternavn',
								label: 'Etternavn',
								value: 'Tøffeldyr'
							}
						]
					}
				]
			}
			expect(mapAliasData(testUdiData)).toEqual(testRes)
		})
	})
})
