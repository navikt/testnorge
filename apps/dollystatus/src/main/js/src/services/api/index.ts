type Method = 'POST' | 'GET' | 'PUT' | 'DELETE' | 'PATCH'

type Config = {
	method: Method
	headers?: Record<string, string>
}

export class ApiError extends Error {
	readonly response: Response

	constructor(response: Response) {
		super(`API-kallet feilet med status ${response.status}`)
		this.response = response
	}
}

const _fetch = (url: string, config: Config, body?: BodyInit): Promise<Response> =>
	window
		.fetch(url, {
			method: config.method,
			credentials: 'include',
			headers: config.headers,
			body: body,
		})
		.then((response: Response) => {
			if (!response.ok) {
				if (response.status === 401 && import.meta.env.DEV) {
					window.location.assign('/oauth2/authorization/aad')
				}
				throw new ApiError(response)
			}
			return response
		})

const fetchJson = <T>(url: string, config: Config, body?: BodyInit): Promise<T> =>
	_fetch(
		url,
		{
			method: config.method,
			headers: { ...config.headers, 'Content-Type': 'application/json' },
		},
		body
	).then((response: Response) => response.json() as Promise<T>)

export default {
	fetch: _fetch,
	fetchJson,
}
