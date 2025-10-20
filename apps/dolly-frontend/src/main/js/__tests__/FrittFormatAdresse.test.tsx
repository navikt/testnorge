import React from 'react'
import { fireEvent, render, screen } from '@testing-library/react'
import { FormProvider, useForm } from 'react-hook-form'
import { dollyTest } from '../vitest.setup'
import { FrittFormatAdresse } from '@/components/fagsystem/pdlf/form/partials/adresser/adressetyper/FrittFormatAdresse'

const Wrapper = ({ children }: { children: React.ReactNode }) => {
	const methods = useForm({ defaultValues: {} })
	return <FormProvider {...methods}>{children}</FormProvider>
}

dollyTest('renders initial adresselinje input', async () => {
	render(
		<Wrapper>
			<FrittFormatAdresse path="test" />
		</Wrapper>,
	)
	const first = await screen.findByLabelText('Adresselinje 1')
	expect(first).toBeInTheDocument()
})

dollyTest('shows error when adding second line before filling first', async () => {
	render(
		<Wrapper>
			<FrittFormatAdresse path="test" />
		</Wrapper>,
	)
	await screen.findByLabelText('Adresselinje 1')
	const addButton = screen.getByRole('button', { name: /Adresselinje/i })
	fireEvent.click(addButton)
	await screen.findByText(/Fyll inn gjeldende adresselinje/i)
	const inputs = screen.getAllByLabelText(/Adresselinje \d+/i)
	expect(inputs.length).toBe(1)
})

dollyTest('adds second line after filling first and removes it correctly', async () => {
	render(
		<Wrapper>
			<FrittFormatAdresse path="test" />
		</Wrapper>,
	)
	const firstInput = (await screen.findByLabelText('Adresselinje 1')) as HTMLInputElement
	fireEvent.change(firstInput, { target: { value: 'Første adresse' } })
	const addButton = screen.getByRole('button', { name: /Adresselinje/i })
	fireEvent.click(addButton)
	const secondInput = (await screen.findByLabelText('Adresselinje 2')) as HTMLInputElement
	fireEvent.change(secondInput, { target: { value: 'Andre adresse' } })
	const deleteButtons = screen.getAllByTitle('Fjern')
	fireEvent.click(deleteButtons[deleteButtons.length - 1])
	const remaining = await screen.findAllByLabelText(/Adresselinje \d+/i)
	expect(remaining.length).toBe(1)
	expect((screen.getByLabelText('Adresselinje 1') as HTMLInputElement).value).toBe('Første adresse')
})

dollyTest('shows error when adding third line before filling second', async () => {
	render(
		<Wrapper>
			<FrittFormatAdresse path="test" />
		</Wrapper>,
	)
	const firstInput = (await screen.findByLabelText('Adresselinje 1')) as HTMLInputElement
	fireEvent.change(firstInput, { target: { value: 'Første adresse' } })
	const addButton = screen.getByRole('button', { name: /Adresselinje/i })
	fireEvent.click(addButton) // add second
	await screen.findByLabelText('Adresselinje 2')
	fireEvent.click(addButton) // attempt third while second empty
	await screen.findByText(/Fyll inn gjeldende adresselinje/i)
	const inputs = screen.getAllByLabelText(/Adresselinje \d+/i)
	expect(inputs.length).toBe(2)
})

dollyTest('adds third line after filling second', async () => {
	render(
		<Wrapper>
			<FrittFormatAdresse path="test" />
		</Wrapper>,
	)
	const firstInput = (await screen.findByLabelText('Adresselinje 1')) as HTMLInputElement
	fireEvent.change(firstInput, { target: { value: 'Første adresse' } })
	const addButton = screen.getByRole('button', { name: /Adresselinje/i })
	fireEvent.click(addButton)
	const secondInput = (await screen.findByLabelText('Adresselinje 2')) as HTMLInputElement
	fireEvent.change(secondInput, { target: { value: 'Andre adresse' } })
	fireEvent.click(addButton)
	const thirdInput = await screen.findByLabelText('Adresselinje 3')
	expect(thirdInput).toBeInTheDocument()
	const inputs = screen.getAllByLabelText(/Adresselinje \d+/i)
	expect(inputs.length).toBe(3)
})
