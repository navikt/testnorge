const hostname = window.location.hostname

export default {
	telemetryCollectorURL: 'https://telemetry.ekstern.dev.nav.no/collect',
	app: {
		name: hostname.includes('localhost')
			? 'metrics-disabled'
			: hostname.includes('idporten')
			? 'dolly-idporten'
			: hostname.includes('dev')
			? 'dolly-frontend-dev'
			: 'dolly-frontend',
		version: 'dev',
	},
}
