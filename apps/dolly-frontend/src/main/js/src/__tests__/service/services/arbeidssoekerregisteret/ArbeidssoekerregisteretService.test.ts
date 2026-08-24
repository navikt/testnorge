import { beforeEach, describe, expect, it, vi } from 'vitest'
import Request from '@/service/services/Request'
import ArbeidssoekerregisteretService from '@/service/services/arbeidssoekerregisteret/ArbeidssoekerregisteretService'

vi.mock('@/service/services/Request', () => ({
	default: {
		delete: vi.fn(),
	},
}))

const mockDelete = vi.mocked(Request.delete)

describe('ArbeidssoekerregisteretService', () => {
	beforeEach(() => {
		vi.clearAllMocks()
		mockDelete.mockResolvedValue(new Response())
	})

	it('stops registration directly through the arbeidssoekerregisteret proxy', async () => {
		await ArbeidssoekerregisteretService.stoppRegistrering('12345678901')

		expect(mockDelete).toHaveBeenCalledWith(
			'/testnav-arbeidssoekerregisteret-proxy/api/v1/arbeidssoekerregistrering/12345678901',
		)
	})
})
