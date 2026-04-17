import { describe, expect, it, vi, beforeEach } from 'vitest'
import { renderHook, waitFor } from '@testing-library/react'
import React from 'react'
import { SWRConfig } from 'swr'
import { useOrganisasjonForvalter } from '@/utils/hooks/useDollyOrganisasjoner'

const mockFetcher = vi.fn()

vi.mock('@/api', () => ({
	fetcher: (...args: any[]) => mockFetcher(...args),
	multiFetcherAareg: vi.fn(),
}))

const swrWrapper = ({ children }: { children: React.ReactNode }) => (
	<SWRConfig value={{ provider: () => new Map(), dedupingInterval: 0 }}>{children}</SWRConfig>
)

const makeFluxResponse = (miljoer: Record<string, object>[]) => miljoer

describe('useOrganisasjonForvalter', () => {
	beforeEach(() => {
		vi.clearAllMocks()
	})

	it('shouldNormalizeFluxArrayResponse', async () => {
		const orgData = {
			organisasjonsnummer: '123456789',
			organisasjonsnavn: 'Test AS',
			enhetstype: 'BEDR',
		}
		const fluxResponse = makeFluxResponse([{ q1: orgData }, { q2: orgData }])

		mockFetcher.mockResolvedValue(fluxResponse)

		const { result } = renderHook(() => useOrganisasjonForvalter(['123456789']), {
			wrapper: swrWrapper,
		})

		await waitFor(() => {
			expect(result.current.loading).toBe(false)
			expect(result.current.organisasjoner).toHaveLength(1)
		})

		const org = result.current.organisasjoner[0]
		expect(org.q1?.organisasjonsnummer).toBe('123456789')
		expect(org.q2?.organisasjonsnummer).toBe('123456789')
	})

	it('shouldHandleSingleEnvironmentFluxResponse', async () => {
		const orgData = {
			organisasjonsnummer: '123456789',
			organisasjonsnavn: 'Test AS',
			enhetstype: 'BEDR',
		}
		const fluxResponse = makeFluxResponse([{ q1: orgData }])

		mockFetcher.mockResolvedValue(fluxResponse)

		const { result } = renderHook(() => useOrganisasjonForvalter(['123456789']), {
			wrapper: swrWrapper,
		})

		await waitFor(() => {
			expect(result.current.loading).toBe(false)
			expect(result.current.organisasjoner).toHaveLength(1)
		})

		expect(result.current.organisasjoner[0].q1?.organisasjonsnummer).toBe('123456789')
	})

	it('shouldReturnEmptyArrayWhenOrgNotFound', async () => {
		mockFetcher.mockResolvedValue([])

		const { result } = renderHook(() => useOrganisasjonForvalter(['123456789']), {
			wrapper: swrWrapper,
		})

		await waitFor(() => {
			expect(result.current.loading).toBe(false)
		})

		expect(result.current.organisasjoner).toHaveLength(0)
	})

	it('shouldReturnHasBeenCalledTrueAfterFetch', async () => {
		mockFetcher.mockResolvedValue([])

		const { result } = renderHook(() => useOrganisasjonForvalter(['123456789']), {
			wrapper: swrWrapper,
		})

		await waitFor(() => {
			expect(result.current.hasBeenCalled).toBe(true)
		})
	})

	it('shouldNotFetchForOrgnummerShorterThanNineDigits', () => {
		const { result } = renderHook(() => useOrganisasjonForvalter(['12345']), {
			wrapper: swrWrapper,
		})

		expect(mockFetcher).not.toHaveBeenCalled()
		expect(result.current.hasBeenCalled).toBe(false)
		expect(result.current.organisasjoner).toHaveLength(0)
	})

	it('shouldNotFetchForEmptyArray', () => {
		const { result } = renderHook(() => useOrganisasjonForvalter([]), {
			wrapper: swrWrapper,
		})

		expect(mockFetcher).not.toHaveBeenCalled()
		expect(result.current.hasBeenCalled).toBe(false)
	})

	it('shouldHandleLegacyObjectResponse', async () => {
		const orgData = {
			organisasjonsnummer: '123456789',
			organisasjonsnavn: 'Test AS',
			enhetstype: 'BEDR',
		}
		const legacyResponse = { q1: orgData, q2: orgData }

		mockFetcher.mockResolvedValue(legacyResponse)

		const { result } = renderHook(() => useOrganisasjonForvalter(['123456789']), {
			wrapper: swrWrapper,
		})

		await waitFor(() => {
			expect(result.current.loading).toBe(false)
			expect(result.current.organisasjoner).toHaveLength(1)
		})

		expect(result.current.organisasjoner[0].q1?.organisasjonsnummer).toBe('123456789')
	})
})
