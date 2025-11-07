import { render, screen, waitFor } from '@testing-library/react'
import { dollyTest } from '../vitest.setup'
import userEvent from '@testing-library/user-event'
import React from 'react'
import { vi } from 'vitest'
import { GjenopprettModal } from '@/components/bestilling/gjenopprett/GjenopprettModal'

vi.mock('@/utils/hooks/useEnvironments', () => ({
	useDollyEnvironments: () => ({
		dollyEnvironments: {
			Q: [
				{ id: 'q1', label: 'Q1' },
				{ id: 'q2', label: 'Q2' },
				{ id: 'q4', label: 'Q4' },
			],
		},
		dollyEnvironmentList: ['q1', 'q2', 'q4'],
		loading: false,
		error: undefined,
	}),
}))

vi.mock('@/components/miljoVelger/MiljoeInfo', () => ({
	MiljoeInfo: () => null,
	filterMiljoe: (_dollyMiljoe: any, utvalgteMiljoer: string[] | undefined) => utvalgteMiljoer || [],
}))

const renderComponent = (props?: Partial<React.ComponentProps<typeof GjenopprettModal>>) => {
	const submitForm = vi.fn()
	const closeModal = vi.fn()
	const gjenopprettHeader = <div>Header</div>
	const mergedProps: React.ComponentProps<typeof GjenopprettModal> = {
		gjenopprettHeader,
		environments: props?.environments,
		submitForm: submitForm,
		closeModal: closeModal,
		bestilling: props?.bestilling,
		brukertype: props?.brukertype,
	}
	const view = render(<GjenopprettModal {...mergedProps} />)
	return { submitForm, closeModal, view }
}

dollyTest('selects environment and includes it in submitted form data', async () => {
	const user = userEvent.setup()
	const { submitForm } = renderComponent()
	await screen.findByText('Velg miljø å gjenopprette i')
	const q1Checkbox = screen.getByRole('checkbox', { name: 'Q1' }) as HTMLInputElement
	await user.click(q1Checkbox)
	await waitFor(() => expect(q1Checkbox.checked).toBe(true))
	const submitButton = screen.getByRole('button', { name: 'Utfør' })
	await user.click(submitButton)
	await waitFor(() => {
		expect(submitForm).toHaveBeenCalledTimes(1)
		const submitted = submitForm.mock.calls[0][0]
		expect(submitted.environments).toContain('q1')
	})
})

dollyTest('preloads provided environments and keeps them selected', async () => {
	const user = userEvent.setup()
	const { submitForm } = renderComponent({ environments: ['q2'] })
	await screen.findByText('Velg miljø å gjenopprette i')
	const q2Checkbox = screen.getByRole('checkbox', { name: 'Q2' }) as HTMLInputElement
	await waitFor(() => expect(q2Checkbox.checked).toBe(true))
	const submitButton = screen.getByRole('button', { name: 'Utfør' })
	await user.click(submitButton)
	await waitFor(() => {
		const submitted = submitForm.mock.calls[0][0]
		expect(submitted.environments).toContain('q2')
	})
})

dollyTest('deselects previously selected environment', async () => {
	const user = userEvent.setup()
	renderComponent()
	await screen.findByText('Velg miljø å gjenopprette i')
	const q1Checkbox = screen.getByRole('checkbox', { name: 'Q1' }) as HTMLInputElement
	await user.click(q1Checkbox)
	await waitFor(() => expect(q1Checkbox.checked).toBe(true))
	await user.click(q1Checkbox)
	await waitFor(() => expect(q1Checkbox.checked).toBe(false))
})

dollyTest('should reflect selected environment visually when clicking label text', async () => {
	const user = userEvent.setup()
	const { submitForm } = renderComponent()
	await screen.findByText('Velg miljø å gjenopprette i')
	await user.click(screen.getByText('Q1'))
	const q1Checkbox = screen.getByRole('checkbox', { name: 'Q1' }) as HTMLInputElement
	await waitFor(() => expect(q1Checkbox.checked).toBe(true))
	await user.click(screen.getByRole('button', { name: 'Utfør' }))
	await waitFor(() => {
		const submitted = submitForm.mock.calls[0][0]
		expect(submitted.environments).toContain('q1')
	})
})

dollyTest('raw input should have checked=true after selecting environment', async () => {
	const user = userEvent.setup()
	renderComponent()
	await screen.findByText('Velg miljø å gjenopprette i')
	await user.click(screen.getByText('Q1'))
	const rawInput = document.getElementById('q1') as HTMLInputElement
	await waitFor(() => expect(rawInput.checked).toBe(true))
})
