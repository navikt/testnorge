import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { FormProvider, useForm, useWatch } from 'react-hook-form'
import { vi } from 'vitest'
import type { ReactNode } from 'react'
import { DollyDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'

vi.mock('@navikt/ds-react', async (importOriginal) => {
	const actual = await importOriginal<typeof import('@navikt/ds-react')>()

	return {
		...actual,
		Button: ({ children, ...props }: { children: ReactNode }) => <button {...props}>{children}</button>,
		DatePicker: ({ children }: { children: ReactNode }) => <>{children}</>,
		Tooltip: ({ children }: { children: ReactNode }) => <>{children}</>,
		useDatepicker: () => ({
			datepickerProps: {},
			setSelected: vi.fn(),
		}),
	}
})

const TestHarness = () => {
	const formMethods = useForm({
		defaultValues: {
			date: '',
		},
	})
	const value = useWatch({
		control: formMethods.control,
		name: 'date',
	})

	return (
		<FormProvider {...formMethods}>
			<form>
				<DollyDatepicker name="date" label="Rapporteringstidspunkt" format="DD.MM.YYYY HH:mm" />
				<div data-testid="stored-value">{value ?? ''}</div>
			</form>
		</FormProvider>
	)
}

describe('DollyDatepicker', () => {
	it('should not store localized date text in form state', async () => {
		const user = userEvent.setup()

		render(<TestHarness />)

		await user.type(screen.getByRole('textbox', { name: 'Rapporteringstidspunkt' }), '05.06.2025')

		expect(
			(screen.getByRole('textbox', { name: 'Rapporteringstidspunkt' }) as HTMLInputElement).value,
		).toBe('05.06.2025')
		expect(screen.getByTestId('stored-value').textContent).toBe('')
	})
})
