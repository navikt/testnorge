import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { beforeEach, describe, expect, it, vi } from 'vitest'
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

describe('BrukerModal', () => {
	beforeEach(() => {
		vi.clearAllMocks()
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
