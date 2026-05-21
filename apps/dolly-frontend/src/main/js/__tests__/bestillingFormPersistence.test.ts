import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest'
import {
	saveBestillingFormState,
	loadBestillingFormState,
	clearBestillingFormState,
} from '@/utils/bestillingFormPersistence'

const STORAGE_KEY = 'dolly-bestilling-saved-form'

describe('bestillingFormPersistence', () => {
	beforeEach(() => {
		sessionStorage.clear()
	})

	afterEach(() => {
		sessionStorage.clear()
		vi.restoreAllMocks()
	})

	describe('saveBestillingFormState', () => {
		it('should save form values and step to sessionStorage', () => {
			const values = { pdldata: { person: { navn: 'Test' } }, arenaforvalter: { aap: [] } }
			saveBestillingFormState(values, 2)

			const stored = JSON.parse(sessionStorage.getItem(STORAGE_KEY)!)
			expect(stored.formValues.pdldata).toEqual({ person: { navn: 'Test' } })
			expect(stored.formValues.arenaforvalter).toEqual({ aap: [] })
			expect(stored.step).toBe(2)
			expect(stored.savedAt).toBeTypeOf('number')
		})

		it('should exclude dokarkiv from saved state', () => {
			const values = {
				pdldata: { person: {} },
				dokarkiv: [{ tittel: 'test', dokumenter: [{ fysiskDokument: 'base64data...' }] }],
			}
			saveBestillingFormState(values, 1)

			const stored = JSON.parse(sessionStorage.getItem(STORAGE_KEY)!)
			expect(stored.formValues.dokarkiv).toBeUndefined()
			expect(stored.formValues.pdldata).toBeDefined()
		})

		it('should exclude histark from saved state', () => {
			const values = {
				pdldata: { person: {} },
				histark: { dokumenter: ['large data'] },
			}
			saveBestillingFormState(values, 1)

			const stored = JSON.parse(sessionStorage.getItem(STORAGE_KEY)!)
			expect(stored.formValues.histark).toBeUndefined()
		})

		it('should not mutate the original form values object', () => {
			const values = { pdldata: {}, dokarkiv: [{ tittel: 'test' }] }
			saveBestillingFormState(values, 0)
			expect(values.dokarkiv).toEqual([{ tittel: 'test' }])
		})
	})

	describe('loadBestillingFormState', () => {
		it('should return null when no saved state exists', () => {
			expect(loadBestillingFormState()).toBeNull()
		})

		it('should return saved state when it exists and is not expired', () => {
			const state = { formValues: { pdldata: {} }, step: 2, savedAt: Date.now() }
			sessionStorage.setItem(STORAGE_KEY, JSON.stringify(state))

			const loaded = loadBestillingFormState()
			expect(loaded).toEqual(state)
		})

		it('should return null and clear state when expired (> 1 hour)', () => {
			const state = {
				formValues: { pdldata: {} },
				step: 1,
				savedAt: Date.now() - 2 * 60 * 60 * 1000,
			}
			sessionStorage.setItem(STORAGE_KEY, JSON.stringify(state))

			expect(loadBestillingFormState()).toBeNull()
			expect(sessionStorage.getItem(STORAGE_KEY)).toBeNull()
		})

		it('should return null and clear state when data is corrupted', () => {
			sessionStorage.setItem(STORAGE_KEY, 'not valid json')

			expect(loadBestillingFormState()).toBeNull()
			expect(sessionStorage.getItem(STORAGE_KEY)).toBeNull()
		})
	})

	describe('clearBestillingFormState', () => {
		it('should remove the saved state from sessionStorage', () => {
			sessionStorage.setItem(STORAGE_KEY, JSON.stringify({ formValues: {}, step: 0, savedAt: 0 }))
			clearBestillingFormState()
			expect(sessionStorage.getItem(STORAGE_KEY)).toBeNull()
		})

		it('should not throw when no saved state exists', () => {
			expect(() => clearBestillingFormState()).not.toThrow()
		})
	})
})
