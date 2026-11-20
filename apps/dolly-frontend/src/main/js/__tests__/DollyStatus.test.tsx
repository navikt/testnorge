import { act, render, renderHook, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { SWRConfig } from 'swr'
import type { PropsWithChildren } from 'react'
import { http, HttpResponse } from 'msw/http'
import { vi } from 'vitest'
import api from '@/api'
import { DollyStatus } from '@/components/layout/header/DollyStatus'
import {
	cancelStatusRetry,
	scheduleStatusRetry,
	useAutomatiskDollyStatusKjoring,
	type DollyStatusOversikt,
} from '@/utils/hooks/useDollyStatus'
import { gjeldendeAzureBrukerMock, gjeldendeBankidBrukerMock } from '#/mocks/BasicMocks'
import { worker } from './mocks/browser'
import { dollyTest } from '../vitest.setup'

const statusoversiktUrl = '/testnav-dollystatus/api/v1/statusoversikt'
const testkjoringerUrl = '/testnav-dollystatus/api/v1/testkjoringer'
const feiledeTestkjoringerUrl = `${testkjoringerUrl}/feilede`
const brukerUrl = '/dolly-backend/api/v1/bruker/current'

const createStatusoversikt = (
	overrides: Partial<DollyStatusOversikt> = {},
): DollyStatusOversikt => ({
	status: 'OK',
	kjoringPagaar: false,
	sistOppdatert: new Date().toISOString(),
	nesteAutomatiskeKjoring: new Date(Date.now() + 5 * 60 * 60 * 1000).toISOString(),
	nesteManuelleKjoringTidligst: null,
	problemer: [],
	...overrides,
})

const mockStatusoversikt = (
	statusoversikt: DollyStatusOversikt,
	onRequest?: (backgroundHeader: string | null) => void,
) => {
	worker.use(
		http.get(statusoversiktUrl, ({ request }) => {
			onRequest?.(request.headers.get('X-Dolly-Background'))
			return HttpResponse.json(statusoversikt)
		}),
	)
}

const mockBruker = (bruker: typeof gjeldendeAzureBrukerMock | typeof gjeldendeBankidBrukerMock) => {
	worker.use(
		http.get(brukerUrl, () => {
			return HttpResponse.json(bruker)
		}),
	)
}

const renderDollyStatus = () => {
	render(
		<SWRConfig value={{ provider: () => new Map() }}>
			<DollyStatus />
		</SWRConfig>,
	)
}

dollyTest('shows the accessible name for an OK status', async () => {
	const backgroundHeaderSpy = vi.fn()
	mockStatusoversikt(createStatusoversikt(), backgroundHeaderSpy)
	renderDollyStatus()

	expect(
		await screen.findByRole('button', { name: 'Systemstatus: Alt fungerer' }),
	).toBeInTheDocument()
	expect(backgroundHeaderSpy).toHaveBeenCalledWith('true')
})

dollyTest('shows problem details for a failing status', async () => {
	mockStatusoversikt(
		createStatusoversikt({
			status: 'FEIL',
			problemer: [
				{
					systemId: 'FREG',
					navn: 'Folkeregisteret',
					miljo: 'q1',
					tilstand: 'FEIL',
					melding: 'Tidsavbrudd',
					tidspunkt: null,
				},
				{
					systemId: 'PDL',
					navn: 'Persondata',
					miljo: null,
					tilstand: 'FEIL',
					melding: null,
					tidspunkt: null,
				},
			],
		}),
	)
	renderDollyStatus()

	const button = await screen.findByRole('button', { name: 'Systemstatus: Feil i 2 systemer' })
	await userEvent.click(button)

	expect(screen.getByText('Folkeregisteret (q1)')).toBeInTheDocument()
	expect(screen.getByText('Tidsavbrudd')).toBeInTheDocument()
	expect(screen.getByText('Persondata')).toBeInTheDocument()
})

dollyTest('hides the retry action for BANKID users', async () => {
	mockStatusoversikt(
		createStatusoversikt({
			status: 'FEIL',
			problemer: [
				{
					systemId: 'FREG',
					navn: 'Folkeregisteret',
					miljo: null,
					tilstand: 'FEIL',
					melding: null,
					tidspunkt: null,
				},
			],
		}),
	)
	mockBruker(gjeldendeBankidBrukerMock)
	renderDollyStatus()

	await userEvent.click(
		await screen.findByRole('button', { name: 'Systemstatus: Feil i 1 system' }),
	)
	expect(
		screen.queryByRole('button', { name: 'Kjør feilende tester på nytt' }),
	).not.toBeInTheDocument()
})

dollyTest('hides the retry action when there are no problems', async () => {
	mockStatusoversikt(createStatusoversikt())
	mockBruker(gjeldendeAzureBrukerMock)
	renderDollyStatus()

	await userEvent.click(await screen.findByRole('button', { name: 'Systemstatus: Alt fungerer' }))
	expect(
		screen.queryByRole('button', { name: 'Kjør feilende tester på nytt' }),
	).not.toBeInTheDocument()
})

dollyTest('shows a manual test action for an unknown status for Azure users', async () => {
	mockStatusoversikt(createStatusoversikt({ status: 'UKJENT' }))
	mockBruker(gjeldendeAzureBrukerMock)
	const postSpy = vi.fn()
	worker.use(
		http.post(testkjoringerUrl, ({ request }) => {
			postSpy(request.url)
			return HttpResponse.json({ runId: 'test-run' }, { status: 202 })
		}),
	)
	renderDollyStatus()

	await userEvent.click(await screen.findByRole('button', { name: 'Systemstatus: Ukjent' }))
	await userEvent.click(await screen.findByRole('button', { name: 'Kjør tester' }))

	await waitFor(() => expect(postSpy).toHaveBeenCalledOnce())
	expect(postSpy).toHaveBeenCalledWith(expect.stringContaining(testkjoringerUrl))
})

dollyTest('shows only the run-all action for an unknown status with stale problems', async () => {
	mockStatusoversikt(
		createStatusoversikt({
			status: 'UKJENT',
			problemer: [
				{
					systemId: 'FREG',
					navn: 'Folkeregisteret',
					miljo: null,
					tilstand: 'VERIFY_FAILED',
					melding: null,
					tidspunkt: null,
				},
			],
		}),
	)
	mockBruker(gjeldendeAzureBrukerMock)
	renderDollyStatus()

	await userEvent.click(await screen.findByRole('button', { name: 'Systemstatus: Ukjent' }))

	expect(await screen.findByRole('button', { name: 'Kjør tester' })).toBeInTheDocument()
	expect(
		screen.queryByRole('button', { name: 'Kjør feilende tester på nytt' }),
	).not.toBeInTheDocument()
})

dollyTest('hides the manual test action for BANKID users with an unknown status', async () => {
	mockStatusoversikt(createStatusoversikt({ status: 'UKJENT' }))
	mockBruker(gjeldendeBankidBrukerMock)
	renderDollyStatus()

	await userEvent.click(await screen.findByRole('button', { name: 'Systemstatus: Ukjent' }))

	expect(screen.queryByRole('button', { name: 'Kjør tester' })).not.toBeInTheDocument()
})

dollyTest('disables the retry action until the manual deadline', async () => {
	mockStatusoversikt(
		createStatusoversikt({
			status: 'FEIL',
			problemer: [
				{
					systemId: 'FREG',
					navn: 'Folkeregisteret',
					miljo: null,
					tilstand: 'FEIL',
					melding: null,
					tidspunkt: null,
				},
			],
			nesteManuelleKjoringTidligst: new Date(Date.now() + 60 * 60 * 1000).toISOString(),
		}),
	)
	renderDollyStatus()

	await userEvent.click(
		await screen.findByRole('button', { name: 'Systemstatus: Feil i 1 system' }),
	)

	const retryButton = await screen.findByRole('button', { name: /Kan kjøres igjen kl\./ })
	expect(retryButton).toBeDisabled()
})

dollyTest('starts retrying failed tests when the manual deadline has passed', async () => {
	mockStatusoversikt(
		createStatusoversikt({
			status: 'FEIL',
			problemer: [
				{
					systemId: 'FREG',
					navn: 'Folkeregisteret',
					miljo: null,
					tilstand: 'FEIL',
					melding: null,
					tidspunkt: null,
				},
			],
			nesteManuelleKjoringTidligst: new Date(Date.now() - 60 * 60 * 1000).toISOString(),
		}),
	)
	const postSpy = vi.fn()
	worker.use(
		http.post(feiledeTestkjoringerUrl, ({ request }) => {
			postSpy(request.url)
			return HttpResponse.json({ runId: 'test-run' }, { status: 202 })
		}),
	)
	renderDollyStatus()

	await userEvent.click(
		await screen.findByRole('button', { name: 'Systemstatus: Feil i 1 system' }),
	)
	await userEvent.click(await screen.findByRole('button', { name: 'Kjør feilende tester på nytt' }))

	await waitFor(() => expect(postSpy).toHaveBeenCalledOnce())
	expect(postSpy).toHaveBeenCalledWith(expect.stringContaining(feiledeTestkjoringerUrl))
})

dollyTest('shows a cooldown message when failed tests were started recently', async () => {
	mockStatusoversikt(
		createStatusoversikt({
			status: 'FEIL',
			problemer: [
				{
					systemId: 'FREG',
					navn: 'Folkeregisteret',
					miljo: null,
					tilstand: 'FEIL',
					melding: null,
					tidspunkt: null,
				},
			],
			nesteManuelleKjoringTidligst: new Date(Date.now() - 60 * 60 * 1000).toISOString(),
		}),
	)
	worker.use(
		http.post(feiledeTestkjoringerUrl, () => {
			return HttpResponse.json({ message: 'Cooling down' }, { status: 429 })
		}),
		http.get('/session/csrf', () => {
			return HttpResponse.json({
				headerName: 'X-CSRF-TOKEN',
				parameterName: '_csrf',
				token: 'test-token',
			})
		}),
		http.post('/api/dolly-logg', () => {
			return HttpResponse.json({})
		}),
	)
	renderDollyStatus()

	await userEvent.click(
		await screen.findByRole('button', { name: 'Systemstatus: Feil i 1 system' }),
	)
	await userEvent.click(await screen.findByRole('button', { name: 'Kjør feilende tester på nytt' }))

	expect(
		await screen.findByText('Testene ble startet nylig. Prøv igjen om noen minutter.'),
	).toHaveAttribute('role', 'status')

	const statusknapp = screen.getByTestId('dolly-status-knapp')
	await userEvent.click(statusknapp)
	await userEvent.click(statusknapp)

	expect(
		screen.queryByText('Testene ble startet nylig. Prøv igjen om noen minutter.'),
	).not.toBeInTheDocument()
})

dollyTest('retries an automatic test run once after a failed POST', async () => {
	vi.useFakeTimers()
	vi.spyOn(Math, 'random').mockReturnValue(0)
	const fetchSpy = vi
		.spyOn(api, 'fetch')
		.mockRejectedValueOnce(Object.assign(new Error('Server error'), { status: 500 }))
		.mockResolvedValueOnce(new Response(null, { status: 202 }))
	const wrapper = ({ children }: PropsWithChildren) => (
		<SWRConfig value={{ provider: () => new Map() }}>{children}</SWRConfig>
	)
	const statusoversikt = createStatusoversikt({
		nesteAutomatiskeKjoring: new Date(Date.now() + 10_000).toISOString(),
	})

	const { unmount } = renderHook(() => useAutomatiskDollyStatusKjoring(statusoversikt, true), {
		wrapper,
	})
	try {
		await act(async () => {
			await vi.advanceTimersByTimeAsync(10_000)
		})
		expect(fetchSpy).toHaveBeenCalledOnce()

		await act(async () => {
			await vi.advanceTimersByTimeAsync(60_000)
		})
		expect(fetchSpy).toHaveBeenCalledTimes(2)
		expect(fetchSpy).toHaveBeenLastCalledWith(
			testkjoringerUrl,
			expect.objectContaining({ headers: { 'X-Dolly-Background': 'true' } }),
		)
	} finally {
		unmount()
		vi.useRealTimers()
		vi.restoreAllMocks()
	}
})

dollyTest('pauses automatic runs after three 202 responses without a new deadline', async () => {
	vi.useFakeTimers()
	vi.spyOn(Math, 'random').mockReturnValue(0)
	const fetchSpy = vi
		.spyOn(api, 'fetch')
		.mockImplementation(async () => new Response(null, { status: 202 }))
	const wrapper = ({ children }: PropsWithChildren) => (
		<SWRConfig value={{ provider: () => new Map() }}>{children}</SWRConfig>
	)
	const statusoversikt = createStatusoversikt({
		nesteAutomatiskeKjoring: new Date(Date.now() + 10_000).toISOString(),
	})

	const { unmount } = renderHook(() => useAutomatiskDollyStatusKjoring(statusoversikt, true), {
		wrapper,
	})
	try {
		await act(async () => {
			await vi.advanceTimersByTimeAsync(10_000)
		})
		expect(fetchSpy).toHaveBeenCalledOnce()

		await act(async () => {
			await vi.advanceTimersByTimeAsync(125_000)
		})
		expect(fetchSpy).toHaveBeenCalledTimes(3)

		await act(async () => {
			await vi.advanceTimersByTimeAsync(14 * 60_000)
		})
		expect(fetchSpy).toHaveBeenCalledTimes(3)

		await act(async () => {
			await vi.advanceTimersByTimeAsync(90_000)
		})
		expect(fetchSpy).toHaveBeenCalledTimes(4)
	} finally {
		unmount()
		vi.useRealTimers()
		vi.restoreAllMocks()
	}
})

dollyTest('keeps a single pending status retry when several requests fail', async () => {
	cancelStatusRetry()
	vi.useFakeTimers()
	const revalidate = vi.fn()
	try {
		scheduleStatusRetry(revalidate)
		scheduleStatusRetry(revalidate)
		scheduleStatusRetry(revalidate)

		await vi.advanceTimersByTimeAsync(60_000)
		expect(revalidate).toHaveBeenCalledOnce()

		scheduleStatusRetry(revalidate)
		await vi.advanceTimersByTimeAsync(60_000)
		expect(revalidate).toHaveBeenCalledTimes(2)
	} finally {
		vi.useRealTimers()
	}
})

dollyTest('shows an unknown status and fetch error details for a 404 JSON response', async () => {
	worker.use(
		http.get(statusoversiktUrl, () => {
			return HttpResponse.json({ status: 404, error: 'Not Found' }, { status: 404 })
		}),
	)
	renderDollyStatus()

	await userEvent.click(await screen.findByRole('button', { name: 'Systemstatus: Ukjent' }))

	expect(await screen.findByText('Fikk ikke hentet status.')).toBeInTheDocument()
})

dollyTest('shows an unknown status and fetch error details for an HTML response', async () => {
	worker.use(
		http.get(statusoversiktUrl, () => {
			return HttpResponse.text('<html><body>Login</body></html>', {
				headers: { 'Content-Type': 'text/html' },
			})
		}),
	)
	renderDollyStatus()

	await userEvent.click(await screen.findByRole('button', { name: 'Systemstatus: Ukjent' }))

	expect(await screen.findByText('Fikk ikke hentet status.')).toBeInTheDocument()
})

dollyTest(
	'shows an unknown status and fetch error details when status retrieval fails',
	async () => {
		worker.use(
			http.get(statusoversiktUrl, () => {
				return new HttpResponse(null, { status: 500 })
			}),
		)
		renderDollyStatus()

		await userEvent.click(await screen.findByRole('button', { name: 'Systemstatus: Ukjent' }))

		expect(await screen.findByText('Fikk ikke hentet status.')).toBeInTheDocument()
	},
)

dollyTest('shows that tests are running when the status is unknown', async () => {
	mockStatusoversikt(
		createStatusoversikt({
			status: 'UKJENT',
			kjoringPagaar: true,
			nesteAutomatiskeKjoring: null,
		}),
	)
	mockBruker(gjeldendeAzureBrukerMock)
	renderDollyStatus()

	await userEvent.click(
		await screen.findByRole('button', { name: 'Systemstatus: Ukjent. Testkjøring pågår' }),
	)

	expect(
		await screen.findByText('Testene kjører. Status oppdateres når kjøringen er ferdig.'),
	).toBeInTheDocument()
	expect(screen.queryByText('Status er ukjent.')).not.toBeInTheDocument()
	expect(screen.queryByRole('button', { name: 'Kjør tester' })).not.toBeInTheDocument()
})

dollyTest('keeps the known status and shows a running test run', async () => {
	mockStatusoversikt(createStatusoversikt({ kjoringPagaar: true, nesteAutomatiskeKjoring: null }))
	mockBruker(gjeldendeAzureBrukerMock)
	renderDollyStatus()

	await userEvent.click(
		await screen.findByRole('button', { name: 'Systemstatus: Alt fungerer. Testkjøring pågår' }),
	)

	expect(await screen.findByText('Alle fagsystem OK.')).toBeInTheDocument()
	expect(screen.getByText('Ny testkjøring pågår.')).toBeInTheDocument()
})
