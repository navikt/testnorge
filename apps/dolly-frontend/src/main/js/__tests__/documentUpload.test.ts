import { beforeEach, describe, expect, vi } from 'vitest'
import { http, HttpResponse } from 'msw'
import { dollyTest } from '../vitest.setup'
import { worker } from './mocks/browser'
import { sendBestilling } from '@/ducks/bestilling'
import { DollyApi } from '@/service/Api'

vi.mock('@/service/Api', () => ({
	DollyApi: {
		createBestilling: vi.fn(),
		createBestillingLeggTilPaaPerson: vi.fn(),
		createBestillingFraEksisterendeIdenter: vi.fn(),
		createBestillingLeggTilPaaGruppe: vi.fn(),
		createOrganisasjonBestilling: vi.fn(),
		importerPersonerFraPdl: vi.fn(),
	},
}))

vi.mock('@/components/bestillingsveileder/utils', () => ({
	getLeggTilIdent: vi.fn(),
	rootPaths: [],
}))

vi.mock('@/logger/Logger', () => ({
	Logger: { trace: vi.fn(), error: vi.fn() },
}))

const uploadUrl = '/dolly-backend/api/v1/dokument/upload'
const chunkSize = 4 * 1024 * 1024

const dokarkivValues = (fysiskDokument: string) => ({
	dokarkiv: [{ dokumenter: [{ dokumentvarianter: [{ fysiskDokument }] }] }],
})

describe('document upload', () => {
	beforeEach(() => {
		vi.clearAllMocks()
	})

	dollyTest(
		'should upload forty MiB in fourteen sequential parts before submitting',
		async () => {
			const encodedLength = 4 * Math.ceil((40 * 1024 * 1024) / 3)
			const document =
				Array.from({ length: 13 }, (_, index) =>
					String.fromCharCode(65 + index).repeat(chunkSize),
				).join('') +
				'A'.repeat(encodedLength - 13 * chunkSize - 2) +
				'=='
			const lengths: number[] = []
			let receivedLength = 0
			let activeRequests = 0
			let maxActiveRequests = 0
			const dispatch = vi.fn().mockResolvedValue({
				action: { type: 'bestveil/postBestilling', payload: { data: {} } },
			})
			const navigate = vi.fn()

			worker.use(
				http.post(`${uploadUrl}/init`, () => HttpResponse.text('upload-id', { status: 201 })),
				http.post<{ uploadId: string }, { data: string }>(
					`${uploadUrl}/:uploadId/append`,
					async ({ request, params }) => {
						activeRequests++
						maxActiveRequests = Math.max(maxActiveRequests, activeRequests)
						const { data } = await request.json()
						expect(params.uploadId).toBe('upload-id')
						expect(data).toBe(document.slice(receivedLength, receivedLength + chunkSize))
						expect(dispatch).not.toHaveBeenCalled()
						expect(DollyApi.createBestilling).not.toHaveBeenCalled()
						receivedLength += data.length
						lengths.push(data.length)
						activeRequests--
						return new HttpResponse(null, { status: 200 })
					},
				),
			)

			await sendBestilling(dokarkivValues(document), { is: {} }, 1, navigate)(dispatch)

			expect(lengths).toEqual([
				...Array<number>(13).fill(chunkSize),
				encodedLength - 13 * chunkSize,
			])
			expect(receivedLength).toBe(encodedLength)
			expect(maxActiveRequests).toBe(1)
			expect(DollyApi.createBestilling).toHaveBeenCalledWith(1, {
				dokarkiv: [{ dokumenter: [{ dokumentvarianter: [{ uploadReferanse: 'upload-id' }] }] }],
			})
			expect(dispatch).toHaveBeenCalledOnce()
			expect(navigate).toHaveBeenCalledWith('/gruppe/1')
		},
		30_000,
	)

	dollyTest(
		'should use the same chunk size for Histark including the final partial part',
		async () => {
			const document = 'A'.repeat(chunkSize) + 'YQ=='
			const chunks: string[] = []
			const dispatch = vi.fn().mockResolvedValue({
				action: { type: 'bestveil/postBestilling', payload: { data: {} } },
			})

			worker.use(
				http.post(`${uploadUrl}/init`, () => HttpResponse.text('histark-upload', { status: 201 })),
				http.post<{ uploadId: string }, { data: string }>(
					`${uploadUrl}/:uploadId/append`,
					async ({ request, params }) => {
						expect(params.uploadId).toBe('histark-upload')
						const { data } = await request.json()
						chunks.push(data)
						return new HttpResponse(null, { status: 200 })
					},
				),
			)

			await sendBestilling(
				{ histark: { dokumenter: [{ fysiskDokument: document }] } },
				{ is: {} },
				1,
				vi.fn(),
			)(dispatch)

			expect(chunks.map((chunk) => chunk.length)).toEqual([chunkSize, 4])
			expect(chunks.join('')).toBe(document)
			expect(DollyApi.createBestilling).toHaveBeenCalledWith(1, {
				histark: { dokumenter: [{ uploadReferanse: 'histark-upload' }] },
			})
		},
	)

	dollyTest.each([400, 500, 'network'] as const)(
		'should stop without retrying or submitting when append fails with %s',
		async (failure) => {
			const append = vi.fn(() =>
				failure === 'network' ? HttpResponse.error() : new HttpResponse(null, { status: failure }),
			)
			worker.use(
				http.post(`${uploadUrl}/init`, () => HttpResponse.text('upload-id', { status: 201 })),
				http.post(`${uploadUrl}/:uploadId/append`, append),
			)
			const dispatch = vi.fn()
			const navigate = vi.fn()

			await expect(
				sendBestilling(
					dokarkivValues('A'.repeat(chunkSize + 4)),
					{ is: {} },
					1,
					navigate,
				)(dispatch),
			).rejects.toThrow()

			expect(append).toHaveBeenCalledOnce()
			expect(DollyApi.createBestilling).not.toHaveBeenCalled()
			expect(dispatch).not.toHaveBeenCalled()
			expect(navigate).not.toHaveBeenCalled()
		},
	)

	dollyTest('should not append or submit when upload initialization fails', async () => {
		const append = vi.fn()
		worker.use(
			http.post(`${uploadUrl}/init`, () => new HttpResponse(null, { status: 400 })),
			http.post(`${uploadUrl}/:uploadId/append`, append),
		)
		const dispatch = vi.fn()
		const navigate = vi.fn()

		await expect(
			sendBestilling(dokarkivValues('YQ=='), { is: {} }, 1, navigate)(dispatch),
		).rejects.toThrow('Kunne ikke starte dokumentopplasting (HTTP 400)')

		expect(append).not.toHaveBeenCalled()
		expect(DollyApi.createBestilling).not.toHaveBeenCalled()
		expect(dispatch).not.toHaveBeenCalled()
		expect(navigate).not.toHaveBeenCalled()
	})
})
