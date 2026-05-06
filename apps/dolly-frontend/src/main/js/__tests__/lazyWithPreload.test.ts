import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest'
import { retryImport } from '@/utils/lazyWithPreload'

vi.mock('@/utils/chunkErrorUtils', () => ({
	isChunkLoadError: vi.fn(() => false),
	handleChunkErrorWithReload: vi.fn(),
}))

describe('retryImport', () => {
	beforeEach(() => {
		vi.useFakeTimers({ shouldAdvanceTime: true })
	})

	afterEach(() => {
		vi.useRealTimers()
		vi.restoreAllMocks()
	})

	it('should resolve on first successful import', async () => {
		const module = { default: () => null }
		const factory = vi.fn().mockResolvedValue(module)

		const result = await retryImport(factory)

		expect(result).toBe(module)
		expect(factory).toHaveBeenCalledTimes(1)
	})

	it('should retry on transient failure and succeed', async () => {
		const module = { default: () => null }
		const factory = vi
			.fn()
			.mockRejectedValueOnce(new Error('Network error'))
			.mockResolvedValueOnce(module)

		const resultPromise = retryImport(factory, 2, 1)

		vi.advanceTimersByTime(10)
		const result = await resultPromise

		expect(result).toBe(module)
		expect(factory).toHaveBeenCalledTimes(2)
	})

	it('should throw after exhausting retries', async () => {
		const factory = vi.fn().mockRejectedValue(new Error('permanent failure'))

		const resultPromise = retryImport(factory, 2, 1)

		vi.advanceTimersByTime(100)

		await expect(resultPromise).rejects.toThrow('permanent failure')
		expect(factory).toHaveBeenCalledTimes(3)
	})

	it('should retry with exponential backoff', async () => {
		const module = { default: () => null }
		const factory = vi
			.fn()
			.mockRejectedValueOnce(new Error('fail 1'))
			.mockRejectedValueOnce(new Error('fail 2'))
			.mockResolvedValueOnce(module)

		const resultPromise = retryImport(factory, 2, 100)

		vi.advanceTimersByTime(100)
		await vi.advanceTimersByTimeAsync(200)

		const result = await resultPromise

		expect(result).toBe(module)
		expect(factory).toHaveBeenCalledTimes(3)
	})
})
