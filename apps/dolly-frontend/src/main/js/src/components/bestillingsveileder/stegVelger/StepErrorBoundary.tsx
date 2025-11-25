import React from 'react'
import { Logger } from '@/logger/Logger'

interface Props {
	children: React.ReactNode
	stepIndex: number
	stepLabel: string
}

export default class StepErrorBoundary extends React.Component<Props, { error: any }> {
	state = { error: null as any }

	static getDerivedStateFromError(error: any) {
		return { error }
	}

	componentDidCatch(error: any, info: any) {
		console.error('StepErrorBoundary', this.props.stepIndex, this.props.stepLabel, error?.message)
		Logger.error({
			event: `Step render feil: ${this.props.stepLabel}`,
			message: `${error?.message} | stepIndex=${this.props.stepIndex} | label=${this.props.stepLabel} | stack=${error?.stack}`,
			uuid: (window as any).uuid,
		})
	}

	render() {
		if (this.state.error) return null
		return this.props.children
	}
}
