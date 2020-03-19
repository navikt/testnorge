import config from '~/config'

const uri = `${config.services.dollyBackend}`

export const SelectOptionsOppslag = {
	hentOrgnr: () =>
		fetch(`${uri}/orgnummer`, {
			credentials: 'include',
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
			.then(response => response.json())
			.catch(error => {
				console.error(error)
				throw error
			})
}
