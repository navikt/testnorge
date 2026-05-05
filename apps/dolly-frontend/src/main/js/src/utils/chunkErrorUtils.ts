const CHUNK_ERROR_PATTERNS = [
	'Failed to fetch dynamically imported module',
	'Unable to preload CSS',
	'Importing a module script failed',
	'error loading dynamically imported module',
	'Loading chunk',
	'Loading CSS chunk',
	'ChunkLoadError',
]

export const isChunkLoadError = (error: unknown): boolean => {
	if (!error) return false

	const message =
		error instanceof Error
			? error.message
			: typeof error === 'string'
				? error
				: String(error)

	return CHUNK_ERROR_PATTERNS.some((pattern) =>
		message.toLowerCase().includes(pattern.toLowerCase()),
	)
}

const RELOAD_COUNT_KEY = 'dolly-chunk-reload-count'
const RELOAD_TIME_KEY = 'dolly-chunk-reload-time'
const MAX_RELOADS = 3
const RELOAD_WINDOW_MS = 60000

export const reloadPage = () => {
	window.location.replace(window.location.pathname + '?_r=' + Date.now())
}

export const handleChunkErrorWithReload = (reload: () => void = reloadPage): boolean => {
	const lastTime = parseInt(sessionStorage.getItem(RELOAD_TIME_KEY) || '0', 10)
	const count = parseInt(sessionStorage.getItem(RELOAD_COUNT_KEY) || '0', 10)

	const windowExpired = Date.now() - lastTime > RELOAD_WINDOW_MS

	if (windowExpired) {
		sessionStorage.setItem(RELOAD_COUNT_KEY, '1')
		sessionStorage.setItem(RELOAD_TIME_KEY, String(Date.now()))
		reload()
		return true
	}

	if (count < MAX_RELOADS) {
		sessionStorage.setItem(RELOAD_COUNT_KEY, String(count + 1))
		sessionStorage.setItem(RELOAD_TIME_KEY, String(Date.now()))
		reload()
		return true
	}

	sessionStorage.removeItem(RELOAD_COUNT_KEY)
	sessionStorage.removeItem(RELOAD_TIME_KEY)
	return false
}

export const clearChunkReloadState = () => {
	sessionStorage.removeItem(RELOAD_COUNT_KEY)
	sessionStorage.removeItem(RELOAD_TIME_KEY)
}
