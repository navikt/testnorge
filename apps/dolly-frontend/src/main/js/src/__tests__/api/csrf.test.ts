import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import axios, { AxiosAdapter, InternalAxiosRequestConfig } from 'axios'
import {
	CSRF_FAILED_EVENT,
	csrfFetch,
	installAxiosCsrf,
	loadCsrfToken,
	resetCsrfToken,
} from '@/api/csrf'

const tokenResponse = (token: string) =>
	new Response(JSON.stringify({ headerName: 'X-CSRF-TOKEN', parameterName: '_csrf', token }), {
		status: 200,
		headers: { 'Content-Type': 'application/json' },
	})

const csrfRejected = () =>
	new Response(null, { status: 403, headers: { 'X-Dolly-Csrf': 'invalid' } })

const mockFetch = (handler: (url: string, init?: RequestInit) => Response) => {
	const fetchMock = vi.fn(async (input: RequestInfo | URL, init?: RequestInit) =>
		handler(typeof input === 'string' ? input : input.toString(), init),
	)
	vi.stubGlobal('fetch', fetchMock)
	return fetchMock
}

const sentToken = (init?: RequestInit) => new Headers(init?.headers).get('X-CSRF-TOKEN')

describe('csrfFetch', () => {
	beforeEach(() => resetCsrfToken())
	afterEach(() => vi.unstubAllGlobals())

	it('sender token bare på muterende kall til samme origin', async () => {
		const fetchMock = mockFetch((url) =>
			url === '/session/csrf' ? tokenResponse('t1') : new Response('ok'),
		)

		await csrfFetch('/dolly-backend/api/v1/gruppe', { method: 'POST' })
		await csrfFetch('/dolly-backend/api/v1/gruppe')
		await csrfFetch('https://example.com/api', { method: 'POST' })

		const calls = fetchMock.mock.calls.filter(([url]) => url !== '/session/csrf')
		expect(sentToken(calls[0][1])).toBe('t1')
		expect(sentToken(calls[1][1])).toBeNull()
		expect(sentToken(calls[2][1])).toBeNull()
	})

	it('henter token én gang for samtidige kall', async () => {
		const fetchMock = mockFetch((url) =>
			url === '/session/csrf' ? tokenResponse('t1') : new Response('ok'),
		)

		await Promise.all([
			csrfFetch('/a', { method: 'POST' }),
			csrfFetch('/b', { method: 'PUT' }),
			csrfFetch('/c', { method: 'DELETE' }),
		])

		expect(fetchMock.mock.calls.filter(([url]) => url === '/session/csrf')).toHaveLength(1)
	})

	it('henter nytt token og prøver én gang til ved avvisning', async () => {
		let tokens = 0
		const fetchMock = mockFetch((url, init) => {
			if (url === '/session/csrf') {
				tokens += 1
				return tokenResponse(`t${tokens}`)
			}
			return sentToken(init) === 't2' ? new Response('ok') : csrfRejected()
		})

		const response = await csrfFetch('/a', { method: 'POST' })

		expect(response.status).toBe(200)
		expect(fetchMock.mock.calls.filter(([url]) => url === '/a')).toHaveLength(2)
	})

	it('varsler når andre forsøk også avvises', async () => {
		const listener = vi.fn()
		window.addEventListener(CSRF_FAILED_EVENT, listener)
		const fetchMock = mockFetch((url) =>
			url === '/session/csrf' ? tokenResponse('t') : csrfRejected(),
		)

		const response = await csrfFetch('/a', { method: 'POST' })

		window.removeEventListener(CSRF_FAILED_EVENT, listener)
		expect(response.status).toBe(403)
		expect(fetchMock.mock.calls.filter(([url]) => url === '/a')).toHaveLength(2)
		expect(listener).toHaveBeenCalledTimes(1)
	})

	it('henter nytt token når loggkall avvises med 400', async () => {
		let tokens = 0
		const fetchMock = mockFetch((url, init) => {
			if (url === '/session/csrf') {
				tokens += 1
				return tokenResponse(`t${tokens}`)
			}
			return sentToken(init) === 't2'
				? new Response('ok')
				: new Response(null, { status: 400, headers: { 'X-Dolly-Csrf': 'invalid' } })
		})

		const response = await csrfFetch('/api/dolly-logg', { method: 'POST' })

		expect(response.status).toBe(200)
		expect(fetchMock.mock.calls.filter(([url]) => url === '/api/dolly-logg')).toHaveLength(2)
	})

	it('vanlig 403 gir ingen ny runde', async () => {
		const fetchMock = mockFetch((url) =>
			url === '/session/csrf' ? tokenResponse('t') : new Response(null, { status: 403 }),
		)

		const response = await csrfFetch('/a', { method: 'POST' })

		expect(response.status).toBe(403)
		expect(fetchMock.mock.calls.filter(([url]) => url === '/a')).toHaveLength(1)
	})

	it('reset avbryter ikke en henting som pågår', async () => {
		let resolveToken: (response: Response) => void = () => {}
		const fetchMock = vi.fn(() => new Promise<Response>((resolve) => (resolveToken = resolve)))
		vi.stubGlobal('fetch', fetchMock)

		const first = loadCsrfToken()
		resetCsrfToken()
		const second = loadCsrfToken()
		resolveToken(tokenResponse('t1'))

		expect(await second).toEqual(await first)
		expect(fetchMock).toHaveBeenCalledTimes(1)
	})

	it('gir opp tokenhentingen etter en tidsgrense', async () => {
		const fetchMock = mockFetch((url) =>
			url === '/session/csrf' ? tokenResponse('t1') : new Response('ok'),
		)

		await loadCsrfToken()

		expect(fetchMock.mock.calls[0][1]?.signal).toBeInstanceOf(AbortSignal)
	})
})

describe('installAxiosCsrf', () => {
	beforeEach(() => resetCsrfToken())
	afterEach(() => vi.unstubAllGlobals())

	it('legger på token og prøver én gang til ved avvisning', async () => {
		let tokens = 0
		mockFetch(() => {
			tokens += 1
			return tokenResponse(`t${tokens}`)
		})
		const sent: Array<string | undefined> = []
		const adapter: AxiosAdapter = async (config: InternalAxiosRequestConfig) => {
			const token = config.headers.get('X-CSRF-TOKEN') as string | undefined
			sent.push(token)
			if (token === 't2') {
				return { data: 'ok', status: 200, statusText: 'OK', headers: {}, config }
			}
			throw Object.assign(new Error('forbidden'), {
				isAxiosError: true,
				config,
				response: {
					status: 403,
					headers: { 'x-dolly-csrf': 'invalid' },
					data: null,
					statusText: 'Forbidden',
					config,
				},
			})
		}
		const instance = axios.create({ adapter })
		installAxiosCsrf(instance)

		const response = await instance.post('/dolly-backend/api/v1/gruppe', {})

		expect(response.data).toBe('ok')
		expect(sent).toEqual(['t1', 't2'])
	})

	it('henter token høyst to ganger når endepunktet feiler', async () => {
		const fetchMock = mockFetch(() => new Response(null, { status: 503 }))
		const adapter: AxiosAdapter = async (config) => {
			throw Object.assign(new Error('forbidden'), {
				isAxiosError: true,
				config,
				response: {
					status: 403,
					headers: { 'x-dolly-csrf': 'invalid' },
					data: null,
					statusText: 'Forbidden',
					config,
				},
			})
		}
		const instance = axios.create({ adapter })
		installAxiosCsrf(instance)

		await expect(instance.post('/dolly-backend/api/v1/gruppe', {})).rejects.toBeDefined()

		expect(fetchMock).toHaveBeenCalledTimes(2)
	})

	it('sender ikke token på GET', async () => {
		const fetchMock = mockFetch(() => tokenResponse('t1'))
		const sent: Array<unknown> = []
		const adapter: AxiosAdapter = async (config) => {
			sent.push(config.headers.get('X-CSRF-TOKEN'))
			return { data: 'ok', status: 200, statusText: 'OK', headers: {}, config }
		}
		const instance = axios.create({ adapter })
		installAxiosCsrf(instance)

		await instance.get('/dolly-backend/api/v1/gruppe')

		expect(sent).toEqual([undefined])
		expect(fetchMock).not.toHaveBeenCalled()
	})
})
