type Method = 'POST' | 'GET' | 'PUT' | 'DELETE'

type Config = {
	method: Method
	headers?: Record<string, string>
}

const _fetch = (url: string, config: Config, body?: object): Promise<Response> =>
	window
		.fetch(url, {
			method: config.method,
			credentials: 'include',
			headers: config.headers,
			body: JSON.stringify(body)
		})
		.then((response: Response) => {
			if (!response.ok) {
				throw new Error('Response fra endepunkt var ikke ok')
			}
			return response
		})
		.catch((error: Error) => {
			console.error(error)
			throw error
		})

const fetchJson = <T>(url: string, config: Config, body?: object): Promise<T> =>
	_fetch(
		url,
		{
			method: config.method,
			headers: { ...config.headers, 'Content-Type': 'application/json' }
		},
		body
	).then((response: Response) => response.json() as Promise<T>)

export default {
	fetch: _fetch,
	fetchJson
}
