import { NotFoundError } from '~/error'
import { Argument } from 'classnames'
import originalFetch from 'isomorphic-fetch'
import fetch_retry from 'fetch-retry'
import logoutBruker from '~/components/utlogging/logoutBruker'

const fetchRetry = fetch_retry(originalFetch)

export const fetcher = (...args: Argument[]) =>
	originalFetch(...args).then((res: Response) => {
		if (!res.ok) {
			if (res.status === 401 || res.status === 403) {
				console.error('Auth feilet, logger ut bruker')
				logoutBruker()
			}
			if (res.status === 404) {
				throw new NotFoundError()
			}
			throw new Error('An error occurred while fetching the data.')
		}
		return res.json()
	})

export const imageFetcher = (...args: Argument[]) =>
	originalFetch(...args).then((res: Response) =>
		res.ok ? res.blob().then((blob: Blob) => URL.createObjectURL(blob)) : null
	)

type Method = 'POST' | 'GET' | 'PUT' | 'DELETE'

type Config = {
	method: Method
	headers?: Record<string, string>
	redirect?: 'follow' | 'manual'
}

const _fetch = (url: string, config: Config, body?: object): Promise<Response> =>
	fetchRetry(url, {
		retries: 5,
		retryDelay: 800,
		method: config.method,
		redirect: config.redirect,
		credentials: 'include',
		headers: config.headers,
		body: JSON.stringify(body),
	})
		.then((response: Response) => {
			if (response.redirected) {
				window.location.href = response.url
			}
			if (!response.ok) {
				if (response.status === 401) {
					console.error('Auth feilet, reloader siden for å få ny auth client.')
					window.location.reload()
				}
				if (response.status === 404) {
					throw new NotFoundError()
				}
				throw new Error('Response fra endepunkt var ikke ok')
			}
			return response
		})
		.catch((error: Error) => {
			console.error(error)
			throw error
		})

const fetchJson = <T>(url: string, config: Config, body?: object): Promise<T> =>
	_fetch(
		url,
		{
			method: config.method,
			headers: { ...config.headers, 'Content-Type': 'application/json' },
		},
		body
	)
		.then((response) => {
			return response.text()
		})
		.then((data) => {
			return data ? JSON.parse(data) : {}
		})

export default {
	fetch: _fetch,
	fetchJson,
}
