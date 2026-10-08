import { fireEvent, render, renderHook, screen } from '@testing-library/react'
import '@testing-library/jest-dom/vitest'
import { describe, expect, it } from 'vitest'
import { FieldValues, FormProvider, useForm } from 'react-hook-form'
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'

const setup = (updateFormOnBlur: boolean) => {
	const { result } = renderHook(() => useForm<FieldValues>({ defaultValues: { felt: '' } }))
	const formMethods = result.current
	render(
		<FormProvider {...formMethods}>
			<TextInput name="felt" data-testid="felt" updateFormOnBlur={updateFormOnBlur} />
		</FormProvider>,
	)
	return { formMethods, input: screen.getByTestId('felt') }
}

describe('TextInput updateFormOnBlur', () => {
	it('should update the form value only when the input loses focus', () => {
		const { formMethods, input } = setup(true)

		fireEvent.change(input, { target: { value: '1234' } })

		expect(input).toHaveValue('1234')
		expect(formMethods.getValues('felt')).toBe('')

		fireEvent.blur(input)

		expect(formMethods.getValues('felt')).toBe('1234')
		expect(formMethods.getFieldState('felt').isDirty).toBe(true)
	})

	it('should update the form value while typing by default', () => {
		const { formMethods, input } = setup(false)

		fireEvent.change(input, { target: { value: '1234' } })

		expect(formMethods.getValues('felt')).toBe('1234')
	})
})
