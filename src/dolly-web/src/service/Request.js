import axios from 'axios'

const instance = axios.create({ withCredentials: true })

class Request {
	static get(url) {
		return instance.get(url)
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

export default Request
