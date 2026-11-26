import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { FormProvider, useForm } from 'react-hook-form'
import { VergemaalForm } from '@/components/fagsystem/pdlf/form/partials/vergemaal/Vergemaal'
import { initialVergemaal } from '@/components/fagsystem/pdlf/form/initialValues'

const advarsel = /Legg til et tjenesteområde der tjenestevirksomhet er «nav»/

const defaultValues = {
	vergemaal: structuredClone(initialVergemaal),
}

const TestHarness = () => {
	const formMethods = useForm({ defaultValues })

	return (
		<FormProvider {...formMethods}>
			<VergemaalForm formMethods={formMethods} path="vergemaal" erRedigering />
		</FormProvider>
	)
}

describe('VergemaalForm', () => {
	it('should update the nav warning while the user edits tjenestevirksomhet', async () => {
		const user = userEvent.setup()
		render(<TestHarness />)

		expect(screen.queryByText(advarsel)).not.toBeInTheDocument()

		const tjenestevirksomhet = screen.getByRole('textbox', { name: 'Tjenestevirksomhet' })
		await user.clear(tjenestevirksomhet)
		await user.type(tjenestevirksomhet, 'kommune')

		expect(screen.getByText(advarsel)).toBeInTheDocument()

		await user.clear(tjenestevirksomhet)
		await user.type(tjenestevirksomhet, 'nav')

		expect(screen.queryByText(advarsel)).not.toBeInTheDocument()
	})

	it('should show the nav warning when the last tjenesteomraade is removed', async () => {
		const user = userEvent.setup()
		render(<TestHarness />)

		await user.click(screen.getAllByRole('button', { name: 'Fjern' })[0])

		expect(await screen.findByText(advarsel)).toBeInTheDocument()
	})
})
