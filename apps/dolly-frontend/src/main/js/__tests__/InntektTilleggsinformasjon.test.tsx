import { describe, expect, it } from 'vitest'
import {
	tilFlatInntekt,
	tilNestedInntekt,
} from '@/components/fagsystem/inntektstub/validerInntekt/tilleggsinformasjon'

const pensjonNestet = {
	beloep: 1000,
	inntektstype: 'PENSJON_ELLER_TRYGD',
	tilleggsinformasjon: {
		pensjon: {
			grunnpensjonsbeloep: 1234.5,
			pensjonsgrad: 100,
			tidsrom: { startdato: '2026-01-01', sluttdato: '2026-12-31' },
		},
	},
}

const pensjonFlat = {
	beloep: 1000,
	inntektstype: 'PENSJON_ELLER_TRYGD',
	tilleggsinformasjonstype: 'AldersUfoereEtterlatteAvtalefestetOgKrigspensjon',
	grunnpensjonsbeloep: 1234.5,
	pensjonsgrad: 100,
	pensjonTidsromStart: '2026-01-01',
	pensjonTidsromSlutt: '2026-12-31',
}

describe('tilFlatInntekt', () => {
	it('should flatten nested tilleggsinformasjon and derive its type', () => {
		expect(tilFlatInntekt(pensjonNestet)).toEqual(pensjonFlat)
	})

	it('should derive the type for a nested object whose key differs from the type', () => {
		expect(
			tilFlatInntekt({
				tilleggsinformasjon: {
					inntjeningsforhold: { inntjeningsforhold: 'loennVedArbeidsmarkedstiltak' },
				},
			}),
		).toEqual({
			tilleggsinformasjonstype: 'SpesielleInntjeningsforhold',
			inntjeningsforhold: 'loennVedArbeidsmarkedstiltak',
		})
	})

	it('should derive the type for marker-only tilleggsinformasjon', () => {
		expect(tilFlatInntekt({ tilleggsinformasjon: { nettoloenn: {} } })).toEqual({
			tilleggsinformasjonstype: 'Nettoloennsordning',
		})
	})

	it('should leave an inntekt without nested tilleggsinformasjon unchanged', () => {
		const inntekt = { beloep: 1, fordel: 'kontantytelse' }

		expect(tilFlatInntekt(inntekt)).toBe(inntekt)
	})
})

describe('tilNestetInntekt', () => {
	it('should nest the flat fields belonging to the selected type', () => {
		expect(tilNestedInntekt(pensjonFlat)).toEqual(pensjonNestet)
	})

	it('should create an empty object for marker-only types', () => {
		expect(tilNestedInntekt({ beloep: 1, tilleggsinformasjonstype: 'Nettoloennsordning' })).toEqual(
			{
				beloep: 1,
				tilleggsinformasjon: { nettoloenn: {} },
			},
		)
	})

	it('should drop flat fields that do not belong to the selected type', () => {
		expect(
			tilNestedInntekt({
				tilleggsinformasjonstype: 'SpesielleInntjeningsforhold',
				inntjeningsforhold: 'loennVedArbeidsmarkedstiltak',
				grunnpensjonsbeloep: 99,
				persontype: '',
			}),
		).toEqual({
			tilleggsinformasjon: {
				inntjeningsforhold: { inntjeningsforhold: 'loennVedArbeidsmarkedstiltak' },
			},
		})
	})

	it('should drop all tilleggsinformasjon fields when no type is selected', () => {
		expect(
			tilNestedInntekt({ beloep: 1, grunnpensjonsbeloep: 99, tilleggsinformasjonstype: '' }),
		).toEqual({
			beloep: 1,
		})
	})

	it('should keep untouched nested tilleggsinformasjon from rows that were never flattened', () => {
		const inntekt = { beloep: 1, tilleggsinformasjon: { livrente: {} } }

		expect(tilNestedInntekt(inntekt)).toEqual(inntekt)
	})

	it('should round-trip every type', () => {
		const typer = [
			'BilOgBaat',
			'BonusFraForsvaret',
			'DagmammaIEgenBolig',
			'Periode',
			'NorskKontinentalsokkel',
			'Livrente',
			'LottOgPartInnenFiske',
			'Nettoloennsordning',
			'AldersUfoereEtterlatteAvtalefestetOgKrigspensjon',
			'ReiseKostOgLosji',
			'SpesielleInntjeningsforhold',
			'UtenlandskArtist',
		]
		for (const tilleggsinformasjonstype of typer) {
			const flat = { beloep: 1, tilleggsinformasjonstype }

			expect(tilFlatInntekt(tilNestedInntekt(flat))).toEqual(flat)
		}
	})
})
