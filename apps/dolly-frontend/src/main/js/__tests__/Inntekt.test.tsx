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

describe('Inntekt autofill', () => {
	it('should autofill a single option once and let the user clear it', async () => {
		const { formMethods, rerender, view } = setup('')
		rerender(view({ beskrivelse: ['fastloenn'] }))

		await waitFor(() => {
			expect(formMethods.getValues(beskrivelsePath)).toBe('fastloenn')
		})

		act(() => formMethods.setValue(beskrivelsePath, null))
		rerender(view({ beskrivelse: ['fastloenn'] }))

		await waitFor(() => {
			expect(formMethods.getValues(beskrivelsePath)).toBeNull()
		})
	})

	it('should autofill again when the single option changes', async () => {
		const { formMethods, rerender, view } = setup('')
		rerender(view({ beskrivelse: ['fastloenn'] }))
		await waitFor(() => {
			expect(formMethods.getValues(beskrivelsePath)).toBe('fastloenn')
		})
		act(() => formMethods.setValue(beskrivelsePath, null))
		rerender(view({ beskrivelse: ['bonus'] }))

		await waitFor(() => {
			expect(formMethods.getValues(beskrivelsePath)).toBe('bonus')
		})
	})
})
