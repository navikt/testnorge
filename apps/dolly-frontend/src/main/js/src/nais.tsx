const hostname = window.location.hostname

export default {
	telemetryCollectorURL: 'https://telemetry.ekstern.dev.nav.no/collect',
	app: {
		name: hostname.includes('localhost')
			? 'dolly-lokal'
			: hostname.includes('idporten')
			? 'dolly-idporten'
			: 'dolly-frontend',
		version: 'dev',
	},
}
