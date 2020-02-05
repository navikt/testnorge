import axios from 'axios'

const instance = axios.create({ withCredentials: true })
const instanceWithoutCredentials = axios.create()

export default class Request {
	static get(url, config) {
		return instance.get(url, config)
	}

	static getWithoutCredentials(url, config) {
		return instanceWithoutCredentials.get(url, config)
	}

	static postWithoutCredentials(url, data, config) {
		return instanceWithoutCredentials.post(url, data, config)
	}

	static putWithoutCredentials(url, data, config) {
		return instanceWithoutCredentials.put(url, data, config)
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
