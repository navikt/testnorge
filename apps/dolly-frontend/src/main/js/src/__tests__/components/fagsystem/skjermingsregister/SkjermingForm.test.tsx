import { render, screen } from '@testing-library/react'
import { FormProvider, useForm } from 'react-hook-form'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { SkjermingForm } from '@/components/fagsystem/skjermingsregister/form/SkjermingForm'

interface TestHarnessProps {
	personFoerLeggTil?: {
		skjermingsregister: {
			skjermetFra: string
		}
	}
}

const TestHarness = ({ personFoerLeggTil }: TestHarnessProps) => {
	const formMethods = useForm({
		defaultValues: {
			skjerming: {
				egenAnsattDatoFom: '2026-08-01',
			},
		},
	})

	return (
		<BestillingsveilederContext value={{ initialValues: {}, personFoerLeggTil }}>
			<FormProvider {...formMethods}>
				<SkjermingForm />
			</FormProvider>
		</BestillingsveilederContext>
	)
}

describe('SkjermingForm', () => {
	it('should display end date when the person already has shielding', () => {
		render(
			<TestHarness
				personFoerLeggTil={{
					skjermingsregister: {
						skjermetFra: '2026-08-01',
					},
				}}
			/>,
		)

		expect(screen.getByRole('textbox', { name: 'Skjerming til' })).toBeInTheDocument()
	})

	it('should hide end date for a new order', () => {
		render(<TestHarness />)

		expect(screen.queryByRole('textbox', { name: 'Skjerming til' })).not.toBeInTheDocument()
	})
})
