import React, { useEffect, useState } from 'react'
import Loading from './Loading'
import { AlertStripeFeil } from 'nav-frontend-alertstriper'
import { Hovedknapp as NavKnapp } from 'nav-frontend-knapper'
import styled from 'styled-components'

interface LoadableComponent<T> {
	onFetch: () => Promise<T>
	render: (data: T) => JSX.Element
}

const Knapp = styled(NavKnapp)`
	margin-top: 15px;
	display: block;
`

const Alert = styled(AlertStripeFeil)`
	margin-bottom: 15px;
	margin-right: 15px;
`

function LoadableComponentWithRetry<T>({ onFetch, render }: LoadableComponent<T>) {
	const [loading, setLoading] = useState(true)
	const [error, setError] = useState<boolean>(false)
	const [data, setData] = useState<T>()

	const callPromise = () => {
		setLoading(true)
		setError(false)

		return onFetch()
			.then((response: T) => {
				setData(response)
				setLoading(false)
			})
			.catch(e => {
				console.error(e)
				setError(true)
				setLoading(false)
			})
	}

	useEffect(() => {
		callPromise()
	}, [])

	if (loading) {
		return <Loading />
	}
	if (error) {
		return (
			<Alert>
				Noe gikk galt! Trykk på "Prøv på nytt" eller kontakt team Dolly.
				<Knapp
					form="kompakt"
					onClick={event => {
						event.preventDefault()
						callPromise()
					}}
				>
					Prøv på nytt
				</Knapp>
			</Alert>
		)
	}

	return render(data)
}

export default LoadableComponentWithRetry
