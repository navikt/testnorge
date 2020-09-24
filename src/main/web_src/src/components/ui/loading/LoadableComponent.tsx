import React, { useEffect, useState } from 'react'
import Loading from './Loading'

interface LoadableComponent {
	onFetch: any
	render: (data: any) => JSX.Element
}

const LoadableComponent = ({ onFetch, render }: LoadableComponent) => {
	const [loading, setLoading] = useState(true)
	const [data, setData] = useState()
	useEffect(() => {
		onFetch()
			.then((response: any) => {
				setData(response)
				setLoading(false)
			})
			.catch((error: Error) => {
				setLoading(false)
			})
	}, [])

	if (loading) {
		return <Loading />
	}

	return render(data)
}
export default LoadableComponent
