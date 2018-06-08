import axios from 'axios'

class Request {
	static get(url) {
		return axios.get(url)
	}

	static post(url, data) {
		return axios.post(url, data)
	}

	static put(url, data) {
		return axios.put(url, data)
	}
}

export default Request
