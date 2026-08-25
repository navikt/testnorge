import { beforeEach, describe, expect, it, vi } from 'vitest'
import Api from '@/api'
import Request from '@/service/services/Request'
import PdlForvalterService from '@/service/services/pdl/PdlForvalterService'
import { identerSearch } from '@/service/services/dollysearch/DollySearch'

vi.mock('@/api', () => ({
	default: {
		fetch: vi.fn(),
	},
}))

vi.mock('@/service/services/Request', () => ({
	default: {
		get: vi.fn(),
	},
}))

const mockFetch = vi.mocked(Api.fetch)
const mockGet = vi.mocked(Request.get)

describe('navigation search services', () => {
	beforeEach(() => {
		vi.clearAllMocks()
		mockGet.mockResolvedValue({ data: [] })
		mockFetch.mockResolvedValue(
			new Response(JSON.stringify([]), {
				status: 200,
				headers: { 'Content-Type': 'application/json' },
			}),
		)
	})

	it('adds pagination to the PDL forvalter search', async () => {
		await PdlForvalterService.soekPersoner('Ola Nordmann', 2, 10)

		expect(mockGet).toHaveBeenCalledWith(
			'/testnav-pdl-forvalter/api/v1/identiteter?fragment=Ola%20Nordmann&sidenummer=2&sidestorrelse=10',
		)
	})

	it('adds pagination and seed to the Dolly ident search', async () => {
		await identerSearch('Ola Nordmann', 3, 10, 123)

		expect(mockFetch).toHaveBeenCalledWith(
			'/testnav-dolly-search-service/api/v1/identer?fragment=Ola%20Nordmann&side=3&antall=10&seed=123',
			{
				method: 'GET',
				headers: { 'Content-Type': 'application/json' },
			},
		)
	})
})
