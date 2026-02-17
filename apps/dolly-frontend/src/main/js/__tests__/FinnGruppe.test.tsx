import { describe, expect, it, vi } from 'vitest'
import { soekGrupper } from '@/pages/gruppeOversikt/FinnGruppe'

vi.mock('@/service/Api', () => ({
	DollyApi: {
		getGrupperFragment: vi.fn(),
	},
}))
vi.mock('@/ducks/finnPerson', () => ({
	setGruppeNavigerTil: vi.fn(),
}))
vi.mock('@/utils/hooks/useRedux', () => ({
	useReduxDispatch: vi.fn(() => vi.fn()),
	useReduxSelector: vi.fn(),
}))
vi.mock('react-router', () => ({
	useNavigate: vi.fn(() => vi.fn()),
}))

import { DollyApi } from '@/service/Api'

describe('soekGrupper', () => {
	it('should return empty array when tekst is empty', async () => {
		const result = await soekGrupper('')
		expect(result).toEqual([])
	})

	it('should return empty array when tekst is falsy', async () => {
		const result = await soekGrupper(null as unknown as string)
		expect(result).toEqual([])
	})

	it('should return formatted options from API response', async () => {
		vi.mocked(DollyApi.getGrupperFragment).mockResolvedValue({
			data: [
				{ id: 1, navn: 'Testgruppe Alpha' },
				{ id: 42, navn: 'Min gruppe' },
			],
		})

		const result = await soekGrupper('test')

		expect(DollyApi.getGrupperFragment).toHaveBeenCalledWith('test')
		expect(result).toEqual([
			{ value: 1, label: 'Gruppe 1 - Testgruppe Alpha' },
			{ value: 42, label: 'Gruppe 42 - Min gruppe' },
		])
	})

	it('should return empty array when API returns no data', async () => {
		vi.mocked(DollyApi.getGrupperFragment).mockResolvedValue({
			data: null,
		})

		const result = await soekGrupper('test')
		expect(result).toEqual([])
	})

	it('should return empty array when API returns empty array', async () => {
		vi.mocked(DollyApi.getGrupperFragment).mockResolvedValue({
			data: [],
		})

		const result = await soekGrupper('test')
		expect(result).toEqual([])
	})
})
