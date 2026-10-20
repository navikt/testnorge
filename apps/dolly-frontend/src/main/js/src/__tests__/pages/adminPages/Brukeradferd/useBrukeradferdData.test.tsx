import { act, renderHook } from '@testing-library/react'
import { vi } from 'vitest'
import { useDashboardBrukeradferdForMonths, useDashboardOversikt } from '@/utils/hooks/useDashboard'
import { useBrukeradferdData } from '@/pages/adminPages/Brukeradferd/useBrukeradferdData'

vi.mock('@/utils/hooks/useDashboard', () => ({
	useDashboardOversikt: vi.fn(),
	useDashboardBrukeradferdForMonths: vi.fn(),
}))

const OVERSIKT = [
	{ aar: 2025, maaned: '11', totaltAntallPersoner: 1, nye: 1, gjenopprettede: 0 },
	{ aar: 2026, maaned: '02', totaltAntallPersoner: 1, nye: 1, gjenopprettede: 0 },
	{ aar: 2026, maaned: '09', totaltAntallPersoner: 1, nye: 1, gjenopprettede: 0 },
]

const sisteMaaneder = () =>
	(vi.mocked(useDashboardBrukeradferdForMonths).mock.lastCall?.[0] ?? []).map(
		(maaned) => `${maaned.year}-${maaned.month}`,
	)

describe('useBrukeradferdData', () => {
	beforeEach(() => {
		vi.useFakeTimers({ toFake: ['Date'] })
		vi.setSystemTime(new Date(2026, 9, 13))
		vi.mocked(useDashboardOversikt).mockReturnValue({
			dashboardOversikt: OVERSIKT,
			loadingDashboardOversikt: false,
			dashboardOversiktError: undefined,
		} as unknown as ReturnType<typeof useDashboardOversikt>)
		vi.mocked(useDashboardBrukeradferdForMonths).mockReturnValue({
			brukeradferd: [],
			loadingBrukeradferd: false,
			brukeradferdError: undefined,
		} as unknown as ReturnType<typeof useDashboardBrukeradferdForMonths>)
	})

	afterEach(() => {
		vi.useRealTimers()
	})

	it('should let the year view fetch every month in the year and switch back on month selection', () => {
		const { result } = renderHook(() => useBrukeradferdData())

		expect(result.current.visning).toBe('maaned')
		expect(result.current.periodeTittel).toBe('i sep. 2026')
		expect(sisteMaaneder()).toEqual(['2026-SEPTEMBER'])

		act(() => result.current.onVisningChange('aar'))

		expect(result.current.periodeTittel).toBe('hittil i 2026')
		expect(result.current.granularitet).toBe('MAANED')
		expect(sisteMaaneder()).toEqual(['2026-FEBRUARY', '2026-SEPTEMBER'])

		act(() => result.current.onYearChange('2025'))

		expect(result.current.visning).toBe('aar')
		expect(result.current.periodeTittel).toBe('i 2025')
		expect(sisteMaaneder()).toEqual(['2025-NOVEMBER'])

		act(() => result.current.onIntervalChange('2026-02'))

		expect(result.current.visning).toBe('maaned')
		expect(result.current.selectedInterval).toBe('2026-02')
		expect(result.current.periodeTittel).toBe('i feb. 2026')
		expect(sisteMaaneder()).toEqual(['2026-FEBRUARY'])
	})
})
