import React from 'react'
import { act, fireEvent, render, screen } from '@testing-library/react'
import { FormProvider, useForm, UseFormReturn } from 'react-hook-form'
import { dollyTest } from '../vitest.setup'
import { Detaljer } from '@/components/fagsystem/organisasjoner/form/partials/Detaljer'

const RenderDetaljer = () => {
	const methods: UseFormReturn = useForm({ defaultValues: { organisasjon: {} } })
	return (
		<FormProvider {...methods}>
			<Detaljer formMethods={methods} path="organisasjon" level={0} />
		</FormProvider>
	)
}

dollyTest('shouldNotHaveUnderenheterInitially', async () => {
	render(<RenderDetaljer />)
	const deleteButtons = screen.queryAllByTitle('Fjern')
	expect(deleteButtons.length).toBe(0)
})

dollyTest('shouldAddSingleUnderenhetOnClick', async () => {
	render(<RenderDetaljer />)
	const addButton = await screen.findByRole('button', { name: /Underenhet/i })
	act(() => {
		fireEvent.click(addButton)
	})
	const deleteButtons = await screen.findAllByTitle('Fjern')
	expect(deleteButtons.length).toBe(1)
})
