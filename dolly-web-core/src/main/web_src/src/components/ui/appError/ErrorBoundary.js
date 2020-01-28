import React from 'react'
import { AppError } from './AppError'

export class ErrorBoundary extends React.Component {
	state = {
		hasError: false
	}

	static getDerivedStateFromError(error) {
		return { hasError: true }
	}

	render() {
		const { children, error } = this.props
		if (this.state.hasError) {
			return <AppError error={error || 'React:ErrorBoundary - Det har skjedd en render feil'} />
		}

		return children
	}
}
