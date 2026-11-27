import { render, screen, waitFor } from '@testing-library/react'
import { dollyTest } from '../vitest.setup'
import userEvent from '@testing-library/user-event'
import React from 'react'
import { vi } from 'vitest'
import { GjenopprettDialog } from '@/components/bestilling/gjenopprett/GjenopprettDialog'
import { TestComponentSelectors } from '#/mocks/Selectors'

const renderComponent = (props?: Partial<React.ComponentProps<typeof GjenopprettDialog>>) => {
	const submitForm = vi.fn()
	const mergedProps: React.ComponentProps<typeof GjenopprettDialog> = {
		title: 'Gjenopprett gruppe',
		submitForm: submitForm,
		environments: props?.environments,
		beskrivelse: props?.beskrivelse,
		bestilling: props?.bestilling,
		antallIdenter: props?.antallIdenter,
		bestilteMiljoer: props?.bestilteMiljoer,
	}
	const view = render(<GjenopprettDialog {...mergedProps} />)
	return { submitForm, view }
}

const openDialog = async (user: ReturnType<typeof userEvent.setup>) => {
	await user.click(screen.getByTestId(TestComponentSelectors.BUTTON_GJENOPPRETT_GRUPPE))
	await screen.findByText('Velg miljø å gjenopprette i')
}

const submitDialog = (user: ReturnType<typeof userEvent.setup>) =>
	user.click(screen.getByTestId(TestComponentSelectors.BUTTON_BESTILLINGDETALJER_GJENOPPRETT_UTFOER))

dollyTest('selects environment and includes it in submitted form data', async () => {
	const user = userEvent.setup()
	const { submitForm } = renderComponent()
	await openDialog(user)
	const q1Checkbox = screen.getByRole('checkbox', { name: 'Q1' }) as HTMLInputElement
	await user.click(q1Checkbox)
	await waitFor(() => expect(q1Checkbox.checked).toBe(true))
	await submitDialog(user)
	await waitFor(() => {
		expect(submitForm).toHaveBeenCalledTimes(1)
		const submitted = submitForm.mock.calls[0][0]
		expect(submitted.environments).toContain('q1')
	})
})

dollyTest('preloads provided environments and keeps them selected', async () => {
	const user = userEvent.setup()
	const { submitForm } = renderComponent({ environments: ['q2'] })
	await openDialog(user)
	const q2Checkbox = screen.getByRole('checkbox', { name: 'Q2' }) as HTMLInputElement
	await waitFor(() => expect(q2Checkbox.checked).toBe(true))
	await submitDialog(user)
	await waitFor(() => {
		const submitted = submitForm.mock.calls[0][0]
		expect(submitted.environments).toContain('q2')
	})
})

dollyTest('deselects previously selected environment', async () => {
	const user = userEvent.setup()
	renderComponent()
	await openDialog(user)
	const q1Checkbox = screen.getByRole('checkbox', { name: 'Q1' }) as HTMLInputElement
	await user.click(q1Checkbox)
	await waitFor(() => expect(q1Checkbox.checked).toBe(true))
	await user.click(q1Checkbox)
	await waitFor(() => expect(q1Checkbox.checked).toBe(false))
})

dollyTest('should reflect selected environment visually when clicking label text', async () => {
	const user = userEvent.setup()
	const { submitForm } = renderComponent()
	await openDialog(user)
	await user.click(screen.getByText('Q1'))
	const q1Checkbox = screen.getByRole('checkbox', { name: 'Q1' }) as HTMLInputElement
	await waitFor(() => expect(q1Checkbox.checked).toBe(true))
	await submitDialog(user)
	await waitFor(() => {
		const submitted = submitForm.mock.calls[0][0]
		expect(submitted.environments).toContain('q1')
	})
})

dollyTest('raw input should have checked=true after selecting environment', async () => {
	const user = userEvent.setup()
	renderComponent()
	await openDialog(user)
	await user.click(screen.getByText('Q1'))
	const rawInput = document.getElementById('q1') as HTMLInputElement
	await waitFor(() => expect(rawInput.checked).toBe(true))
})
