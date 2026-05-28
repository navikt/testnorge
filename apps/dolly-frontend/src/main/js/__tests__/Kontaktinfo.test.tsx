import { describe, expect, it, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { Kontaktinfo } from '@/components/feedback/Kontaktinfo'
import { KontaktinfoPanel } from '@/components/feedback/KontaktinfoPanel'
import { useCurrentBruker } from '@/utils/hooks/useBruker'

vi.mock('@/utils/hooks/useBruker', () => ({
	useCurrentBruker: vi.fn(() => ({
		currentBruker: { brukertype: 'AZURE' },
	})),
	useBrukerProfil: vi.fn(() => ({ brukerProfil: null })),
	useBrukerProfilBilde: vi.fn(() => ({ brukerBilde: null })),
}))

vi.mock('@/components/ui/icon/Icon', () => ({
	default: ({ kind, ...props }: any) => <span data-testid={`icon-${kind}`} {...props} />,
}))

vi.mock('@/components/ui/appError/ErrorBoundary', () => ({
	ErrorBoundary: ({ children }: any) => <>{children}</>,
}))

vi.mock('@/components/feedback/KontaktModal', () => ({
	KontaktModal: ({ closeModal }: any) => (
		<div data-testid="kontakt-modal">
			<button onClick={closeModal}>Lukk modal</button>
		</div>
	),
}))

const mockedUseCurrentBruker = vi.mocked(useCurrentBruker)

describe('Kontaktinfo', () => {
	it('should open popover when button is clicked', async () => {
		const user = userEvent.setup()

		render(<Kontaktinfo />)

		const button = screen.getByTitle('Kontakt oss')
		await user.click(button)

		expect(screen.getByText('Kontakt oss', { selector: 'h2' })).toBeInTheDocument()
	})

	it('should open KontaktModal when kontaktskjema button is clicked', async () => {
		const user = userEvent.setup()

		render(<Kontaktinfo />)

		const button = screen.getByTitle('Kontakt oss')
		await user.click(button)

		const kontaktskjemaButton = screen.getByText('Åpne kontaktskjema')
		await user.click(kontaktskjemaButton)

		expect(screen.getByTestId('kontakt-modal')).toBeInTheDocument()
	})
})

describe('KontaktinfoPanel', () => {
	it('should render Slack link for internal NAV user', () => {
		mockedUseCurrentBruker.mockReturnValue({
			currentBruker: { brukertype: 'AZURE' },
		} as any)

		const setOpenState = vi.fn()
		const openKontaktskjema = vi.fn()

		render(<KontaktinfoPanel setOpenState={setOpenState} openKontaktskjema={openKontaktskjema} />)

		const slackLink = screen.getByText('Gå til Slack-kanal').closest('a')
		expect(slackLink).toHaveAttribute('href', 'https://nav-it.slack.com/archives/CA3P9NGA2')
		expect(slackLink).toHaveAttribute('target', '_blank')
	})

	it('should render Slack link for BankID user', () => {
		mockedUseCurrentBruker.mockReturnValue({
			currentBruker: { brukertype: 'BANKID' },
		} as any)

		const setOpenState = vi.fn()
		const openKontaktskjema = vi.fn()

		render(<KontaktinfoPanel setOpenState={setOpenState} openKontaktskjema={openKontaktskjema} />)

		const slackLink = screen.getByText('Gå til Slack-kanal').closest('a')
		expect(slackLink).toHaveAttribute(
			'href',
			'https://offentlig-paas-no.slack.com/archives/C0B2BAXKJKV',
		)
	})

	it('should render email link with correct mailto href', () => {
		mockedUseCurrentBruker.mockReturnValue({
			currentBruker: { brukertype: 'AZURE' },
		} as any)

		const setOpenState = vi.fn()
		const openKontaktskjema = vi.fn()

		render(<KontaktinfoPanel setOpenState={setOpenState} openKontaktskjema={openKontaktskjema} />)

		const emailLink = screen.getByText('Send e-post').closest('a')
		expect(emailLink).toHaveAttribute('href', 'mailto:dolly@nav.no')
	})

	it('should call openKontaktskjema when kontaktskjema button is clicked', async () => {
		mockedUseCurrentBruker.mockReturnValue({
			currentBruker: { brukertype: 'AZURE' },
		} as any)

		const user = userEvent.setup()
		const setOpenState = vi.fn()
		const openKontaktskjema = vi.fn()

		render(<KontaktinfoPanel setOpenState={setOpenState} openKontaktskjema={openKontaktskjema} />)

		const kontaktskjemaButton = screen.getByText('Åpne kontaktskjema')
		await user.click(kontaktskjemaButton)

		expect(openKontaktskjema).toHaveBeenCalled()
	})

	it('should call setOpenState(false) when close button is clicked', async () => {
		mockedUseCurrentBruker.mockReturnValue({
			currentBruker: { brukertype: 'AZURE' },
		} as any)

		const user = userEvent.setup()
		const setOpenState = vi.fn()
		const openKontaktskjema = vi.fn()

		render(<KontaktinfoPanel setOpenState={setOpenState} openKontaktskjema={openKontaktskjema} />)

		const closeButton = screen.getByRole('button', { name: /lukk/i })
		await user.click(closeButton)

		expect(setOpenState).toHaveBeenCalledWith(false)
	})
})
