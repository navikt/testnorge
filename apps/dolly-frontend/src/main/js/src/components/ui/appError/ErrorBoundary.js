import React from 'react'
import { AppError } from './AppError'
import Logger from '~/logger'

export class ErrorBoundary extends React.Component {
	state = {
		error: this.props.error,
		stackTrace: this.props.stackTrace,
	}

	triggerError = ({ error, errorInfo }) => {
		this.setState({
			error: error,
			stackTrace: errorInfo.componentStack,
		})
	}

	componentDidCatch(error, info) {
		console.log('error: ', error) //TODO - SLETT MEG
		console.log('info: ', info) //TODO - SLETT MEG
		this.setState({
			error: error,
			stackTrace: info.componentStack,
		})
		Logger.error({
			event: `Global React feil: ${error.message}`,
			message: error.message + info.componentStack,
			uuid: window.uuid,
		})
	}

	render() {
		if (this.state.error) {
			const { error, stackTrace } = this.state
			return (
				<AppError
					error={error || this.props.error || 'React:ErrorBoundary - Det har skjedd en render feil'}
					stackTrace={stackTrace || 'Noe gikk galt'}
					style={this.props.style}
				/>
			)
		}
		return this.props.children
	}
}
