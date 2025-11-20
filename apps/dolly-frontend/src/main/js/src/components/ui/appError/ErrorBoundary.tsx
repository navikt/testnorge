import { AppError } from './AppError'
import { Logger } from '@/logger/Logger'
import React, { ErrorInfo, ReactNode } from 'react'

interface ErrorBoundaryProps {
	children?: ReactNode
	error?: Error
	stackTrace?: string
	style?: React.CSSProperties
}

interface ErrorBoundaryState {
	error: Error | null
	stackTrace: string | null
}

declare global {
	interface Window {
		uuid?: string
	}
}

export class ErrorBoundary extends React.Component<ErrorBoundaryProps, ErrorBoundaryState> {
	state: ErrorBoundaryState = {
		error: this.props.error || null,
		stackTrace: this.props.stackTrace || null,
	}

	componentDidCatch(error: Error, info: ErrorInfo) {
		console.error('🔴 ErrorBoundary caught error 🔴')
		console.error('Error:', error)
		console.error('Error message:', error.message)
		console.error('Component stack:', info.componentStack)
		console.error('Current location:', window.location.href)

		const componentName = this.extractComponentName(info.componentStack || undefined)
		const isMinified = this.isMinifiedError(error)

		if (isMinified) {
			console.error('⚠️ Minified error detected - production build')
			console.error('Component causing error:', componentName || 'Unknown')
			console.error('Error stack:', error.stack)
		}

		this.setState({
			error: error,
			stackTrace: info.componentStack || null,
		})

		Logger.error({
			event: `Global React feil: ${error.message}`,
			message: `${error.message}${info.componentStack}${isMinified ? ' [MINIFIED]' : ''} | Component: ${componentName || 'Unknown'} | Stack: ${error.stack || 'N/A'}`,
			uuid: window.uuid,
		})
	}

	extractComponentName(componentStack: string | undefined): string | null {
		if (!componentStack) return null
		const match = componentStack.match(/^\s*(?:at\s+)?(\w+)/)
		return match ? match[1] : null
	}

	isMinifiedError(error: Error): boolean {
		return !!(
			error.message &&
			(error.message.includes('Minified React error') ||
				/^[A-Z]\d+$/.test(error.message) ||
				error.message.length < 10)
		)
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
