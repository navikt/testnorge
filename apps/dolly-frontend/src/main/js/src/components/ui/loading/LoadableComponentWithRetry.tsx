import React, { useEffect, useState } from 'react'
import Loading from './Loading'
import styled from 'styled-components'
import { Alert, Button } from '@navikt/ds-react'

interface LoadableComponent<T> {
	onFetch: () => Promise<T>
	render: (data: T) => JSX.Element
	label?: string
}

const Knapp = styled(Button)`
	margin-top: 15px;
	display: block;
`

const AlertError = styled(Alert)`
	margin-bottom: 15px;
	margin-right: 15px;
`

function LoadableComponentWithRetry<T>({ onFetch, render, label }: LoadableComponent<T>) {
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
			.catch((e) => {
				console.error(e)
				setError(true)
				setLoading(false)
			})
	}

	useEffect(() => {
		callPromise()
	}, [])

	if (loading) {
		return <Loading label={label} />
	}
	if (error) {
		return (
			<AlertError variant={'error'}>
				Noe gikk galt! Trykk på "Prøv på nytt" eller kontakt Team Dolly.
				<Knapp
					form="kompakt"
					onClick={(event) => {
						event.preventDefault()
						callPromise()
					}}
				>
					Prøv på nytt
				</Knapp>
			</AlertError>
		)
	}

	return render(data)
}

export default LoadableComponentWithRetry
