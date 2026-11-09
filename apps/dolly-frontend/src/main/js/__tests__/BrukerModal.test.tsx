import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes, useLocation } from 'react-router'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import BrukerModal from '@/pages/brukerPage/BrukerModal'
import { BrukerApi, PersonOrgTilgangApi, SessionApi } from '@/service/Api'
import { LogoutErrorStates } from '@/components/utlogging/logoutState'
import type { Organisasjon } from '@/pages/brukerPage/types'

const { logoutBrukerMock } = vi.hoisted(() => ({
	logoutBrukerMock: vi.fn(),
}))

vi.mock('@/service/Api')
vi.mock('@/components/utlogging/logoutBruker', () => ({
	default: logoutBrukerMock,
}))
vi.mock('@/logger/Logger', () => ({
	Logger: {
		error: vi.fn(),
		trace: vi.fn(),
	},
}))
vi.mock('@/pages/brukerPage/OrganisasjonVelger', () => ({
	default: ({ onClick }: { onClick: (organisasjon: Organisasjon) => void }) => (
		<button
			onClick={() =>
				onClick({
					navn: 'Testorganisasjon',
					organisasjonsnummer: '123456789',
				})
			}
		>
			Velg organisasjon
		</button>
	),
}))

const BestillingSide = () => {
	const location = useLocation()
	return <p>Bestilling for {location.state?.identtype}</p>
}

const velgOrganisasjonMedEpost = () => {
	vi.mocked(PersonOrgTilgangApi.getOrganisasjoner).mockResolvedValue({
		data: [{ navn: 'Testorganisasjon', organisasjonsnummer: '123456789' }],
	})
	vi.mocked(BrukerApi.getBruker).mockResolvedValue({
		brukernavn: 'testbruker',
		epost: 'test@nav.no',
		organisasjonsnummer: '123456789',
	})
	vi.mocked(SessionApi.addToSession).mockResolvedValue(new Response())
}

const renderMedRouter = () =>
	render(
		<MemoryRouter initialEntries={['/bruker']}>
			<Routes>
				<Route path="/bruker" element={<BrukerModal />} />
				<Route path="/" element={<p>Forsiden</p>} />
				<Route path="/gruppe/:gruppeId/bestilling" element={<BestillingSide />} />
			</Routes>
		</MemoryRouter>,
	)

describe('BrukerModal', () => {
	beforeEach(() => {
		vi.clearAllMocks()
	})

	afterEach(() => {
		sessionStorage.clear()
	})

	it('should go to the front page after session is updated', async () => {
		const user = userEvent.setup()
		velgOrganisasjonMedEpost()

		renderMedRouter()
		await user.click(await screen.findByRole('button', { name: 'Velg organisasjon' }))

		expect(await screen.findByText('Forsiden')).toBeInTheDocument()
	})

	it('should return to a saved bestilling after session is updated', async () => {
		const user = userEvent.setup()
		velgOrganisasjonMedEpost()
		sessionStorage.setItem(
			'dolly-bestilling-saved-form',
			JSON.stringify({
				formValues: {},
				step: 1,
				savedAt: Date.now(),
				path: '/gruppe/5/bestilling',
				locationState: { identtype: 'NPID' },
			}),
		)

		renderMedRouter()
		await user.click(await screen.findByRole('button', { name: 'Velg organisasjon' }))

		expect(await screen.findByText('Bestilling for NPID')).toBeInTheDocument()
	})

	it('should logout with organisation error when no organisations are available', async () => {
		vi.mocked(PersonOrgTilgangApi.getOrganisasjoner).mockResolvedValue({ data: [] })

		render(<BrukerModal />)

		await waitFor(() => {
			expect(logoutBrukerMock).toHaveBeenCalledWith(LogoutErrorStates.ORGANISATION_ERROR)
		})
	})

	it('should logout with person organisation error when organisation lookup fails', async () => {
		vi.mocked(PersonOrgTilgangApi.getOrganisasjoner).mockRejectedValue(new Error('Unavailable'))

		render(<BrukerModal />)

		await waitFor(() => {
			expect(logoutBrukerMock).toHaveBeenCalledWith(LogoutErrorStates.PERSON_ORG_ERROR)
		})
	})

	it('should logout with session error when session creation fails', async () => {
		const user = userEvent.setup()
		vi.mocked(PersonOrgTilgangApi.getOrganisasjoner).mockResolvedValue({
			data: [{ navn: 'Testorganisasjon', organisasjonsnummer: '123456789' }],
		})
		vi.mocked(BrukerApi.getBruker).mockResolvedValue({
			brukernavn: 'testbruker',
			epost: 'test@nav.no',
			organisasjonsnummer: '123456789',
		})
		vi.mocked(SessionApi.addToSession).mockRejectedValue(new Error('Unavailable'))

		render(<BrukerModal />)
		await user.click(await screen.findByRole('button', { name: 'Velg organisasjon' }))

		await waitFor(() => {
			expect(logoutBrukerMock).toHaveBeenCalledWith(LogoutErrorStates.SESSION_ERROR)
		})
	})
})
