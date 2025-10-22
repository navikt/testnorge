import { render, screen, waitFor } from '@testing-library/react'
import { dollyTest } from '../vitest.setup'
import { userEvent } from '@vitest/browser/context'
import React, { act } from 'react'
import { vi } from 'vitest'
import BrukernavnVelger from '@/pages/brukerPage/BrukernavnVelger'
import { BrukerApi } from '@/service/Api'
import { Organisasjon } from '@/pages/brukerPage/types'

vi.mock('@/service/Api')
vi.mock('@/components/utlogging/navigateToLogin')

const mockOrganisasjon: Organisasjon = {
	navn: 'Test Organisasjon',
	organisasjonsnummer: '123456789',
}

const TestComponent = ({
	organisasjon,
	addToSession,
}: {
	organisasjon: Organisasjon
	addToSession: () => void
}) => {
	return <BrukernavnVelger organisasjon={organisasjon} addToSession={addToSession} />
}

dollyTest('renders BrukernavnVelger and handles successful user creation', async () => {
	const addToSessionMock = vi.fn()
	vi.spyOn(BrukerApi, 'opprettBruker').mockResolvedValue({
		brukernavn: 'testbruker123',
		epost: 'test@test.com',
	})

	render(<TestComponent organisasjon={mockOrganisasjon} addToSession={addToSessionMock} />)

	expect(screen.getByText(/Fyll inn eget navn/)).toBeInTheDocument()
	expect(screen.getByText('Test Organisasjon')).toBeInTheDocument()

	const brukernavnInput = screen.getByLabelText('Navn')
	const epostInput = screen.getByLabelText('Epost')
	const submitButton = screen.getByRole('button', { name: 'Gå videre til Dolly' })

	await act(async () => {
		await userEvent.type(brukernavnInput, 'testbruker123')
		await userEvent.type(epostInput, 'test@test.com')
		await userEvent.click(submitButton)
	})

	await waitFor(() => {
		expect(BrukerApi.opprettBruker).toHaveBeenCalledWith(
			'testbruker123',
			'test@test.com',
			'123456789',
		)
	})

	await waitFor(() => {
		expect(addToSessionMock).toHaveBeenCalledWith('123456789')
	})
})

dollyTest('shows validation errors for invalid input', async () => {
	const addToSessionMock = vi.fn()
	render(<TestComponent organisasjon={mockOrganisasjon} addToSession={addToSessionMock} />)

	const submitButton = screen.getByRole('button', { name: 'Gå videre til Dolly' })

	await act(async () => {
		await userEvent.click(submitButton)
	})

	await waitFor(() => {
		expect(screen.getByText('Brukernavn er påkrevd')).toBeInTheDocument()
		expect(screen.getByText('Epost er påkrevd')).toBeInTheDocument()
	})
})

dollyTest('shows validation error for invalid email', async () => {
	const addToSessionMock = vi.fn()
	render(<TestComponent organisasjon={mockOrganisasjon} addToSession={addToSessionMock} />)

	const brukernavnInput = screen.getByLabelText('Navn')
	const epostInput = screen.getByLabelText('Epost')
	const submitButton = screen.getByRole('button', { name: 'Gå videre til Dolly' })

	await act(async () => {
		await userEvent.type(brukernavnInput, 'testbruker123')
		await userEvent.type(epostInput, 'invalid-email')
		await userEvent.click(submitButton)
	})

	await waitFor(() => {
		expect(screen.getByText('Epost må være på gyldig format')).toBeInTheDocument()
	})
})
