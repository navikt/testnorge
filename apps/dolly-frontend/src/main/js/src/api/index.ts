import { NotFoundError } from '@/error'
import { Argument } from 'classnames'
import originalFetch from 'isomorphic-fetch'
import axios from 'axios'
import fetch_retry from 'fetch-retry'
import { runningCypressE2E } from '@/service/services/Request'
import * as _ from 'lodash-es'
import { navigateToLogin } from '@/components/utlogging/navigateToLogin'

const fetchRetry = fetch_retry(originalFetch)

export const multiFetcherAny = (urlListe, headers) => {
	return Promise.any(
		urlListe.map((url) =>
			fetcher(url, headers).then((result) => {
				if (_.isEmpty(result)) {
					throw new Error('Returnerte ingen verdi, prøver neste promise..')
				}
				return [result]
			}),
		),
	)
}

export const multiFetcherAll = (urlListe, headers = null) => {
	return Promise.all(
		urlListe.map((url) =>
			fetcher(url, headers).then((result) => {
				return result
			}),
		),
	)
}

export const multiFetcherBatchData = (url, dataListe) => {
	return Promise.all(
		dataListe.map((data) =>
			fetchJson(url, { method: 'POST' }, data).then((result) => {
				return result
			}),
		),
	)
}

export const multiFetcherInst = (miljoUrlListe, headers = null, path = null) => {
	return Promise.all(
		miljoUrlListe.map((obj) =>
			fetcher(obj.url, headers).then((result) => {
				return { miljo: obj.miljo, data: path ? result[path] : result?.[obj.miljo] }
			}),
		),
	)
}

export const multiFetcherArena = (miljoUrlListe, headers = null) => {
	return Promise.all(
		miljoUrlListe?.map((obj) =>
			fetcher(obj.url, headers)
				.then((result) => {
					const filteredResult =
						result?.status === 'NO_CONTENT' || result?.status === 'NOT_FOUND' ? null : result
					return { miljo: obj.miljo, data: filteredResult, status: result?.status }
				})
				.catch((feil) => {
					return { miljo: obj.miljo, feil: feil }
				}),
		),
	)
}

export const multiFetcherAareg = (miljoUrlListe, headers = null, path = null) => {
	return Promise.allSettled(
		miljoUrlListe.map((obj) =>
			fetcher(obj.url, headers)
				.then((result) => {
					return { miljo: obj.miljo, data: path ? result[path] : result }
				})
				.catch((feil) => {
					return { miljo: obj.miljo, feil: feil }
				}),
		),
	).then((liste) => liste?.map((item) => item?.value))
}

export const multiFetcherAmelding = (miljoUrlListe, headers = null, path = null) => {
	return Promise.allSettled(
		miljoUrlListe.map((obj) =>
			fetcher(obj.url, { miljo: obj.miljo })
				.then((result) => ({ miljo: obj.miljo, data: path ? result[path] : result }))
				.catch((feil) => {
					return { miljo: obj.miljo, feil: feil }
				}),
		),
	).then((liste) => liste?.map((item) => item?.value))
}

export const multiFetcherPensjon = (miljoUrlListe, headers = null as any) => {
	return Promise.all(
		miljoUrlListe.map((obj) =>
			fetcher(obj.url, { miljo: obj.miljo, ...headers }).then((result) => {
				return { miljo: obj.miljo, data: result }
			}),
		),
	)
}

export const multiFetcherDokarkiv = (miljoUrlListe) =>
	Promise.all(
		miljoUrlListe?.map((obj) =>
			obj.url
				? fetcher(obj.url, { miljo: obj.miljo }).then((result) => ({
						miljo: obj.miljo,
						data: result,
				  }))
				: { miljo: obj.miljo, data: null },
		),
	)

export const fetcher = (url, headers) =>
	axios
		.get(url, { headers: headers })
		.then((res) => {
			return res.data
		})
		.catch((reason) => {
			if (
				(reason?.response?.status === 401 || reason?.response?.status === 403) &&
				url.includes('dolly-backend')
			) {
				console.error('Auth feilet, navigerer til login')
				navigateToLogin()
			}
			if (reason.status === 404 || reason.response?.status === 404) {
				if (reason.response?.data?.error) {
					throw new Error(reason.response?.data?.error)
				}
				throw new NotFoundError()
			}
			throw new Error(`Henting av data fra ${url} feilet.`)
		})

export const imageFetcher = (...args: Argument[]) =>
	originalFetch(...args).then((res: Response) =>
		res.ok ? res.blob().then((blob: Blob) => URL.createObjectURL(blob)) : null,
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
			if (!response.ok && !runningCypressE2E()) {
				if (response.status === 401 && url.includes('dolly-backend')) {
					console.error('Auth feilet, navigerer til login')
					navigateToLogin()
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
		if (!response.ok && !runningCypressE2E()) {
			if (response.status === 401 && url.includes('dolly-backend')) {
				console.error('Auth feilet, navigerer til login')
				navigateToLogin()
			}
			if (response.status === 404) {
				throw new NotFoundError()
			}
			throw new Error('Response fra endepunkt var ikke ok')
		}
		return response
	})

const fetchJson = (url: string, config: Config, body?: object): Promise =>
	_fetch(
		url,
		{
			method: config.method,
			headers: { ...config.headers, 'Content-Type': 'application/json' },
		},
		body,
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
