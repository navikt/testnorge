import api from '@/api'
import Logger from '~/logger'

export const REQUEST_ERROR = 'REQUEST_ERROR'

export const runningTestcafe = () => {
	return window?.hasOwnProperty('%testCafeAutomation%')
}

export default class Request {
	static get(url: string, headers: Record<string, string> = {}) {
		return api
			.fetchJson(url, { headers, method: 'GET' })
			.then((response) => ({ data: response }))
			.catch((error) => Request.logError(error, url))
	}

	static getBilde(url: string) {
		return api
			.fetch(url, { method: 'GET' })
			.then((response) => ({ data: response }))
			.catch((error) => Request.logError(error, url))
	}

	static getExcel(url: string) {
		return api
			.fetch(url, { method: 'GET' })
			.then((response) => response.blob())
			.then((blob) => URL.createObjectURL(blob))
			.catch((error) => Request.logError(error, url))
	}

	static post(url: string, data?: object) {
		return api.fetchJson(url, { method: 'POST' }, data).then((response) => ({ data: response }))
	}

	static put(url: string, data?: object) {
		return api.fetchJson(url, { method: 'PUT' }, data).then((response) => ({ data: response }))
	}

	static putWithoutResponse(url: string, data?: object) {
		return api.fetch(url, { method: 'PUT', headers: { 'Content-Type': 'application/json' } }, data)
	}

	static delete(url: string, headers: Record<string, string> = {}) {
		return api.fetch(url, { headers, method: 'DELETE' })
	}

	private static logError(error: any, url: string) {
		const event = `Henting av data fra ${url} feilet.`
		console.error(event, error.message)
		Logger.error({
			event: event,
			message: error.message,
		})
		if (error.name !== 'NotFoundError') {
			const errorMessage = event + ' Dersom Dolly er ustabil, prøv å laste siden på nytt!'
			sessionStorage.setItem(REQUEST_ERROR, errorMessage)
			throw new Error(errorMessage)
		}
	}
}
