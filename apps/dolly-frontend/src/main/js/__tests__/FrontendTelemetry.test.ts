import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { captureException, init } from '@nais/apm'
import type { TransportBody } from '@grafana/faro-web-sdk'
import {
	captureFrontendError,
	initializeFrontendTelemetry,
} from '@/observability/frontendTelemetry'

const state = vi.hoisted(() => ({
	initialized: false,
	local: false,
}))

vi.mock('@nais/apm', async (importOriginal) => {
	const actual = await importOriginal<typeof import('@nais/apm')>()
	return {
		...actual,
		init: vi.fn(),
		captureException: vi.fn(),
		isInitialized: () => state.initialized,
		isLocalHost: () => state.local,
	}
})

function setMetadata(enabled = 'true', app = 'dolly-frontend-dev') {
	for (const [name, content] of Object.entries({
		'dolly-telemetry-enabled': enabled,
		'nais-app': app,
		'nais-team': 'dolly',
		'nais-cluster': 'dev-gcp',
		'nais-telemetry-url': 'https://telemetry.ekstern.dev.nav.no/collect',
	})) {
		const meta = document.createElement('meta')
		meta.name = name
		meta.content = content
		document.head.appendChild(meta)
	}
}

describe('Frontend telemetry', () => {
	beforeEach(() => {
		vi.clearAllMocks()
		vi.stubEnv('MODE', 'production')
		vi.stubEnv('COMMIT_HASH', 'test-release')
		state.initialized = false
		state.local = false
	})

	afterEach(() => {
		document
			.querySelectorAll('meta[name^="nais-"], meta[name="dolly-telemetry-enabled"]')
			.forEach((meta) => meta.remove())
		vi.unstubAllEnvs()
		vi.restoreAllMocks()
	})

	it.each(['false', ''])('should not initialize when telemetry is disabled (%s)', (enabled) => {
		setMetadata(enabled)

		initializeFrontendTelemetry()

		expect(init).not.toHaveBeenCalled()
	})

	it('should not initialize without runtime configuration', () => {
		initializeFrontendTelemetry()

		expect(init).not.toHaveBeenCalled()
	})

	it.each(['test', 'local-dev', 'development'])('should not initialize in %s mode', (mode) => {
		setMetadata()
		vi.stubEnv('MODE', mode)

		initializeFrontendTelemetry()

		expect(init).not.toHaveBeenCalled()
	})

	it('should not initialize a production build on localhost', () => {
		setMetadata()
		state.local = true

		initializeFrontendTelemetry()

		expect(init).not.toHaveBeenCalled()
	})

	it.each(['dolly-frontend', 'dolly-frontend-dev', 'dolly-idporten'])(
		'should configure only errors and performance signals for %s',
		(app) => {
			setMetadata('true', app)

			initializeFrontendTelemetry()

			expect(init).toHaveBeenCalledExactlyOnceWith({
				app,
				namespace: 'dolly',
				environment: 'dev-gcp',
				telemetryUrl: 'https://telemetry.ekstern.dev.nav.no/collect',
				version: 'test-release',
				dangerouslyDisablePiiScrubbing: false,
				devConsoleEcho: false,
				tracing: false,
				sessionReplay: { enabled: false },
				screenshotOnError: false,
				faro: {
					instrumentations: expect.any(Array),
				},
			})
			const options = vi.mocked(init).mock.calls[0][0]
			expect(options?.faro?.instrumentations?.map(({ name }) => name)).toEqual([
				'@grafana/faro-web-sdk:instrumentation-performance',
				'@grafana/faro-web-sdk:instrumentation-web-vitals',
				'@nais/apm-errors-instrumentation',
			])
		},
	)

	it.each(['nais-app', 'nais-team', 'nais-cluster', 'nais-telemetry-url'])(
		'should reject enabled telemetry without %s',
		(name) => {
			setMetadata()
			document.querySelector(`meta[name="${name}"]`)?.remove()
			const error = vi.spyOn(console, 'error').mockImplementation(() => {})

			initializeFrontendTelemetry()

			expect(init).not.toHaveBeenCalled()
			expect(error).toHaveBeenCalledWith(
				'Nettlesertelemetri er avslått: Nais-konfigurasjonen er ufullstendig.',
			)
		},
	)

	it('should not initialize twice', () => {
		setMetadata()
		state.initialized = true

		initializeFrontendTelemetry()

		expect(init).not.toHaveBeenCalled()
	})

	it('should not forward React errors before initialization', () => {
		captureFrontendError(new Error('synthetic render error'))

		expect(captureException).not.toHaveBeenCalled()
	})

	it('should forward React errors after initialization', () => {
		state.initialized = true
		const error = new Error('synthetic render error')

		captureFrontendError(error)

		expect(captureException).toHaveBeenCalledExactlyOnceWith(error)
	})

	it('should send scrubbed errors, Web Vitals and page load data through the real SDK', async () => {
		setMetadata()
		initializeFrontendTelemetry()
		const options = vi.mocked(init).mock.calls[0][0]
		const sdk = await vi.importActual<typeof import('@nais/apm')>('@nais/apm')
		const fetchSpy = vi
			.spyOn(globalThis, 'fetch')
			.mockResolvedValue(new Response(null, { status: 202 }))
		const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
		sessionStorage.setItem('telemetry-test-private-value', 'not-for-telemetry')

		const instance = sdk.init({
			...options,
			faro: { ...options?.faro, batching: { enabled: false } },
		})
		const payloads = (): TransportBody[] =>
			fetchSpy.mock.calls.flatMap(([, request]) =>
				typeof request?.body === 'string' ? [JSON.parse(request.body)] : [],
			)

		try {
			sdk.captureException(new Error('synthetic failure for 01817012345'))
			sdk.pushMeasurement('web-vitals', { LCP: 120, CLS: 0.01 })
			console.error('synthetic-console-marker')

			await vi.waitFor(() => {
				expect(payloads().flatMap((body) => body.exceptions ?? [])).toEqual(
					expect.arrayContaining([
						expect.objectContaining({ value: 'synthetic failure for [fnr]' }),
					]),
				)
				expect(payloads().flatMap((body) => body.measurements ?? [])).toEqual(
					expect.arrayContaining([
						expect.objectContaining({ type: 'web-vitals', values: { LCP: 120, CLS: 0.01 } }),
					]),
				)
				expect(payloads().flatMap((body) => body.events ?? [])).toEqual(
					expect.arrayContaining([
						expect.objectContaining({ name: 'faro.performance.navigation' }),
					]),
				)
			})

			for (const [url, request] of fetchSpy.mock.calls) {
				expect(url).toBe('https://telemetry.ekstern.dev.nav.no/collect')
				const headers = new Headers(request?.headers)
				expect(headers.has('Authorization')).toBe(false)
				expect(headers.has('Idempotency-Key')).toBe(false)
			}
			for (const body of payloads()) {
				expect(body.meta.app).toMatchObject({
					name: 'dolly-frontend-dev',
					namespace: 'dolly',
					environment: 'dev-gcp',
					version: 'test-release',
				})
				expect(body.meta.user).toBeUndefined()
			}
			expect(JSON.stringify(payloads())).not.toContain('01817012345')
			expect(JSON.stringify(payloads())).not.toContain('not-for-telemetry')
			expect(JSON.stringify(payloads())).not.toContain('synthetic-console-marker')
			expect(console.error).toBe(consoleSpy)

			fetchSpy.mockResolvedValueOnce(new Response(null, { status: 503 }))
			expect(() => sdk.captureException(new Error('second synthetic error'))).not.toThrow()
			await vi.waitFor(() =>
				expect(payloads().flatMap((body) => body.exceptions ?? [])).toEqual(
					expect.arrayContaining([expect.objectContaining({ value: 'second synthetic error' })]),
				),
			)
		} finally {
			instance.pause()
			instance.instrumentations.remove(...instance.instrumentations.instrumentations)
			sessionStorage.removeItem('telemetry-test-private-value')
		}
	})
})
