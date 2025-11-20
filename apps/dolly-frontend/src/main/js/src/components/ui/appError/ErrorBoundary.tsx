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
		console.error('Error:', error)
		console.error('Error message:', error.message)
		console.error('Error name:', error.name)
		console.error('Error toString:', error.toString())
		console.error('Component stack (RAW):', info.componentStack)
		console.error('Error stack (RAW):', error.stack)
		console.error('Current location:', window.location.href)

		const componentNames = this.extractAllComponentNames(info.componentStack || undefined)
		const fileInfo = this.extractFileInfo(error.stack)
		const fileLocationDetailed = this.extractDetailedFileLocation(info.componentStack)
		const isMinified = this.isMinifiedError(error)

		if (isMinified) {
			console.log('Minified component names:', componentNames)
			console.log('First failing component:', componentNames[0] || 'Unknown')
			console.log('Detailed file location:', fileLocationDetailed)
			console.log('Full component stack:', info.componentStack)
			console.log('Source file:', fileInfo)
		}

		this.setState({
			error: error,
			stackTrace: info.componentStack || null,
		})

		Logger.error({
			event: `Global React feil: ${error.message}`,
			message: `${error.message} | Components: ${componentNames.join(' > ')} | ${isMinified ? '[MINIFIED]' : ''} | File: ${fileInfo} | Location: ${fileLocationDetailed} | Stack: ${error.stack || 'N/A'}`,
			uuid: window.uuid,
		})
	}

	extractAllComponentNames(componentStack: string | undefined): string[] {
		if (!componentStack) return []
		const lines = componentStack.split('\n').filter((line) => line.trim())
		const names: string[] = []

		for (const line of lines) {
			const match = line.match(/^\s*(?:at\s+)?(\w+)/)
			if (match && match[1]) {
				names.push(match[1])
			}
		}

		return names.length > 0 ? names : ['Unknown']
	}

	extractFileInfo(stack: string | undefined): string {
		if (!stack) return 'Unknown'
		const match = stack.match(/at\s+(?:\w+\s+)?\(?([^)]+\.js:\d+:\d+)\)?/)
		return match ? match[1] : 'Unknown'
	}

	extractDetailedFileLocation(componentStack: string | undefined): string {
		if (!componentStack) return 'Unknown'
		const firstLine = componentStack.split('\n')[0]
		const match = firstLine.match(/\((https?:\/\/[^)]+)\)/)
		return match ? match[1] : 'Unknown'
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
