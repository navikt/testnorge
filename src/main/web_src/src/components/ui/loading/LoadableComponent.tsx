import React, { useEffect, useState } from 'react'
import Loading from './Loading'
import { DollyErrorAlert } from './DollyErrorAlert'
import { v4 as Uuid } from 'uuid'
import Logger from '~/logger'

interface LoadableComponent {
	onFetch: any
	renderOnError?: (error: Error) => JSX.Element
	render: (data: any) => JSX.Element
}

const LoadableComponent = ({ onFetch, render, renderOnError }: LoadableComponent) => {
	const [loading, setLoading] = useState(true)
	const [error, setError] = useState()
	const [data, setData] = useState()
	const [uuid, setUuid] = useState()
	useEffect(() => {
		onFetch()
			.then((response: any) => {
				setData(response)
				setLoading(false)
			})
			.catch((error: Error) => {
				console.error(error.stack)
				setUuid(Uuid())
				Logger.error({
					event: error.stack,
					message: 'Error i LoadableComponent onFetch'
				})
				setError(error)
				setLoading(false)
			})
	}, [])

	if (error) {
		return renderOnError ? renderOnError(error) : <DollyErrorAlert error={error} />
	}

	if (loading) {
		return <Loading />
	}

	return render(data)
}
export default LoadableComponent
