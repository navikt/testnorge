export const validate = values =>
	window
		.fetch('https://dolly-t2.nais.preprod.local/api/v1/inntektstub', {
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
