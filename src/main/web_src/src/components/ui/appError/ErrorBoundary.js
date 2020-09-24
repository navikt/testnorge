import React from 'react'
import { AppError } from './AppError'
import Logger from '~/logger'

export class ErrorBoundary extends React.Component {
	state = {
		error: this.props.error,
		errorInfo: null
	}

	componentDidCatch(error, info) {
		this.setState({
			error: error,
			errorInfo: info
		})
		Logger.error({
			event: `Global React feil: ${error.message}`,
			message: error.message + info.componentStack,
			uuid: window.uuid
		})
	}

	render() {
		if (this.state.error) {
			const { error, errorInfo } = this.state
			return (
				<AppError
					error={error || this.props.error || 'React:ErrorBoundary - Det har skjedd en render feil'}
					stackTrace={errorInfo ? errorInfo.componentStack : 'Noe gikk galt'}
					style={this.props.style}
				/>
			)
		}
		return this.props.children
	}
}
