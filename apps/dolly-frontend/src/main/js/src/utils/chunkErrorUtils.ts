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

const RELOAD_SESSION_KEY = 'dolly-reloaded-for-chunk-error'

export const reloadPage = () => {
	window.location.reload()
}

export const handleChunkErrorWithReload = (reload: () => void = reloadPage): boolean => {
	const hasAlreadyReloaded = sessionStorage.getItem(RELOAD_SESSION_KEY) === 'true'

	if (hasAlreadyReloaded) {
		sessionStorage.removeItem(RELOAD_SESSION_KEY)
		return false
	}

	sessionStorage.setItem(RELOAD_SESSION_KEY, 'true')
	reload()
	return true
}
