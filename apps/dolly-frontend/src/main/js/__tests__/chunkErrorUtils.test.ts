import { describe, expect, it } from 'vitest'
import { isChunkLoadError } from '@/utils/chunkErrorUtils'

describe('isChunkLoadError', () => {
	it('should detect "Failed to fetch dynamically imported module"', () => {
		const error = new Error('Failed to fetch dynamically imported module: /assets/Steg2-abc123.js')
		expect(isChunkLoadError(error)).toBe(true)
	})

	it('should detect "Unable to preload CSS"', () => {
		const error = new Error('Unable to preload CSS for /assets/style-abc123.css')
		expect(isChunkLoadError(error)).toBe(true)
	})

	it('should detect "Importing a module script failed"', () => {
		const error = new Error('Importing a module script failed.')
		expect(isChunkLoadError(error)).toBe(true)
	})

	it('should detect "error loading dynamically imported module"', () => {
		const error = new Error('error loading dynamically imported module')
		expect(isChunkLoadError(error)).toBe(true)
	})

	it('should detect "Loading chunk" errors', () => {
		const error = new Error('Loading chunk 42 failed.')
		expect(isChunkLoadError(error)).toBe(true)
	})

	it('should detect "ChunkLoadError"', () => {
		const error = new Error('ChunkLoadError: Loading chunk app failed')
		expect(isChunkLoadError(error)).toBe(true)
	})

	it('should detect errors case-insensitively', () => {
		const error = new Error('FAILED TO FETCH DYNAMICALLY IMPORTED MODULE')
		expect(isChunkLoadError(error)).toBe(true)
	})

	it('should return false for unrelated errors', () => {
		const error = new Error('Cannot read property "map" of undefined')
		expect(isChunkLoadError(error)).toBe(false)
	})

	it('should return false for null', () => {
		expect(isChunkLoadError(null)).toBe(false)
	})

	it('should return false for undefined', () => {
		expect(isChunkLoadError(undefined)).toBe(false)
	})

	it('should handle plain string errors', () => {
		expect(isChunkLoadError('Failed to fetch dynamically imported module')).toBe(true)
	})

	it('should return false for empty string', () => {
		expect(isChunkLoadError('')).toBe(false)
	})
})
