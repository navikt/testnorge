import { act, render, renderHook, waitFor } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { FieldValues, FormProvider, useForm } from 'react-hook-form'
import Inntekt from '@/components/fagsystem/inntektstub/validerInntekt/Inntekt'

vi.mock('@/components/ui/form/inputs/select/Select', () => ({
	FormSelect: () => null,
}))

vi.mock('@/components/ui/form/inputs/textInput/TextInput', () => ({
	FormTextInput: () => null,
}))

vi.mock('@/components/ui/form/inputs/datepicker/Datepicker', () => ({
	FormDatepicker: () => null,
}))

vi.mock(
	'@/components/fagsystem/inntektstub/form/partials/inntektsinformasjonLister/inntektForm',
	() => ({ initialValues: {} }),
)

const path = 'inntektsliste.0'
const beskrivelsePath = `${path}.beskrivelse`
const fields = { beskrivelse: ['fastloenn', 'bonus'] }

const setup = (beskrivelse = 'fastloenn') => {
	const { result } = renderHook(() =>
		useForm<FieldValues>({
			defaultValues: {
				inntektsliste: [{ inntektstype: 'LOENNSINNTEKT', beskrivelse }],
			},
		}),
	)
	const formMethods = result.current
	const onValidate = vi.fn()
	const view = (options = {}, isLoadingFields = false, visible = true) => (
		<FormProvider {...formMethods}>
			{visible && (
				<Inntekt
					formMethods={formMethods}
					path={path}
					fields={options}
					isLoadingFields={isLoadingFields}
					onValidate={onValidate}
				/>
			)}
		</FormProvider>
	)
	const { rerender } = render(view())
	return { formMethods, rerender, view }
}

describe('Inntekt validation', () => {
	it('should preserve a selected description without errors when returning before options load', async () => {
		const { formMethods, rerender, view } = setup()
		rerender(view(fields))
		rerender(view({}, false, false))
		rerender(view())

		expect(formMethods.getFieldState(beskrivelsePath).error).toBeUndefined()
		rerender(view({}, true))
		expect(formMethods.getFieldState(beskrivelsePath).error).toBeUndefined()
		rerender(view(fields))

		await waitFor(() => {
			expect(formMethods.getFieldState(beskrivelsePath).error).toBeUndefined()
			expect(formMethods.getValues(beskrivelsePath)).toBe('fastloenn')
		})
	})

	it('should not validate against stale options while loading', () => {
		const { formMethods, rerender, view } = setup()
		rerender(view({ beskrivelse: ['bonus', 'overtid'] }, true))

		expect(formMethods.getFieldState(beskrivelsePath).error).toBeUndefined()
		expect(formMethods.getValues(beskrivelsePath)).toBe('fastloenn')
	})

	it.each(['', 'ukjent'])(
		'should report an invalid description "%s" once options load',
		async (value) => {
			const { formMethods, rerender, view } = setup(value)
			expect(formMethods.getFieldState(beskrivelsePath).error).toBeUndefined()
			rerender(view(fields))

			await waitFor(() => {
				expect(formMethods.getFieldState(beskrivelsePath).error).toMatchObject({
					type: 'inntektPaakrevd',
					message: 'Feltet er påkrevd',
				})
			})
		},
	)

	it('should clear its own error when the description becomes valid', async () => {
		const { formMethods, rerender, view } = setup('')
		rerender(view(fields))
		await waitFor(() => {
			expect(formMethods.getFieldState(beskrivelsePath).error).toBeDefined()
		})

		act(() => formMethods.setValue(beskrivelsePath, 'fastloenn'))

		await waitFor(() => {
			expect(formMethods.getFieldState(beskrivelsePath).error).toBeUndefined()
		})
	})

	it('should clear its own error when updated options allow the selected description', async () => {
		const { formMethods, rerender, view } = setup()
		rerender(view({ beskrivelse: ['bonus', 'overtid'] }))
		await waitFor(() => {
			expect(formMethods.getFieldState(beskrivelsePath).error).toBeDefined()
		})
		rerender(view(fields))

		await waitFor(() => {
			expect(formMethods.getFieldState(beskrivelsePath).error).toBeUndefined()
		})
	})

	it('should preserve errors from other validation', () => {
		const { formMethods, rerender, view } = setup()
		act(() => formMethods.setError(beskrivelsePath, { type: 'server', message: 'Ugyldig inntekt' }))
		rerender(view(fields))

		expect(formMethods.getFieldState(beskrivelsePath).error).toMatchObject({
			type: 'server',
			message: 'Ugyldig inntekt',
		})
	})
})
