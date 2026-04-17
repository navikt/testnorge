import { describe, expect, it } from 'vitest'
import { normalizeMiljoeResponse } from '@/service/services/organisasjonforvalter/OrganisasjonForvalterService'

describe('normalizeMiljoeResponse', () => {
	it('shouldMergeArrayOfSingleEntryMaps', () => {
		const fluxResponse = [
			{ q1: { organisasjonsnummer: '123456789', organisasjonsnavn: 'Test AS' } },
			{ q2: { organisasjonsnummer: '123456789', organisasjonsnavn: 'Test AS' } },
		]

		const result = normalizeMiljoeResponse(fluxResponse)

		expect(Object.keys(result).sort()).toEqual(['q1', 'q2'])
		expect((result.q1 as any).organisasjonsnummer).toBe('123456789')
		expect((result.q2 as any).organisasjonsnummer).toBe('123456789')
	})

	it('shouldHandleSingleItemArray', () => {
		const fluxResponse = [
			{ q1: { organisasjonsnummer: '123456789', organisasjonsnavn: 'Test AS' } },
		]

		const result = normalizeMiljoeResponse(fluxResponse)

		expect(Object.keys(result)).toEqual(['q1'])
		expect((result.q1 as any).organisasjonsnummer).toBe('123456789')
	})

	it('shouldHandleAlreadyFlatObject', () => {
		const legacyResponse = {
			q1: { organisasjonsnummer: '123456789' },
			q2: { organisasjonsnummer: '123456789' },
		}

		const result = normalizeMiljoeResponse(legacyResponse)

		expect(Object.keys(result).sort()).toEqual(['q1', 'q2'])
	})

	it('shouldReturnEmptyObjectForEmptyArray', () => {
		const result = normalizeMiljoeResponse([])

		expect(result).toEqual({})
	})

	it('shouldReturnEmptyObjectForNull', () => {
		const result = normalizeMiljoeResponse(null)

		expect(result).toEqual({})
	})

	it('shouldReturnEmptyObjectForUndefined', () => {
		const result = normalizeMiljoeResponse(undefined)

		expect(result).toEqual({})
	})

	it('shouldProduceCorrectEnvironmentKeysForObjectDotKeys', () => {
		const fluxResponse = [
			{ q1: { organisasjonsnummer: '123456789' } },
			{ q2: { organisasjonsnummer: '123456789' } },
		]

		const normalized = normalizeMiljoeResponse(fluxResponse)
		const miljoer = Object.keys(normalized)

		expect(miljoer).not.toContain('0')
		expect(miljoer).not.toContain('1')
		expect(miljoer).toContain('q1')
		expect(miljoer).toContain('q2')
	})
})
