import { beforeEach, describe, expect, it, vi } from 'vitest'
import axios from 'axios'
import { backgroundFetcher, fetcher, postFetcher } from '@/api'
import { handleSessionExpired } from '@/components/utlogging/sessionExpired'

vi.mock('@/components/utlogging/sessionExpired', async (importOriginal) => ({
	...(await importOriginal<typeof import('@/components/utlogging/sessionExpired')>()),
	handleSessionExpired: vi.fn(),
}))

const sessionExpiredError = {
	response: { status: 401, headers: { 'x-dolly-session': 'expired' } },
}

const settlesWithin = (promise: Promise<unknown>, ms: number) =>
	Promise.race([
		promise.then(
			() => true,
			() => true,
		),
		new Promise((resolve) => setTimeout(() => resolve(false), ms)),
	])

describe('API ved utløpt sesjon', () => {
	beforeEach(() => {
		vi.restoreAllMocks()
		vi.mocked(handleSessionExpired).mockClear()
	})

	it('fetcher venter på redirect i stedet for å kaste feil', async () => {
		vi.spyOn(axios, 'get').mockRejectedValue(sessionExpiredError)

		const settled = await settlesWithin(fetcher('/dolly-backend/api/v1/bruker/current'), 50)

		expect(settled).toBe(false)
		expect(handleSessionExpired).toHaveBeenCalledTimes(1)
	})

	it('postFetcher venter på redirect i stedet for å kaste feil', async () => {
		vi.spyOn(axios, 'post').mockRejectedValue(sessionExpiredError)

		const settled = await settlesWithin(postFetcher('/testnav-kelvin/api', {}), 50)

		expect(settled).toBe(false)
		expect(handleSessionExpired).toHaveBeenCalledTimes(1)
	})

	it('backgroundFetcher kaster feil uten å omdirigere', async () => {
		vi.spyOn(axios, 'get').mockRejectedValue(sessionExpiredError)

		await expect(backgroundFetcher('/dolly-backend/api/v1/bestilling/1')).rejects.toThrow(
			'Sesjonen er utløpt',
		)
		expect(handleSessionExpired).not.toHaveBeenCalled()
	})

	it('backgroundFetcher sender bakgrunnsheader', async () => {
		const get = vi.spyOn(axios, 'get').mockResolvedValue({ data: { ferdig: false } })

		await expect(backgroundFetcher('/dolly-backend/api/v1/bestilling/1')).resolves.toEqual({
			ferdig: false,
		})
		expect(get).toHaveBeenCalledWith('/dolly-backend/api/v1/bestilling/1', {
			headers: { 'X-Dolly-Background': 'true' },
			timeout: 15000,
		})
	})
})
