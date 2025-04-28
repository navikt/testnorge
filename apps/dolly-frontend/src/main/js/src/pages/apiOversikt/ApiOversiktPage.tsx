import React from 'react'
import { useApiOversikt } from '@/utils/hooks/useApiOversikt'

export default () => {
	const { apiOversikt, loading, error } = useApiOversikt()
	console.log('apiOversikt: ', apiOversikt) //TODO - SLETT MEG

	return (
		<>
			<h1>API-oversikt</h1>
		</>
	)
}
