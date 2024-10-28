import { NotFoundError } from '@/error'
import { Argument } from 'classnames'
import originalFetch from 'isomorphic-fetch'
import axios from 'axios'
import fetch_retry from 'fetch-retry'
import { runningE2ETest } from '@/service/services/Request'
import { navigateToLogin } from '@/components/utlogging/navigateToLogin'
import { Logger } from '@/logger/Logger'

const fetchRetry = fetch_retry(originalFetch)

const allowForbidden = ['testnav-arbeidsplassencv', 'testnav-fullmakt', 'infostripe', 'norg2']

export const multiFetcherAll = (urlListe, headers = null) => {
	return Promise.all(
		urlListe.map((url) =>
			fetcher(url, headers).then((result) => {
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

export const multiFetcherAfpOffentlig = (miljoUrlListe, headers = null, path = null) => {
	return Promise.allSettled(
		miljoUrlListe.map((obj) =>
			fetcher(obj.url, headers)
				.then((result) => {
					return { miljo: obj.miljo, data: result }
				})
				.catch((feil) => {
					return { miljo: obj.miljo, feil: feil }
				}),
		),
	).then((liste) => liste?.map((item) => item?.value))
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

export const cvFetcher = (url, headers) =>
	axios
		.get(url, { headers: headers })
		.then((res) => {
			return res.data
		})
		.catch((reason) => {
			if (reason?.response?.status === 403) {
				throw {
					message: `Mangler tilgang for å hente CV fra ${url}`,
					status: reason?.response?.status,
				}
			}
			if (reason.status === 404 || reason.response?.status === 404) {
				if (reason.response?.data?.error) {
					throw new Error(reason.response?.data?.error)
				}
				throw new NotFoundError()
			}
			throw new Error(`Henting av data fra ${url} feilet.`)
		})

const clearLargeCookies = () => {
	const cookies = document.cookie.split(';')
	cookies.forEach((cookie) => {
		const [name, value] = cookie.split('=')
		if (value && value.length > 1000) {
			// Fjerner cookies som er over 1000 tegn
			document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/`
		}
	})
}

export const fetcher = (url, headers) => {
	clearLargeCookies()
	return axios
		.get(url, { headers: headers })
		.then((res) => {
			return res.data
		})
		.catch((reason) => {
			if (
				(reason?.response?.status === 401 || reason?.response?.status === 403) &&
				!allowForbidden.some((value) => url.includes(value))
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
}

export const imageFetcher = (...args: Argument[]) => {
	clearLargeCookies()
	return originalFetch(...args).then((res: Response) =>
		res.ok ? res.blob().then((blob: Blob) => URL.createObjectURL(blob)) : null,
	)
}

type Method = 'POST' | 'GET' | 'PUT' | 'DELETE'

type Config = {
	method: Method
	headers?: Record<string, string>
	redirect?: 'follow' | 'manual'
}

const _fetch = (url: string, config: Config, body?: object): Promise<Response> => {
	clearLargeCookies()
	return fetchRetry(url, {
		retryOn: (attempt, error, response) => {
			if (!response.ok && !runningE2ETest()) {
				if (response?.status === 401 && !allowForbidden.some((value) => url.includes(value))) {
					console.error('Auth feilet, navigerer til login')
					navigateToLogin()
				}
				if (attempt < 4) {
					return true
				}
				Logger.error({
					event: `Response fra URL: ${response.url} var ikke OK`,
					message: error,
					uuid: window.uuid,
				})
				throw new Error(`Response fra endepunkt var ikke ok, max retries oversteget`)
			}
			return false
		},
		retries: 5,
		retryDelay: (attempt: number, _error: any, _response: any) => {
			return Math.pow(2, attempt) * 1000 // 1000, 2000, 4000
		},
		method: config.method,
		redirect: config.redirect,
		credentials: 'include',
		headers: config.headers,
		body: JSON.stringify(body),
	}).then((response: Response) => {
		if (response.redirected) {
			window.location.href = response.url
		}
		if (!response.ok && !runningE2ETest()) {
			if (response?.status === 401 && !allowForbidden.some((value) => url.includes(value))) {
				console.error('Auth feilet, navigerer til login')
				navigateToLogin()
			}
			if (response.status === 404) {
				throw new NotFoundError()
			}
			Logger.error({
				event: `Response fra URL: ${response.url} var ikke OK`,
				message: response.text(),
				uuid: window.uuid,
			})
			throw new Error('Response fra endepunkt var ikke ok')
		}
		return response
	})
}

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
