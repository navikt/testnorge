import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { Form, FormProvider, useForm } from 'react-hook-form'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'

interface TestHarnessProps {
	onSubmit: () => void
}

const TestHarness = ({ onSubmit }: TestHarnessProps) => {
	const formMethods = useForm({
		defaultValues: {
			liste: [{ verdi: 'a' }],
		},
	})

	return (
		<FormProvider {...formMethods}>
			<Form onSubmit={onSubmit}>
				<FormDollyFieldArray name="liste" header="Element" newEntry={{ verdi: 'b' }}>
					{(path: string) => <span>{path}</span>}
				</FormDollyFieldArray>
			</Form>
		</FormProvider>
	)
}

describe('FormDollyFieldArray', () => {
	it('should not submit the surrounding form when adding or removing entries', async () => {
		const onSubmit = vi.fn()
		const user = userEvent.setup()
		render(<TestHarness onSubmit={onSubmit} />)

		const leggTil = screen.getByRole('button', { name: 'Element' })
		expect(leggTil).toHaveAttribute('type', 'button')
		await user.click(leggTil)
		expect(await screen.findByText('liste.1')).toBeInTheDocument()

		const fjernKnapper = screen.getAllByRole('button', { name: 'Fjern' })
		fjernKnapper.forEach((knapp) => expect(knapp).toHaveAttribute('type', 'button'))
		await user.click(fjernKnapper[0])
		expect(screen.queryByText('liste.1')).not.toBeInTheDocument()

		expect(onSubmit).not.toHaveBeenCalled()
	})
})
