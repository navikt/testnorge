import type { AxiosError, AxiosInstance, InternalAxiosRequestConfig } from 'axios'
import { BACKGROUND_REQUEST_HEADERS } from '@/components/utlogging/sessionExpired'

export const CSRF_HEADER = 'x-dolly-csrf'
export const CSRF_FAILED_EVENT = 'dolly-csrf-failed'

const CSRF_INVALID = 'invalid'
const TOKEN_URL = '/session/csrf'
const TOKEN_TIMEOUT_MS = 10_000
const SAFE_METHODS = ['GET', 'HEAD', 'OPTIONS', 'TRACE']

export interface CsrfToken {
	headerName: string
	parameterName: string
	token: string
}

export type CsrfTokenResult =
	{ status: 'ok'; csrfToken: CsrfToken } | { status: 'expired' } | { status: 'failed' }

let pending: Promise<CsrfTokenResult> | null = null
let inFlight = false

const fetchToken = async (): Promise<CsrfTokenResult> => {
	try {
		const response = await globalThis.fetch(TOKEN_URL, {
			credentials: 'same-origin',
			headers: BACKGROUND_REQUEST_HEADERS,
			redirect: 'manual',
			signal: AbortSignal.timeout(TOKEN_TIMEOUT_MS),
		})
		if (response.status === 401 || response.type === 'opaqueredirect') {
			return { status: 'expired' }
		}
		if (!response.ok) {
			return { status: 'failed' }
		}
		return { status: 'ok', csrfToken: await response.json() }
	} catch {
		return { status: 'failed' }
	}
}

export const loadCsrfToken = (): Promise<CsrfTokenResult> => {
	if (!pending) {
		inFlight = true
		const current = fetchToken().then((result) => {
			if (pending === current) {
				inFlight = false
				if (result.status !== 'ok') {
					pending = null
				}
			}
			return result
		})
		pending = current
	}
	return pending
}

const refreshCsrfToken = (rejected: Promise<CsrfTokenResult>): Promise<CsrfTokenResult> => {
	if (pending === rejected) {
		pending = null
	}
	return loadCsrfToken()
}

export const resetCsrfToken = () => {
	if (!inFlight) {
		pending = null
	}
}

export const isCsrfRejected = (status?: number, headerValue?: string | null) =>
	(status === 400 || status === 403) && headerValue === CSRF_INVALID

export const notifyCsrfFailure = () => window.dispatchEvent(new CustomEvent(CSRF_FAILED_EVENT))

const needsCsrfToken = (method: string | undefined, url: string | undefined) => {
	if (!method || SAFE_METHODS.includes(method.toUpperCase())) {
		return false
	}
	try {
		return new URL(url ?? '', window.location.href).origin === window.location.origin
	} catch {
		return false
	}
}

const tokenHeader = async (tokenRequest: Promise<CsrfTokenResult>) => {
	const result = await tokenRequest
	return result.status === 'ok' ? result.csrfToken : null
}

export const csrfFetch = async (url: string, init: RequestInit = {}) => {
	if (!needsCsrfToken(init.method, url)) {
		return globalThis.fetch(url, init)
	}

	const send = async (tokenRequest: Promise<CsrfTokenResult>) => {
		const csrfToken = await tokenHeader(tokenRequest)
		const headers = new Headers(init.headers)
		if (csrfToken) {
			headers.set(csrfToken.headerName, csrfToken.token)
		}
		return globalThis.fetch(url, { ...init, headers })
	}

	const tokenRequest = loadCsrfToken()
	const response = await send(tokenRequest)
	if (!isCsrfRejected(response.status, response.headers.get(CSRF_HEADER))) {
		return response
	}

	const retry = await send(refreshCsrfToken(tokenRequest))
	if (isCsrfRejected(retry.status, retry.headers.get(CSRF_HEADER))) {
		notifyCsrfFailure()
	}
	return retry
}

type CsrfRequestConfig = InternalAxiosRequestConfig & {
	csrfTokenRequest?: Promise<CsrfTokenResult>
	csrfRetried?: boolean
}

export const installAxiosCsrf = (instance: AxiosInstance) => {
	instance.interceptors.request.use(async (config: CsrfRequestConfig) => {
		if (!needsCsrfToken(config.method, config.url)) {
			return config
		}
		if (!config.csrfRetried || !config.csrfTokenRequest) {
			config.csrfTokenRequest = loadCsrfToken()
		}
		const csrfToken = await tokenHeader(config.csrfTokenRequest)
		if (csrfToken) {
			config.headers.set(csrfToken.headerName, csrfToken.token)
		}
		return config
	})

	instance.interceptors.response.use(undefined, async (error: AxiosError) => {
		const config = error.config as CsrfRequestConfig | undefined
		const rejected = isCsrfRejected(error.response?.status, error.response?.headers?.[CSRF_HEADER])
		if (!config || !rejected) {
			throw error
		}
		if (config.csrfRetried) {
			notifyCsrfFailure()
			throw error
		}
		config.csrfRetried = true
		config.csrfTokenRequest = config.csrfTokenRequest
			? refreshCsrfToken(config.csrfTokenRequest)
			: loadCsrfToken()
		return instance.request(config)
	})
}
