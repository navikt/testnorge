import { describe, expect, it, vi, afterEach, beforeEach } from 'vitest'
import { render } from '@testing-library/react'
import { NewVersionToast, BESTILLING_SAVE_EVENT } from '@/components/versionBanner/NewVersionBanner'
import React from 'react'
import { toast } from 'react-toastify'
import { MemoryRouter } from 'react-router'

vi.mock('@/utils/hooks/useVersionCheck', () => ({
	useVersionCheck: vi.fn(),
}))

vi.mock('react-toastify', () => ({
	toast: {
		info: vi.fn(),
		update: vi.fn(),
		dismiss: vi.fn(),
	},
	ToastContainer: () => null,
}))

import { useVersionCheck } from '@/utils/hooks/useVersionCheck'

const mockedUseVersionCheck = vi.mocked(useVersionCheck)
const mockedToast = vi.mocked(toast)

describe('NewVersionToast', () => {
	afterEach(() => {
		vi.clearAllMocks()
	})

	it('should not show toast when no new version is available', () => {
		mockedUseVersionCheck.mockReturnValue({ isNewVersionAvailable: false })

		render(
			<MemoryRouter initialEntries={['/gruppe/1/bestilling']}>
				<NewVersionToast />
			</MemoryRouter>,
		)

		expect(mockedToast.info).not.toHaveBeenCalled()
	})

	it('should show toast when new version is available', () => {
		mockedUseVersionCheck.mockReturnValue({ isNewVersionAvailable: true })

		render(
			<MemoryRouter initialEntries={['/']}>
				<NewVersionToast />
			</MemoryRouter>,
		)

		expect(mockedToast.info).toHaveBeenCalledWith(
			expect.anything(),
			expect.objectContaining({
				toastId: 'dolly-new-version',
				autoClose: false,
				closeOnClick: true,
				pauseOnHover: true,
				draggable: true,
				position: 'bottom-right',
			}),
		)
	})

	it('should create toast with onReload handler that calls reload', () => {
		mockedUseVersionCheck.mockReturnValue({ isNewVersionAvailable: true })

		render(
			<MemoryRouter initialEntries={['/']}>
				<NewVersionToast />
			</MemoryRouter>,
		)

		const toastCall = mockedToast.info.mock.calls[0]
		const toastContent = toastCall[0] as React.ReactElement
		expect((toastContent as any).props.onReload).toBeTypeOf('function')
	})

	it('should show save message when on bestilling path', () => {
		mockedUseVersionCheck.mockReturnValue({ isNewVersionAvailable: true })

		render(
			<MemoryRouter initialEntries={['/gruppe/1/bestilling']}>
				<NewVersionToast />
			</MemoryRouter>,
		)

		const toastCall = mockedToast.info.mock.calls[0]
		const toastContent = toastCall[0] as React.ReactElement
		expect((toastContent as any).props.showSaveMessage).toBe(true)
	})

	it('should not show save message on non-bestilling path', () => {
		mockedUseVersionCheck.mockReturnValue({ isNewVersionAvailable: true })

		render(
			<MemoryRouter initialEntries={['/']}>
				<NewVersionToast />
			</MemoryRouter>,
		)

		const toastCall = mockedToast.info.mock.calls[0]
		const toastContent = toastCall[0] as React.ReactElement
		expect((toastContent as any).props.showSaveMessage).toBe(false)
	})
})
