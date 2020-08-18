import config from '~/config'

type Request = {
	identer: Array<string>
	kildeMiljoe: string
}

export const api = {
	importerPersoner: (gruppeId: string, request: Request) =>
		fetch(`${config.services.dollyBackend}/gruppe/${gruppeId}/bestilling/importFraTps`, {
			method: 'POST',
			credentials: 'include',
			body: JSON.stringify(request),
			headers: {
				'Content-Type': 'application/json'
			}
		})
			.then(response => {
				if (!response.ok) {
					throw new Error(response.statusText)
				}
				return response
			})
			.catch(error => {
				console.error(error)
				throw error
			}),
	opprettGruppe: (navn: string, hensikt: string) =>
		fetch(`${config.services.dollyBackend}/gruppe`, {
			method: 'POST',
			credentials: 'include',
			body: JSON.stringify({ navn, hensikt }),
			headers: {
				'Content-Type': 'application/json'
			}
		})
			.then(response => {
				return response.json()
			})
			.catch(error => {
				console.error(error)
				throw error
			})
}
