import React from 'react'
import { Logger } from '@/logger/Logger'
import { Alert, Button } from '@navikt/ds-react'
import { isChunkLoadError } from '@/utils/chunkErrorUtils'

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

	componentDidUpdate(prevProps: Props) {
		if (prevProps.stepIndex !== this.props.stepIndex) {
			this.setState({ error: null })
		}
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
		if (this.state.error) {
			const chunkError = isChunkLoadError(this.state.error)
			return (
				<Alert variant="error" style={{ margin: '1rem 0' }}>
					{chunkError
						? 'En ny versjon av Dolly er tilgjengelig. Siden bør lastes inn på nytt for å unngå problemer.'
						: `Noe gikk galt ved visning av "${this.props.stepLabel}".`}
					<div style={{ marginTop: '0.5rem' }}>
						<Button variant="secondary" size="small" onClick={() => window.location.reload()}>
							Last inn siden på nytt
						</Button>
					</div>
				</Alert>
			)
		}
		return this.props.children
	}
}
