import React from 'react'
import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { ArbeidssoekerregisteretStopp } from '@/components/fagsystem/arbeidssoekerregisteret/visning/ArbeidssoekerregisteretStopp'
import { DollyApi } from '@/service/Api'

vi.mock('@/service/Api', () => ({
	DollyApi: {
		stoppArbeidssoekerregisteret: vi.fn(),
	},
}))

vi.mock('@navikt/ds-react', async () => {
	const actual = await vi.importActual<typeof import('@navikt/ds-react')>('@navikt/ds-react')
	const MockModal = ({ open, header, children }: any) =>
		open ? (
			<div role="dialog" aria-label={header?.heading}>
				<h2>{header?.heading}</h2>
				{children}
			</div>
		) : null
	MockModal.Body = ({ children }: any) => <div>{children}</div>
	MockModal.Footer = ({ children }: any) => <div>{children}</div>
	return {
		...actual,
		Modal: MockModal,
	}
})

const mockStoppArbeidssoekerregisteret = vi.mocked(DollyApi.stoppArbeidssoekerregisteret)

describe('ArbeidssoekerregisteretStopp', () => {
	beforeEach(() => {
		vi.clearAllMocks()
		mockStoppArbeidssoekerregisteret.mockResolvedValue(new Response())
	})

	it('stops registration and notifies the view', async () => {
		const onStopped = vi.fn()

		render(<ArbeidssoekerregisteretStopp ident="12345678901" onStopped={onStopped} />)

		const stoppButton = screen.getByRole('button', { name: 'Stopp' })
		expect(stoppButton).toHaveClass('aksel-button--xsmall')

		fireEvent.click(stoppButton)
		expect(
			screen.getByRole('dialog', { name: 'Stopp arbeidssøkerregistrering' }),
		).toBeInTheDocument()

		fireEvent.click(screen.getByRole('button', { name: 'Ja, stopp registreringen' }))

		await waitFor(() =>
			expect(mockStoppArbeidssoekerregisteret).toHaveBeenCalledWith('12345678901'),
		)
		await waitFor(() => expect(onStopped).toHaveBeenCalledTimes(1))
		await waitFor(() => expect(screen.queryByRole('dialog')).not.toBeInTheDocument())
	})

	it('keeps the modal open when stopping fails', async () => {
		const onStopped = vi.fn()
		mockStoppArbeidssoekerregisteret.mockRejectedValue(new Error('Stopp feilet'))

		render(<ArbeidssoekerregisteretStopp ident="12345678901" onStopped={onStopped} />)

		fireEvent.click(screen.getByRole('button', { name: 'Stopp' }))
		fireEvent.click(screen.getByRole('button', { name: 'Ja, stopp registreringen' }))

		expect(await screen.findByText('Stopp feilet')).toBeInTheDocument()
		expect(
			screen.getByRole('dialog', { name: 'Stopp arbeidssøkerregistrering' }),
		).toBeInTheDocument()
		expect(onStopped).not.toHaveBeenCalled()
	})
})
