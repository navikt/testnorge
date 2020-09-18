import React from 'react'
import { AppError } from './AppError'
import Logger from '~/logger'

export class ErrorBoundary extends React.Component {
	constructor(props) {
		super(props)
		this.state = {
			hasError: false
		}
	}

	static getDerivedStateFromError(error) {
		return { hasError: true }
	}

	componentDidCatch(error, info) {
		Logger.error({
			event: `Global React feil: ${error.message}`,
			message: error.message + info.componentStack,
			uuid: window.uuid
		})
	}

	render() {
		const { children, error } = this.props
		if (this.state.hasError) {
			return (
				<AppError
					message={error || 'React:ErrorBoundary - Det har skjedd en render feil'}
					error={error}
				/>
			)
		}
		return children
	}
}
