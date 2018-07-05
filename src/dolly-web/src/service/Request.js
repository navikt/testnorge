import axios from 'axios'

const instance = axios.create({ withCredentials: true })

const debugLoading = resolvedValue => {
	// TODO: Create debug flag? process.env.DEBUG ?
	const debug = true
	const DELAY_TIME = 1000

	return new Promise(resolve => {
		if (debug) {
			setTimeout(() => resolve(resolvedValue), DELAY_TIME)
		} else {
			resolve(resolvedValue)
		}
	})
}

export default class Request {
	static get(url) {
		return instance.get(url).then(debugLoading)
	}

	static post(url, data) {
		return instance.post(url, data)
	}

	static put(url, data) {
		return instance.put(url, data)
	}

	static delete(url) {
		return instance.delete(url)
	}
}
