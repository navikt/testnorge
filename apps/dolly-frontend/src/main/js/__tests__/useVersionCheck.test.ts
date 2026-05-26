import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest'
import { renderHook, waitFor, act } from '@testing-library/react'
import { useVersionCheck } from '@/utils/hooks/useVersionCheck'

describe('useVersionCheck', () => {
	let fetchSpy: ReturnType<typeof vi.fn>
	let originalFetch: typeof globalThis.fetch

	beforeEach(() => {
		vi.useFakeTimers({ shouldAdvanceTime: true })
		originalFetch = globalThis.fetch
		fetchSpy = vi.fn()
		globalThis.fetch = fetchSpy
	})

	afterEach(() => {
		vi.useRealTimers()
		globalThis.fetch = originalFetch
		vi.restoreAllMocks()
	})

	it('should not detect new version when commit hash matches', async () => {
		fetchSpy.mockResolvedValue({
			ok: true,
			json: () => Promise.resolve({ commitHash: 'abc1234' }),
		})

		const { result } = renderHook(() => useVersionCheck('abc1234'))

		await waitFor(() => {
			expect(fetchSpy).toHaveBeenCalled()
		})

		expect(result.current.isNewVersionAvailable).toBe(false)
	})

	it('should detect new version when commit hash differs', async () => {
		fetchSpy.mockResolvedValue({
			ok: true,
			json: () => Promise.resolve({ commitHash: 'def5678' }),
		})

		const { result } = renderHook(() => useVersionCheck('abc1234'))

		await waitFor(() => {
			expect(result.current.isNewVersionAvailable).toBe(true)
		})
	})

	it('should not fail if fetch errors', async () => {
		fetchSpy.mockRejectedValue(new Error('Network error'))

		const { result } = renderHook(() => useVersionCheck('abc1234'))

		await waitFor(() => {
			expect(fetchSpy).toHaveBeenCalled()
		})

		expect(result.current.isNewVersionAvailable).toBe(false)
	})

	it('should not fail if response is not ok', async () => {
		fetchSpy.mockResolvedValue({ ok: false })

		const { result } = renderHook(() => useVersionCheck('abc1234'))

		await waitFor(() => {
			expect(fetchSpy).toHaveBeenCalled()
		})

		expect(result.current.isNewVersionAvailable).toBe(false)
	})

	it('should not fetch when commit hash is empty', async () => {
		const { result } = renderHook(() => useVersionCheck(''))

		await act(async () => {
			vi.advanceTimersByTime(100)
		})

		expect(fetchSpy).not.toHaveBeenCalled()
		expect(result.current.isNewVersionAvailable).toBe(false)
	})

	it('should poll at 2 minute intervals', async () => {
		fetchSpy.mockResolvedValue({
			ok: true,
			json: () => Promise.resolve({ commitHash: 'abc1234' }),
		})

		renderHook(() => useVersionCheck('abc1234'))

		await waitFor(() => {
			expect(fetchSpy).toHaveBeenCalledTimes(1)
		})

		await act(async () => {
			vi.advanceTimersByTime(2 * 60_000)
		})

		await waitFor(() => {
			expect(fetchSpy).toHaveBeenCalledTimes(2)
		})
	})

	it('should stop polling once new version is detected', async () => {
		fetchSpy.mockResolvedValue({
			ok: true,
			json: () => Promise.resolve({ commitHash: 'def5678' }),
		})

		const { result } = renderHook(() => useVersionCheck('abc1234'))

		await waitFor(() => {
			expect(result.current.isNewVersionAvailable).toBe(true)
		})

		const callCount = fetchSpy.mock.calls.length

		await act(async () => {
			vi.advanceTimersByTime(120_000)
		})

		expect(fetchSpy).toHaveBeenCalledTimes(callCount)
	})

	it('should fetch with cache-busting query parameter', async () => {
		fetchSpy.mockResolvedValue({
			ok: true,
			json: () => Promise.resolve({ commitHash: 'abc1234' }),
		})

		renderHook(() => useVersionCheck('abc1234'))

		await waitFor(() => {
			expect(fetchSpy).toHaveBeenCalled()
		})

		const url = fetchSpy.mock.calls[0][0] as string
		expect(url).toMatch(/^\/version\.json\?_t=\d+$/)
	})

	it('should fetch with no-store cache control', async () => {
		fetchSpy.mockResolvedValue({
			ok: true,
			json: () => Promise.resolve({ commitHash: 'abc1234' }),
		})

		renderHook(() => useVersionCheck('abc1234'))

		await waitFor(() => {
			expect(fetchSpy).toHaveBeenCalled()
		})

		const options = fetchSpy.mock.calls[0][1] as RequestInit
		expect(options.cache).toBe('no-store')
	})

	it('should latch to true and not flip back', async () => {
		fetchSpy.mockResolvedValue({
			ok: true,
			json: () => Promise.resolve({ commitHash: 'def5678' }),
		})

		const { result } = renderHook(() => useVersionCheck('abc1234'))

		await waitFor(() => {
			expect(result.current.isNewVersionAvailable).toBe(true)
		})

		expect(result.current.isNewVersionAvailable).toBe(true)
	})
})
