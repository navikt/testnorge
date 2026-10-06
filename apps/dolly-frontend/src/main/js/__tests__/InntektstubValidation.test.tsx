import { act, renderHook } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import * as Yup from 'yup'
import { validation } from '@/components/fagsystem/inntektstub/form/validation'
import {
	fjernGyldigeVerdier,
	GYLDIGE_VERDIER,
} from '@/components/fagsystem/inntektstub/validerInntekt/gyldigeVerdier'

const inntektPath = 'inntektstub.inntektsinformasjon.0.inntektsliste.0'
const persontypePath = `${inntektPath}.tilleggsinformasjon.reiseKostOgLosji.persontype`

const gyldigeVerdier = {
	beskrivelse: ['fastloenn', 'bonus'],
	inngaarIGrunnlagForTrekk: [true, false],
	fordel: ['kontantytelse'],
	skatteOgAvgiftsregel: ['<TOM>', 'svalbard'],
	opptjeningsland: ['<TOM>', '<UTFYLT>'],
	antall: ['<TOM>'],
	persontype: ['kunde', 'ansatt'],
}

const lagInntekt = (overstyring = {}) => ({
	beloep: 1000,
	inntektstype: 'LOENNSINNTEKT',
	beskrivelse: 'fastloenn',
	inngaarIGrunnlagForTrekk: false,
	tilleggsinformasjon: { reiseKostOgLosji: { persontype: 'kunde' } },
	[GYLDIGE_VERDIER]: gyldigeVerdier,
	...overstyring,
})

const setup = (inntekt: Record<string, unknown>) => {
	const { result } = renderHook(() =>
		useForm({
			mode: 'onChange',
			resolver: yupResolver(Yup.object(validation) as any),
			defaultValues: {
				inntektstub: {
					inntektsinformasjon: [
						{
							sisteAarMaaned: '2026-01',
							virksomhet: '947064649',
							opplysningspliktig: '947064640',
							inntektsliste: [inntekt],
						},
					],
				},
			},
		}),
	)
	const validerSomVedNavigering = async () => {
		let gyldig = false
		await act(async () => {
			gyldig = await result.current.trigger(['inntektstub'])
		})
		return gyldig
	}
	return { form: result, validerSomVedNavigering }
}

describe('Inntektstub validation of dynamic fields', () => {
	it('should accept an inntekt that satisfies the valid values from the API', async () => {
		const { validerSomVedNavigering } = setup(lagInntekt())

		expect(await validerSomVedNavigering()).toBe(true)
	})

	it('should block navigation when a nested tilleggsinformasjon field is missing', async () => {
		const { form, validerSomVedNavigering } = setup(
			lagInntekt({ tilleggsinformasjon: { reiseKostOgLosji: {} } }),
		)

		expect(await validerSomVedNavigering()).toBe(false)
		expect(form.current.getFieldState(persontypePath).error?.message).toBe('Feltet er påkrevd')
	})

	it('should report every required field without a value, including false-able booleans', async () => {
		const { form, validerSomVedNavigering } = setup(
			lagInntekt({ inngaarIGrunnlagForTrekk: null, beskrivelse: 'ukjent' }),
		)

		expect(await validerSomVedNavigering()).toBe(false)
		expect(
			form.current.getFieldState(`${inntektPath}.inngaarIGrunnlagForTrekk`).error,
		).toBeDefined()
		expect(form.current.getFieldState(`${inntektPath}.beskrivelse`).error).toBeDefined()
	})

	it('should not require optional, empty-only or single-option fields', async () => {
		const { form, validerSomVedNavigering } = setup(lagInntekt())

		await validerSomVedNavigering()

		for (const felt of ['skatteOgAvgiftsregel', 'opptjeningsland', 'antall', 'fordel']) {
			expect(form.current.getFieldState(`${inntektPath}.${felt}`).error).toBeUndefined()
		}
	})

	it('should clear the error once the field gets a valid value', async () => {
		const { form, validerSomVedNavigering } = setup(
			lagInntekt({ tilleggsinformasjon: { reiseKostOgLosji: {} } }),
		)
		await validerSomVedNavigering()

		act(() => form.current.setValue(persontypePath, 'ansatt'))

		expect(await validerSomVedNavigering()).toBe(true)
		expect(form.current.getFieldState(persontypePath).error).toBeUndefined()
	})

	it('should skip dynamic validation until valid values are fetched', async () => {
		const { validerSomVedNavigering } = setup(
			lagInntekt({ [GYLDIGE_VERDIER]: undefined, inngaarIGrunnlagForTrekk: null }),
		)

		expect(await validerSomVedNavigering()).toBe(true)
	})
})

describe('fjernGyldigeVerdier', () => {
	it('should remove the valid values from every inntekt before submit', () => {
		const renset = fjernGyldigeVerdier({
			inntektsinformasjon: [
				{ virksomhet: '1', inntektsliste: [lagInntekt()] },
				{ virksomhet: '2' },
			],
		})

		expect(renset.inntektsinformasjon[0].inntektsliste[0]).not.toHaveProperty(GYLDIGE_VERDIER)
		expect(renset.inntektsinformasjon[0].inntektsliste[0].beskrivelse).toBe('fastloenn')
		expect(renset.inntektsinformasjon[1]).toEqual({ virksomhet: '2' })
	})
})
