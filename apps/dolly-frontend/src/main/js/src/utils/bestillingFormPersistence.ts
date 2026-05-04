const STORAGE_KEY = 'dolly-bestilling-saved-form'
const MAX_AGE_MS = 60 * 60 * 1000

const EXCLUDED_KEYS = ['dokarkiv', 'histark']

interface SavedFormState {
	formValues: Record<string, any>
	step: number
	savedAt: number
}

export function saveBestillingFormState(
	formValues: Record<string, any>,
	step: number,
): void {
	const stripped = { ...formValues }
	for (const key of EXCLUDED_KEYS) {
		delete stripped[key]
	}
	const state: SavedFormState = { formValues: stripped, step, savedAt: Date.now() }
	sessionStorage.setItem(STORAGE_KEY, JSON.stringify(state))
}

export function loadBestillingFormState(): SavedFormState | null {
	try {
		const raw = sessionStorage.getItem(STORAGE_KEY)
		if (!raw) return null
		const parsed: SavedFormState = JSON.parse(raw)
		if (Date.now() - parsed.savedAt > MAX_AGE_MS) {
			clearBestillingFormState()
			return null
		}
		return parsed
	} catch {
		clearBestillingFormState()
		return null
	}
}

export function clearBestillingFormState(): void {
	sessionStorage.removeItem(STORAGE_KEY)
}
