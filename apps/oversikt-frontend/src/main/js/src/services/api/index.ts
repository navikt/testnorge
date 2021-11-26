import { BadRequestError, NotFoundError } from '@navikt/dolly-lib'

type Method = 'POST' | 'GET' | 'PUT' | 'DELETE' | 'PATCH'

type Config = {
	method: Method
	headers?: Record<string, string>
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
				if (response.status === 404) {
					throw new NotFoundError()
				}

				if (response.status == 400) {
					throw new BadRequestError(response)
				}

				throw new Error('Response fra endepunkt var ikke ok')
			}
			return response
		})
		.catch((error: Error) => {
			console.error(error)
			throw error
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
