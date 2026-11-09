import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest'
import {
	BESTILLING_SAVE_EVENT,
	saveBestillingFormState,
	loadBestillingFormState,
	loadBestillingFormStateForCurrentPage,
	clearBestillingFormState,
	requestBestillingSave,
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

		it('should save current path and router state so the bestilling can be reopened', () => {
			const originalUrl = window.location.href
			const originalState = window.history.state
			window.history.replaceState(
				{ usr: { antall: '1', identtype: 'FNR' } },
				'',
				'/gruppe/5/bestilling?mal=2',
			)
			try {
				saveBestillingFormState({ pdldata: {} }, 1)
			} finally {
				window.history.replaceState(originalState, '', originalUrl)
			}

			const stored = JSON.parse(sessionStorage.getItem(STORAGE_KEY)!)
			expect(stored.path).toBe('/gruppe/5/bestilling?mal=2')
			expect(stored.locationState).toEqual({ antall: '1', identtype: 'FNR' })
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

	describe('loadBestillingFormStateForCurrentPage', () => {
		const currentPath = () => window.location.pathname + window.location.search

		it('should return saved state from the current page', () => {
			const state = { formValues: {}, step: 1, savedAt: Date.now(), path: currentPath() }
			sessionStorage.setItem(STORAGE_KEY, JSON.stringify(state))

			expect(loadBestillingFormStateForCurrentPage()).toEqual(state)
		})

		it('should ignore saved state from another page', () => {
			const state = { formValues: {}, step: 1, savedAt: Date.now(), path: '/gruppe/5/bestilling' }
			sessionStorage.setItem(STORAGE_KEY, JSON.stringify(state))

			expect(loadBestillingFormStateForCurrentPage()).toBeNull()
		})

		it('should ignore saved state without path', () => {
			sessionStorage.setItem(
				STORAGE_KEY,
				JSON.stringify({ formValues: {}, step: 1, savedAt: Date.now() }),
			)

			expect(loadBestillingFormStateForCurrentPage()).toBeNull()
		})
	})

	describe('requestBestillingSave', () => {
		it('should dispatch the save event', () => {
			const listener = vi.fn()
			window.addEventListener(BESTILLING_SAVE_EVENT, listener)

			requestBestillingSave()

			window.removeEventListener(BESTILLING_SAVE_EVENT, listener)
			expect(listener).toHaveBeenCalledTimes(1)
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
