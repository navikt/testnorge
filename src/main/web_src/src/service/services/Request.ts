import api from '@/api'

export enum ResponseType {
	TEXT = 'Text',
	JSON = 'Json'
}
export default class Request {
	static get(url: string, headers: Record<string, string> = {}) {
		return api.fetchJson(url, { headers, method: 'GET' }).then(response => ({ data: response }))
	}

	static post(url: string, data?: object, responseType?: ResponseType) {
		if (responseType === ResponseType.TEXT) {
			return api.fetchText(url, { method: 'POST' }, data).then(response => ({ data: response }))
		} else {
			return api.fetchJson(url, { method: 'POST' }, data).then(response => ({ data: response }))
		}
	}

	static put(url: string, data?: object) {
		return api.fetchJson(url, { method: 'PUT' }, data).then(response => ({ data: response }))
	}

	static delete(url: string) {
		return api.fetch(url, { method: 'DELETE' })
	}
}
