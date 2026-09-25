import { cleanup, render, screen, waitFor } from '@testing-library/react'
import { afterAll, afterEach, beforeAll, describe, expect, it } from 'vitest'
import { delay, http, HttpResponse } from 'msw'
import { setupServer } from 'msw/node'

import StatusPage from '@/pages/StatusPage/StatusPage'
import { FagsystemStatus } from '@/services/statusApi'

const status: FagsystemStatus = {
	systemId: 'arena',
	displayName: 'Arena',
	environment: 'Q1',
	runId: null,
	state: 'NOT_RUN',
	startedAt: null,
	completedAt: null,
	cachedUntil: null,
	cleanupAttempts: 0,
	error: null,
	technicalStatus: {
		state: 'UNKNOWN',
		checkedAt: null,
	},
}

const server = setupServer()

beforeAll(() => server.listen({ onUnhandledRequest: 'error' }))
afterEach(() => {
	cleanup()
	server.resetHandlers()
})
afterAll(() => server.close())

describe('StatusPage', () => {
	it('should sort functional checks before technical checks and blocked systems alphabetically within each group', async () => {
		const statuses: FagsystemStatus[] = [
			{ ...status, systemId: 'blocked-b', displayName: 'Blokkert B', state: 'BLOCKED' },
			{
				...status,
				systemId: 'technical-b',
				displayName: 'Teknisk B',
				state: 'TECHNICAL_ONLY',
				technicalStatus: { state: 'UP', checkedAt: null },
			},
			{ ...status, systemId: 'functional-b', displayName: 'Funksjonell B', state: 'OK' },
			{ ...status, systemId: 'blocked-a', displayName: 'Blokkert A', state: 'BLOCKED' },
			{ ...status, systemId: 'functional-a', displayName: 'Funksjonell A', state: 'VERIFY_FAILED' },
			{
				...status,
				systemId: 'technical-a',
				displayName: 'Teknisk A',
				state: 'TECHNICAL_ONLY',
				technicalStatus: { state: 'DOWN', checkedAt: null },
			},
		]
		server.use(
			http.get('/api/v1/fagsystem-statuser', () => HttpResponse.json(statuses)),
			http.post('/api/v1/testkjoringer', () => new HttpResponse(null, { status: 404 })),
		)

		render(<StatusPage />)

		const headings = await screen.findAllByRole('heading', { level: 2 })
		expect(headings.map((heading) => heading.textContent)).toEqual([
			'Funksjonell A',
			'Funksjonell B',
			'Teknisk A',
			'Teknisk B',
			'Blokkert A',
			'Blokkert B',
		])
	})

	it.each(['NOT_RUN', 'VERIFY', 'OK', 'CREATE_FAILED'] as const)(
		'should keep a system with a blocked environment last when its other environment is %s',
		async (state) => {
			server.use(
				http.get('/api/v1/fagsystem-statuser', () =>
					HttpResponse.json([
						{ ...status, state },
						{ ...status, environment: 'Q2', state: 'BLOCKED' },
						{ ...status, systemId: 'pdl', displayName: 'PDL', state: 'OK' },
						{
							...status,
							systemId: 'sigrun',
							displayName: 'Sigrun',
							state: 'TECHNICAL_ONLY',
						},
					]),
				),
				http.post('/api/v1/testkjoringer', () => new HttpResponse(null, { status: 404 })),
			)

			render(<StatusPage />)

			const headings = await screen.findAllByRole('heading', { level: 2 })
			expect(headings.map((heading) => heading.textContent)).toEqual(['PDL', 'Sigrun', 'Arena'])
		},
	)

	it('should show loading and start expired tests when the page opens', async () => {
		let starts = 0
		server.use(
			http.get('/api/v1/fagsystem-statuser', async () => {
				await delay(50)
				return HttpResponse.json([status])
			}),
			http.post('/api/v1/testkjoringer', () => {
				starts += 1
				return HttpResponse.json({ runId: 'aaf62d6f-eb87-49ce-bcef-b82ca3fd940d' }, { status: 202 })
			}),
		)

		render(<StatusPage />)

		expect(screen.getByText('Henter fagssystemstatus')).toBeInTheDocument()
		expect(await screen.findByRole('heading', { name: 'Arena' })).toBeInTheDocument()
		await waitFor(() => expect(starts).toBe(1))
	})

	it('should show a safe error when the initial request fails', async () => {
		server.use(http.get('/api/v1/fagsystem-statuser', () => HttpResponse.json({}, { status: 500 })))

		render(<StatusPage />)

		expect(await screen.findByText('Statussjekken feilet')).toBeInTheDocument()
		expect(
			screen.getByText('Vi kunne ikke starte statussjekken. Last inn siden på nytt.'),
		).toBeInTheDocument()
	})

	it('should join polling when a manual rerun conflicts with an active run', async () => {
		let statusRequests = 0
		server.use(
			http.get('/api/v1/fagsystem-statuser', () => {
				statusRequests += 1
				return HttpResponse.json([
					statusRequests === 1
						? {
								...status,
								state: 'OK',
								startedAt: '2020-09-21T09:00:00Z',
								completedAt: '2020-09-21T09:01:00Z',
							}
						: {
								...status,
								state: 'VERIFY',
								runId: 'aaf62d6f-eb87-49ce-bcef-b82ca3fd940d',
								startedAt: '2026-09-21T10:00:00Z',
							},
				])
			}),
			http.post('/api/v1/testkjoringer', () => new HttpResponse(null, { status: 404 })),
			http.post('/api/v1/fagsystemer/arena/testkjoringer', () =>
				HttpResponse.json({ message: 'En annen testkjøring pågår.' }, { status: 409 }),
			),
		)

		render(<StatusPage />)
		const button = await screen.findByRole('button', { name: 'Kjør på nytt' })
		button.click()

		expect(await screen.findByText('En annen testkjøring pågår.')).toBeInTheDocument()
		expect(await screen.findByTitle('Tester Arena')).toBeInTheDocument()
	})
})
