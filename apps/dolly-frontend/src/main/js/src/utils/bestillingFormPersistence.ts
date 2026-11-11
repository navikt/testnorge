export const BESTILLING_SAVE_EVENT = 'dolly-save-and-reload'

const STORAGE_KEY = 'dolly-bestilling-saved-form'
const MAX_AGE_MS = 60 * 60 * 1000

const EXCLUDED_KEYS = ['dokarkiv', 'histark']

const currentPath = () => window.location.pathname + window.location.search

interface SavedFormState {
	formValues: Record<string, any>
	step: number
	savedAt: number
	path?: string
	locationState?: unknown
}

let pendingFormState: string | null = null

function serialize(formValues: Record<string, any>, step: number): string {
	const stripped = { ...formValues }
	for (const key of EXCLUDED_KEYS) {
		delete stripped[key]
	}
	const state: SavedFormState = {
		formValues: stripped,
		step,
		savedAt: Date.now(),
		path: currentPath(),
		locationState: window.history.state?.usr,
	}
	return JSON.stringify(state)
}

export function saveBestillingFormState(formValues: Record<string, any>, step: number): void {
	sessionStorage.setItem(STORAGE_KEY, serialize(formValues, step))
}

export function rememberBestillingFormState(formValues: Record<string, any>, step: number): void {
	pendingFormState = serialize(formValues, step)
}

export function savePendingBestillingFormState(): void {
	if (pendingFormState) {
		sessionStorage.setItem(STORAGE_KEY, pendingFormState)
		pendingFormState = null
	}
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

export function loadBestillingFormStateForCurrentPage(): SavedFormState | null {
	const saved = loadBestillingFormState()
	return saved?.path === currentPath() ? saved : null
}

export function requestBestillingSave(): void {
	window.dispatchEvent(new CustomEvent(BESTILLING_SAVE_EVENT))
}

export function clearBestillingFormState(): void {
	sessionStorage.removeItem(STORAGE_KEY)
}
