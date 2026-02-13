import { describe, expect, it, vi } from 'vitest'
import { soekBestillinger } from '@/pages/gruppeOversikt/FinnBestilling'

vi.mock('@/service/Api', () => ({
	DollyApi: {
		getBestillingerFragment: vi.fn(),
	},
}))
vi.mock('@/ducks/finnPerson', () => ({
	navigerTilBestilling: vi.fn(),
}))
vi.mock('@/utils/hooks/useRedux', () => ({
	useReduxDispatch: vi.fn(() => vi.fn()),
}))

import { DollyApi } from '@/service/Api'

describe('soekBestillinger', () => {
	it('should return empty array when tekst is empty', async () => {
		const result = await soekBestillinger('')
		expect(result).toEqual([])
	})

	it('should return empty array when tekst is falsy', async () => {
		const result = await soekBestillinger(null as unknown as string)
		expect(result).toEqual([])
	})

	it('should return formatted options from API response', async () => {
		vi.mocked(DollyApi.getBestillingerFragment).mockResolvedValue({
			data: [
				{ id: 5, navn: 'Testgruppe' },
				{ id: 12, navn: 'Min bestilling' },
			],
		})

		const result = await soekBestillinger('test')

		expect(DollyApi.getBestillingerFragment).toHaveBeenCalledWith('test')
		expect(result).toEqual([
			{ value: 5, label: '#5 - Testgruppe' },
			{ value: 12, label: '#12 - Min bestilling' },
		])
	})

	it('should strip # from search text before calling API', async () => {
		vi.mocked(DollyApi.getBestillingerFragment).mockResolvedValue({
			data: [{ id: 1, navn: 'Test' }],
		})

		await soekBestillinger('#123')

		expect(DollyApi.getBestillingerFragment).toHaveBeenCalledWith('123')
	})

	it('should return empty array when API returns no data', async () => {
		vi.mocked(DollyApi.getBestillingerFragment).mockResolvedValue({
			data: null,
		})

		const result = await soekBestillinger('test')
		expect(result).toEqual([])
	})

	it('should return empty array when API returns empty array', async () => {
		vi.mocked(DollyApi.getBestillingerFragment).mockResolvedValue({
			data: [],
		})

		const result = await soekBestillinger('test')
		expect(result).toEqual([])
	})
})
