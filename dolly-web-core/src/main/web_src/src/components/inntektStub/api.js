import config from '~/config'

const uri = `${config.services.dollyBackend}`

export const validate = values =>
	window
		.fetch(`${uri}/inntektstub`, {
			method: 'POST',
			mode: 'cors',
			credentials: 'include',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(values)
		})
		.then(response => response.json())
		.catch(error => {
			console.error('error :', error)
			return error
		})
