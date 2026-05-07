import { describe, expect, it, vi, afterEach } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { NewVersionBanner, BESTILLING_SAVE_EVENT } from '@/components/versionBanner/NewVersionBanner'
import React from 'react'
import { MemoryRouter } from 'react-router'

vi.mock('@/utils/hooks/useVersionCheck', () => ({
	useVersionCheck: vi.fn(),
}))

vi.mock('@navikt/ds-react', async () => {
	const actual = await vi.importActual<Record<string, unknown>>('@navikt/ds-react')
	const MockGlobalAlert = ({ children, status }: any) => (
		<div data-testid="global-alert" data-status={status}>{children}</div>
	)
	MockGlobalAlert.Header = ({ children }: any) => <div>{children}</div>
	MockGlobalAlert.Title = ({ children }: any) => <h2>{children}</h2>
	MockGlobalAlert.Content = ({ children }: any) => <div>{children}</div>
	MockGlobalAlert.CloseButton = ({ onClick }: any) => (
		<button data-testid="close-alert" onClick={onClick}>Close</button>
	)
	return {
		...actual,
		GlobalAlert: MockGlobalAlert,
		Button: ({ children, onClick }: any) => <button onClick={onClick}>{children}</button>,
	}
})

import { useVersionCheck } from '@/utils/hooks/useVersionCheck'

const mockedUseVersionCheck = vi.mocked(useVersionCheck)

describe('NewVersionBanner', () => {
	afterEach(() => {
		vi.clearAllMocks()
	})

	it('should not render when no new version is available', () => {
		mockedUseVersionCheck.mockReturnValue({ isNewVersionAvailable: false })

		const { container } = render(
			<MemoryRouter initialEntries={['/']}>
				<NewVersionBanner />
			</MemoryRouter>,
		)

		expect(container.innerHTML).toBe('')
	})

	it('should render alert when new version is available', () => {
		mockedUseVersionCheck.mockReturnValue({ isNewVersionAvailable: true })

		render(
			<MemoryRouter initialEntries={['/']}>
				<NewVersionBanner />
			</MemoryRouter>,
		)

		expect(screen.getByTestId('global-alert')).toBeDefined()
		expect(screen.getByText('Ny versjon tilgjengelig')).toBeDefined()
		expect(screen.getByText('Oppdater nå')).toBeDefined()
	})

	it('should show save message when on bestilling path', () => {
		mockedUseVersionCheck.mockReturnValue({ isNewVersionAvailable: true })

		render(
			<MemoryRouter initialEntries={['/gruppe/1/bestilling']}>
				<NewVersionBanner />
			</MemoryRouter>,
		)

		expect(
			screen.getByText(/Alle endringene dine vil bli lagret før siden lastes inn igjen/),
		).toBeDefined()
	})

	it('should not show save message on non-bestilling path', () => {
		mockedUseVersionCheck.mockReturnValue({ isNewVersionAvailable: true })

		render(
			<MemoryRouter initialEntries={['/']}>
				<NewVersionBanner />
			</MemoryRouter>,
		)

		expect(screen.queryByText(/Alle endringene dine vil bli lagret/)).toBeNull()
	})

	it('should dismiss when close button is clicked', () => {
		mockedUseVersionCheck.mockReturnValue({ isNewVersionAvailable: true })

		render(
			<MemoryRouter initialEntries={['/']}>
				<NewVersionBanner />
			</MemoryRouter>,
		)

		expect(screen.getByTestId('global-alert')).toBeDefined()

		fireEvent.click(screen.getByTestId('close-alert'))

		expect(screen.queryByTestId('global-alert')).toBeNull()
	})
})
