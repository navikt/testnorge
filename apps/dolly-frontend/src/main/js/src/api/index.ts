import { NotFoundError } from '~/error'
import { Argument } from 'classnames'
import originalFetch from 'isomorphic-fetch'
import axios from 'axios'
import fetch_retry from 'fetch-retry'
import logoutBruker from '~/components/utlogging/logoutBruker'
import { runningTestcafe } from '~/service/services/Request'
import _isEmpty from 'lodash/isEmpty'

const fetchRetry = fetch_retry(originalFetch)

export const multiFetcherAny = (urlListe, headers) => {
	return Promise.any(
		urlListe.map((url) =>
			fetcher(url, headers).then((result) => {
				if (_isEmpty(result)) {
					throw new Error('Returnerte ingen verdi, prøver neste promise..')
				}
				return [result]
			})
		)
	)
}

export const multiFetcherAll = (urlListe, headers = null) => {
	return Promise.all(
		urlListe.map((url) =>
			fetcher(url, headers).then((result) => {
				return [result]
			})
		)
	)
}

export const multiFetcherFagsystemer = (miljoUrlListe, headers = null) => {
	return Promise.all(
		miljoUrlListe.map((obj) =>
			fetcher(obj.url, headers).then((result) => {
				// console.log('result: ', result) //TODO - SLETT MEG
				return { miljo: obj.miljo, data: [...result] }
			})
		)
	)
}

// TODO: lag én som er generell
export const multiFetcherPopp = (miljoUrlListe, headers = null) => {
	return Promise.all(
		miljoUrlListe.map((obj) =>
			fetcher(obj.url, headers).then((result) => {
				// console.log('result: ', result) //TODO - SLETT MEG
				return { miljo: obj.miljo, data: result?.inntekter }
			})
		)
	)
}

export const fetcher = (url, headers: Record<string, string>) =>
	axios
		.get(url, { headers: headers })
		.then((res) => {
			return res.data
		})
		.catch((reason) => {
			if (runningTestcafe()) {
				return null
			}
			if (reason.status === 401 || reason.status === 403) {
				console.error('Auth feilet, logger ut bruker')
				logoutBruker()
			}
			if (reason.status === 404) {
				throw new NotFoundError()
			}
			throw new Error(`Henting av data fra ${url} feilet.`)
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
		retryOn: (attempt, _error, response) => {
			if (!response.ok && !runningTestcafe()) {
				if (response.status === 401) {
					console.error('Auth feilet, reloader siden for å få ny auth client.')
					window.location.reload()
				}
				if (attempt < 4) {
					return true
				}
				throw new Error('Response fra endepunkt var ikke ok')
			}
			return false
		},
		retries: 5,
		retryDelay: 800,
		method: config.method,
		redirect: config.redirect,
		credentials: 'include',
		headers: config.headers,
		body: JSON.stringify(body),
	}).then((response: Response) => {
		if (response.redirected) {
			window.location.href = response.url
		}
		if (!response.ok && !runningTestcafe()) {
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
			return response?.text()
		})
		.then((data) => {
			return data ? JSON.parse(data) : {}
		})

export default {
	fetch: _fetch,
	fetchJson,
}
