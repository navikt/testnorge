/// <reference types="@testing-library/jest-dom" />
import { describe, expect } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { FormProvider, useForm } from 'react-hook-form'
import { DollyDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { dollyTest } from '../vitest.setup'

const DatepickerWrapper = ({
	name = 'testDate',
	defaultValue = null,
	onChange = undefined,
}: {
	name?: string
	defaultValue?: string | null
	onChange?: (date: Date | null) => void
}) => {
	const formMethods = useForm({
		defaultValues: {
			[name]: defaultValue,
		},
	})

	return (
		<FormProvider {...formMethods}>
			<DollyDatepicker name={name} label="Test Date" onChange={onChange} />
			<div data-testid="form-value">{formMethods.watch(name) || 'empty'}</div>
		</FormProvider>
	)
}

describe('DollyDatepicker - Manual Input', () => {
	dollyTest('should parse manually entered date correctly in DD.MM.YYYY format', async () => {
		const user = userEvent.setup()
		render(<DatepickerWrapper />)

		const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

		await user.type(input, '04.11.2025')
		await user.tab()

		await waitFor(() => {
			const formValue = screen.getByTestId('form-value')
			expect(formValue.textContent).toContain('2025-11-04')
		})

		expect(input).toHaveValue('04.11.2025')
	})

	dollyTest('should handle day and month correctly without swapping', async () => {
		const user = userEvent.setup()
		render(<DatepickerWrapper />)

		const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

		await user.type(input, '06.11.2025')
		await user.tab()

		await waitFor(() => {
			const formValue = screen.getByTestId('form-value')
			expect(formValue.textContent).toContain('2025-11-06')
		})

		expect(input).toHaveValue('06.11.2025')
	})

	dollyTest('should set default time to 06:00:00 for date-only input', async () => {
		const user = userEvent.setup()
		render(<DatepickerWrapper />)

		const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

		await user.type(input, '07.10.2025')
		await user.tab()

		await waitFor(() => {
			const formValue = screen.getByTestId('form-value')
			expect(formValue.textContent).toBe('2025-10-07T06:00:00')
		})
	})

	dollyTest('should handle dates without timezone shifts', async () => {
		const user = userEvent.setup()
		render(<DatepickerWrapper />)

		const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

		await user.type(input, '30.10.2025')
		await user.tab()

		await waitFor(() => {
			const formValue = screen.getByTestId('form-value')
			expect(formValue.textContent).toBe('2025-10-30T06:00:00')
		})

		expect(input).toHaveValue('30.10.2025')
	})

	dollyTest('should handle various date formats', async () => {
		const user = userEvent.setup()
		render(<DatepickerWrapper />)

		const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

		const testCases = [
			{ input: '01.01.2025', expectedForm: '2025-01-01T06:00:00', expectedDisplay: '01.01.2025' },
			{ input: '31.12.2024', expectedForm: '2024-12-31T06:00:00', expectedDisplay: '31.12.2024' },
			{ input: '15.06.2025', expectedForm: '2025-06-15T06:00:00', expectedDisplay: '15.06.2025' },
		]

		for (const testCase of testCases) {
			await user.clear(input)
			await user.type(input, testCase.input)
			await user.tab()

			await waitFor(() => {
				const formValue = screen.getByTestId('form-value')
				expect(formValue.textContent).toBe(testCase.expectedForm)
			})

			expect(input).toHaveValue(testCase.expectedDisplay)
		}
	})
})

describe('DollyDatepicker - Blur Behavior', () => {
	dollyTest(
		'should not change value when user enters and leaves field without input (single blur)',
		async () => {
			const user = userEvent.setup()
			render(<DatepickerWrapper defaultValue="2025-11-04T06:00:00" />)

			const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

			await waitFor(() => {
				expect(input).toHaveValue('04.11.2025')
			})

			const initialFormValue = screen.getByTestId('form-value').textContent

			await user.click(input)
			await user.tab()

			await waitFor(() => {
				const formValue = screen.getByTestId('form-value')
				expect(formValue.textContent).toBe(initialFormValue)
			})

			expect(input).toHaveValue('04.11.2025')
		},
	)

	dollyTest(
		'should not change value when user enters and leaves field without input (double blur)',
		async () => {
			const user = userEvent.setup()
			render(<DatepickerWrapper defaultValue="2025-11-04T06:00:00" />)

			const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

			await waitFor(() => {
				expect(input).toHaveValue('04.11.2025')
			})

			const initialFormValue = screen.getByTestId('form-value').textContent

			await user.click(input)
			await user.tab()

			await waitFor(() => {
				const formValue = screen.getByTestId('form-value')
				expect(formValue.textContent).toBe(initialFormValue)
			})

			expect(input).toHaveValue('04.11.2025')

			await user.click(input)
			await user.tab()

			await waitFor(() => {
				const formValue = screen.getByTestId('form-value')
				expect(formValue.textContent).toBe(initialFormValue)
			})

			expect(input).toHaveValue('04.11.2025')
		},
	)

	dollyTest(
		'should not change value when user enters and leaves field without input (triple blur)',
		async () => {
			const user = userEvent.setup()
			render(<DatepickerWrapper defaultValue="2025-11-04T06:00:00" />)

			const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

			await waitFor(() => {
				expect(input).toHaveValue('04.11.2025')
			})

			const initialFormValue = screen.getByTestId('form-value').textContent

			for (let i = 0; i < 3; i++) {
				await user.click(input)
				await user.tab()

				await waitFor(() => {
					const formValue = screen.getByTestId('form-value')
					expect(formValue.textContent).toBe(initialFormValue)
				})

				expect(input).toHaveValue('04.11.2025')
			}
		},
	)

	dollyTest('should not swap day and month on repeated blur cycles', async () => {
		const user = userEvent.setup()
		render(<DatepickerWrapper defaultValue="2025-11-06T06:00:00" />)

		const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

		await waitFor(() => {
			expect(input).toHaveValue('06.11.2025')
		})

		for (let i = 0; i < 5; i++) {
			await user.click(input)
			await user.tab()

			await waitFor(() => {
				const formValue = screen.getByTestId('form-value')
				expect(formValue.textContent).toBe('2025-11-06T06:00:00')
			})

			expect(input).toHaveValue('06.11.2025')
		}
	})

	dollyTest('should maintain correct date after focus and blur without changes', async () => {
		const user = userEvent.setup()
		render(<DatepickerWrapper defaultValue="2025-04-11T06:00:00" />)

		const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

		await waitFor(() => {
			expect(input).toHaveValue('11.04.2025')
		})

		await user.click(input)
		await user.tab()

		await waitFor(() => {
			const formValue = screen.getByTestId('form-value')
			expect(formValue.textContent).toBe('2025-04-11T06:00:00')
		})

		expect(input).toHaveValue('11.04.2025')
	})
})

describe('DollyDatepicker - Form and Display Consistency', () => {
	dollyTest('should keep form value and display value in sync on manual input', async () => {
		const user = userEvent.setup()
		render(<DatepickerWrapper />)

		const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

		await user.type(input, '15.03.2025')
		await user.tab()

		await waitFor(() => {
			const formValue = screen.getByTestId('form-value')
			expect(formValue.textContent).toBe('2025-03-15T06:00:00')
		})

		expect(input).toHaveValue('15.03.2025')
	})

	dollyTest('should display correct format when initialized with ISO date string', async () => {
		render(<DatepickerWrapper defaultValue="2025-12-25T06:00:00" />)

		const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

		await waitFor(() => {
			expect(input).toHaveValue('25.12.2025')
		})

		const formValue = screen.getByTestId('form-value')
		expect(formValue.textContent).toBe('2025-12-25T06:00:00')
	})

	dollyTest('should handle editing existing date correctly', async () => {
		const user = userEvent.setup()
		render(<DatepickerWrapper defaultValue="2025-11-04T06:00:00" />)

		const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

		await waitFor(() => {
			expect(input).toHaveValue('04.11.2025')
		})

		await user.clear(input)
		await user.type(input, '20.11.2025')
		await user.tab()

		await waitFor(() => {
			const formValue = screen.getByTestId('form-value')
			expect(formValue.textContent).toBe('2025-11-20T06:00:00')
		})

		expect(input).toHaveValue('20.11.2025')
	})

	dollyTest('should clear form value when input is cleared', async () => {
		const user = userEvent.setup()
		render(<DatepickerWrapper defaultValue="2025-11-04T06:00:00" />)

		const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

		await waitFor(() => {
			expect(input).toHaveValue('04.11.2025')
		})

		await user.clear(input)
		await user.tab()

		await waitFor(() => {
			const formValue = screen.getByTestId('form-value')
			expect(formValue.textContent).toBe('empty')
		})

		expect(input).toHaveValue('')
	})
})

describe('DollyDatepicker - Edge Cases', () => {
	dollyTest('should handle dates at start of month', async () => {
		const user = userEvent.setup()
		render(<DatepickerWrapper />)

		const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

		await user.type(input, '01.05.2025')
		await user.tab()

		await waitFor(() => {
			const formValue = screen.getByTestId('form-value')
			expect(formValue.textContent).toBe('2025-05-01T06:00:00')
		})

		expect(input).toHaveValue('01.05.2025')
	})

	dollyTest('should handle dates at end of month', async () => {
		const user = userEvent.setup()
		render(<DatepickerWrapper />)

		const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

		await user.type(input, '31.01.2025')
		await user.tab()

		await waitFor(() => {
			const formValue = screen.getByTestId('form-value')
			expect(formValue.textContent).toBe('2025-01-31T06:00:00')
		})

		expect(input).toHaveValue('31.01.2025')
	})

	dollyTest('should handle leap year dates', async () => {
		const user = userEvent.setup()
		render(<DatepickerWrapper />)

		const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

		await user.type(input, '29.02.2024')
		await user.tab()

		await waitFor(() => {
			const formValue = screen.getByTestId('form-value')
			expect(formValue.textContent).toBe('2024-02-29T06:00:00')
		})

		expect(input).toHaveValue('29.02.2024')
	})

	dollyTest('should handle dates that could be ambiguous (DD vs MM)', async () => {
		const user = userEvent.setup()
		render(<DatepickerWrapper />)

		const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

		const ambiguousDates = [
			{ input: '11.06.2025', expectedForm: '2025-06-11T06:00:00', expectedDisplay: '11.06.2025' },
			{ input: '06.11.2025', expectedForm: '2025-11-06T06:00:00', expectedDisplay: '06.11.2025' },
			{ input: '12.01.2025', expectedForm: '2025-01-12T06:00:00', expectedDisplay: '12.01.2025' },
			{ input: '01.12.2025', expectedForm: '2025-12-01T06:00:00', expectedDisplay: '01.12.2025' },
		]

		for (const testCase of ambiguousDates) {
			await user.clear(input)
			await user.type(input, testCase.input)
			await user.tab()

			await waitFor(() => {
				const formValue = screen.getByTestId('form-value')
				expect(formValue.textContent).toBe(testCase.expectedForm)
			})

			expect(input).toHaveValue(testCase.expectedDisplay)
		}
	})

	dollyTest('should maintain date across multiple blur cycles with no edits', async () => {
		const user = userEvent.setup()
		render(<DatepickerWrapper defaultValue="2025-10-30T06:00:00" />)

		const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

		await waitFor(() => {
			expect(input).toHaveValue('30.10.2025')
		})

		for (let i = 0; i < 10; i++) {
			await user.click(input)
			await user.tab()

			await waitFor(() => {
				const formValue = screen.getByTestId('form-value')
				expect(formValue.textContent).toBe('2025-10-30T06:00:00')
			})

			expect(input).toHaveValue('30.10.2025')
		}
	})
})

describe('DollyDatepicker - ISO Format Parsing', () => {
	dollyTest('should correctly parse ISO date with time component', async () => {
		render(<DatepickerWrapper defaultValue="2025-11-04T12:30:00" />)

		const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

		await waitFor(() => {
			expect(input).toHaveValue('04.11.2025')
		})

		const formValue = screen.getByTestId('form-value')
		expect(formValue.textContent).toBe('2025-11-04T12:30:00')
	})

	dollyTest('should handle ISO dates without swapping month and day', async () => {
		const isoDates = [
			{ iso: '2025-04-11T06:00:00', display: '11.04.2025' },
			{ iso: '2025-11-04T06:00:00', display: '04.11.2025' },
			{ iso: '2025-01-12T06:00:00', display: '12.01.2025' },
			{ iso: '2025-12-01T06:00:00', display: '01.12.2025' },
		]

		for (const testCase of isoDates) {
			const { rerender } = render(<DatepickerWrapper defaultValue={testCase.iso} />)

			const input = screen.getByPlaceholderText('DD.MM.ÅÅÅÅ')

			await waitFor(() => {
				expect(input).toHaveValue(testCase.display)
			})

			const formValue = screen.getByTestId('form-value')
			expect(formValue.textContent).toBe(testCase.iso)

			rerender(<div />)
		}
	})
})
