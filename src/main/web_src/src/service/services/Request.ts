import api from '@/api'

export default class Request {
	static get(url: string, headers: Record<string, string> = {}) {
		return api.fetchJson(url, { headers, method: 'GET' }).then(response => ({ data: response }))
	}

	static getBilde(url: string) {
		return api
			.fetch(url, { method: 'GET' })
			.then(response => ({ data: response }))
			.catch(error => null)
	}

	static post(url: string, data?: object) {
		return api.fetchJson(url, { method: 'POST' }, data).then(response => ({ data: response }))
	}

	static put(url: string, data?: object) {
		return api.fetchJson(url, { method: 'PUT' }, data).then(response => ({ data: response }))
	}

	static putWithoutResponse(url: string, data?: object) {
		return api.fetch(url, { method: 'PUT' }, data)
	}

	static delete(url: string) {
		return api.fetch(url, { method: 'DELETE' })
	}
}
