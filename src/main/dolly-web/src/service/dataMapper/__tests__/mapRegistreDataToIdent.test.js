import { mapSigrunData, mapKrrData } from '../mapRegistreDataToIdent'

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
				data: testSigrunData.map( (data,i) => {
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
})
