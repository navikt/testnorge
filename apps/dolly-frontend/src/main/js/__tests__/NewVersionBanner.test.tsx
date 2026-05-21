import { describe, expect, it, vi, afterEach } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { AppBanner, BESTILLING_SAVE_EVENT } from '@/components/versionBanner/NewVersionBanner'
import React from 'react'
import { MemoryRouter } from 'react-router'

vi.mock('@/utils/hooks/useVersionCheck', () => ({
	useVersionCheck: vi.fn(),
}))

vi.mock('@navikt/aksel-icons', () => {
	return new Proxy(
		{},
		{
			get: (_target, name) => {
				if (typeof name !== 'string' || name === '__esModule') return undefined
				return (props: any) => <svg data-testid={`icon-${name}`} {...props} />
			},
		},
	)
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
				<AppBanner />
			</MemoryRouter>,
		)

		expect(container.innerHTML).toBe('')
	})

	it('should render GlobalAlert without errors when new version is available', () => {
		mockedUseVersionCheck.mockReturnValue({ isNewVersionAvailable: true })

		expect(() =>
			render(
				<MemoryRouter initialEntries={['/']}>
					<AppBanner />
				</MemoryRouter>,
			),
		).not.toThrow()

		expect(screen.getByText('Ny versjon tilgjengelig')).toBeDefined()
		expect(screen.getByText('Oppdater nå')).toBeDefined()
	})

	it('should show save message when on bestilling path', () => {
		mockedUseVersionCheck.mockReturnValue({ isNewVersionAvailable: true })

		render(
			<MemoryRouter initialEntries={['/gruppe/1/bestilling']}>
				<AppBanner />
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
				<AppBanner />
			</MemoryRouter>,
		)

		expect(screen.queryByText(/Alle endringene dine vil bli lagret/)).toBeNull()
	})

	it('should dismiss when close button is clicked', () => {
		mockedUseVersionCheck.mockReturnValue({ isNewVersionAvailable: true })

		render(
			<MemoryRouter initialEntries={['/']}>
				<AppBanner />
			</MemoryRouter>,
		)

		expect(screen.getByText('Ny versjon tilgjengelig')).toBeDefined()

		const closeButton = screen.getByRole('button', { name: /lukk/i })
		fireEvent.click(closeButton)

		expect(screen.queryByText('Ny versjon tilgjengelig')).toBeNull()
	})

	it('should include importer path as bestilling context', () => {
		mockedUseVersionCheck.mockReturnValue({ isNewVersionAvailable: true })

		render(
			<MemoryRouter initialEntries={['/gruppe/1/importer']}>
				<AppBanner />
			</MemoryRouter>,
		)

		expect(
			screen.getByText(/Alle endringene dine vil bli lagret før siden lastes inn igjen/),
		).toBeDefined()
	})
})
