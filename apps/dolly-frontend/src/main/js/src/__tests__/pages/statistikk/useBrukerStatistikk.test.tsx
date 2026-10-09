import { renderHook } from '@testing-library/react'
import useSWR from 'swr'
import { vi } from 'vitest'
import DollyEndpoints from '@/service/services/dolly/DollyEndpoints'
import {
	useBrukerBestillingerDetaljert,
	useBrukerBestillingerOversikt,
} from '@/utils/hooks/useBrukerStatistikk'

vi.mock('swr', () => ({
	default: vi.fn(() => ({ data: undefined, isLoading: false, error: undefined })),
}))

const sisteSwrKall = () => vi.mocked(useSWR).mock.lastCall ?? []

describe('useBrukerStatistikk', () => {
	beforeEach(() => {
		vi.mocked(useSWR).mockClear()
	})

	it('should include the effective owner in the overview cache key', () => {
		renderHook(() => useBrukerBestillingerOversikt('team-bruker'))

		const [key, , options] = sisteSwrKall()
		expect(key).toEqual([DollyEndpoints.brukerBestillinger(), 'team-bruker'])
		expect(options).toEqual({ revalidateOnFocus: false })
	})

	it('should include the effective owner in the detail cache key', () => {
		renderHook(() => useBrukerBestillingerDetaljert('team-bruker', 2026, 'AUGUST'))

		expect(sisteSwrKall()[0]).toEqual([
			DollyEndpoints.brukerBestillingerDetaljert(2026, 'AUGUST'),
			'team-bruker',
		])
	})

	it('should not fetch before the owner is known', () => {
		renderHook(() => useBrukerBestillingerOversikt(undefined))
		expect(sisteSwrKall()[0]).toBeNull()

		renderHook(() => useBrukerBestillingerDetaljert(undefined, 2026, 'AUGUST'))
		expect(sisteSwrKall()[0]).toBeNull()
	})
})
