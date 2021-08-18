import React from 'react'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const CriticalError = (props: { error: string }) => {
	const generateCriticalErrorMessage = (stackTrace: string) => {
		if (stackTrace.includes('miljoer'))
			return 'Problemer med å hente gyldige miljøer. Prøv å refresh siden (ctrl + R).'
		else if (stackTrace.includes('current'))
			return 'Problemer med å hente Azure id for innlogget bruker. Prøv å refresh siden (ctrl + R).'
		else return 'Problemer med å hente dolly config. Prøv å refresh siden (ctrl + R).'
	}

	return (
		<ErrorBoundary
			error={generateCriticalErrorMessage(props.error)}
			stackTrace={props.error}
			style={{ margin: '25px auto' }}
		/>
	)
}
