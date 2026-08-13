import React from 'react'
import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { ArbeidssoekerregisteretStopp } from '@/components/fagsystem/arbeidssoekerregisteret/visning/ArbeidssoekerregisteretStopp'
import { ArbeidssoekerregisteretApi } from '@/service/Api'

vi.mock('@/service/Api', () => ({
	ArbeidssoekerregisteretApi: {
		stoppRegistrering: vi.fn(),
	},
}))

const mockStoppArbeidssoekerregisteret = vi.mocked(ArbeidssoekerregisteretApi.stoppRegistrering)

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
			screen.getByRole('alertdialog', { name: 'Stopp arbeidssøkerregistrering' }),
		).toBeInTheDocument()
		expect(
			screen.getByRole('alertdialog', { name: 'Stopp arbeidssøkerregistrering' }),
		).toHaveAttribute('data-position', 'center')
		expect(
			screen.getByRole('alertdialog', { name: 'Stopp arbeidssøkerregistrering' }),
		).toHaveAttribute('data-size', 'medium')
		const dialogTitle = screen.getByRole('heading', {
			name: 'Stopp arbeidssøkerregistrering',
		})
		expect(dialogTitle.tagName).toBe('H2')
		expect(
			screen.getByRole('alertdialog', { name: 'Stopp arbeidssøkerregistrering' }),
		).toHaveAttribute('aria-labelledby', dialogTitle.id)
		expect(getComputedStyle(dialogTitle).fontSize).not.toBe('40px')
		expect(getComputedStyle(dialogTitle).marginTop).toBe('0px')
		expect(
			screen
				.getByText('Er du sikker på at du vil stoppe arbeidssøkerregistreringen?')
				.closest('.aksel-dialog__body'),
		).not.toBeNull()

		fireEvent.click(screen.getByRole('button', { name: 'Ja, stopp registreringen' }))

		await waitFor(() =>
			expect(mockStoppArbeidssoekerregisteret).toHaveBeenCalledWith('12345678901'),
		)
		await waitFor(() => expect(onStopped).toHaveBeenCalledTimes(1))
		await waitFor(() => expect(screen.queryByRole('alertdialog')).not.toBeInTheDocument())
	})

	it('keeps the modal open when stopping fails', async () => {
		const onStopped = vi.fn()
		mockStoppArbeidssoekerregisteret.mockRejectedValue(new Error('Stopp feilet'))

		render(<ArbeidssoekerregisteretStopp ident="12345678901" onStopped={onStopped} />)

		fireEvent.click(screen.getByRole('button', { name: 'Stopp' }))
		fireEvent.click(screen.getByRole('button', { name: 'Ja, stopp registreringen' }))

		expect(await screen.findByText('Stopp feilet')).toBeInTheDocument()
		expect(
			screen.getByRole('alertdialog', { name: 'Stopp arbeidssøkerregistrering' }),
		).toBeInTheDocument()
		expect(onStopped).not.toHaveBeenCalled()
	})
})
