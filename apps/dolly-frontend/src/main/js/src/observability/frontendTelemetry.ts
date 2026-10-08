import {
	captureException,
	init,
	isInitialized,
	isLocalHost,
	NaisErrorsInstrumentation,
} from '@nais/apm'
import { PerformanceInstrumentation, WebVitalsInstrumentation } from '@grafana/faro-web-sdk'

const metaContent = (name: string) =>
	document.querySelector<HTMLMetaElement>(`meta[name="${name}"]`)?.content

export function initializeFrontendTelemetry() {
	if (
		import.meta.env.MODE !== 'production' ||
		isLocalHost() ||
		metaContent('dolly-telemetry-enabled') !== 'true' ||
		isInitialized()
	) {
		return
	}

	const app = metaContent('nais-app')
	const namespace = metaContent('nais-team')
	const environment = metaContent('nais-cluster')
	const telemetryUrl = metaContent('nais-telemetry-url')
	if (!app || !namespace || !environment || !telemetryUrl) {
		console.error('Nettlesertelemetri er avslått: Nais-konfigurasjonen er ufullstendig.')
		return
	}

	init({
		app,
		namespace,
		environment,
		telemetryUrl,
		version: import.meta.env.COMMIT_HASH,
		dangerouslyDisablePiiScrubbing: false,
		devConsoleEcho: false,
		tracing: false,
		sessionReplay: { enabled: false },
		screenshotOnError: false,
		faro: {
			instrumentations: [
				new PerformanceInstrumentation(),
				new WebVitalsInstrumentation(),
				new NaisErrorsInstrumentation(),
			],
		},
	})
}

export function captureFrontendError(error: unknown) {
	if (isInitialized()) {
		captureException(error)
	}
}
