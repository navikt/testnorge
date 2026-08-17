import { beforeEach, describe, expect, it, vi } from 'vitest'
import { DollyApi, PdlforvalterApi } from '@/service/Api'
import { identerSearch } from '@/service/services/dollysearch/DollySearch'
import { PERSON_SEARCH_PAGE_SIZE, soekPersoner } from '@/pages/gruppeOversikt/FinnPerson'

vi.mock('@/service/Api', async (importOriginal) => {
	const actual = await importOriginal<typeof import('@/service/Api')>()
	return {
		...actual,
		DollyApi: {
			...actual.DollyApi,
			getAktoerFraPdl: vi.fn(),
		},
		PdlforvalterApi: {
			...actual.PdlforvalterApi,
			soekPersoner: vi.fn(),
		},
	}
})

vi.mock('@/service/services/dollysearch/DollySearch', () => ({
	identerSearch: vi.fn(),
}))

vi.mock('@/utils/hooks/useRedux', () => ({
	useReduxDispatch: vi.fn(),
	useReduxSelector: vi.fn(),
}))

vi.mock('@/ducks/finnPerson', () => ({
	navigerTilPerson: vi.fn(),
}))

const mockGetAktoerFraPdl = vi.mocked(DollyApi.getAktoerFraPdl)
const mockSoekPersoner = vi.mocked(PdlforvalterApi.soekPersoner)
const mockIdenterSearch = vi.mocked(identerSearch)

const personer = Array.from({ length: PERSON_SEARCH_PAGE_SIZE }, (_, index) => ({
	ident: `123456789${index}`,
	fornavn: 'Ola',
	etternavn: `Nordmann ${index}`,
}))

describe('soekPersoner', () => {
	beforeEach(() => {
		vi.clearAllMocks()
		mockSoekPersoner.mockResolvedValue({ data: personer })
		mockIdenterSearch.mockResolvedValue({ data: personer })
		mockGetAktoerFraPdl.mockResolvedValue({ data: {} })
	})

	it('paginates both list sources and only performs the direct lookup on the first page', async () => {
		const setError = vi.fn()
		const setPersonState = vi.fn()

		const firstPage = await soekPersoner('ola', setError, setPersonState, 0, 123)
		const secondPage = await soekPersoner('ola', setError, setPersonState, 1, 123, {
			pdlf: true,
			pdl: true,
			pdlAktoer: false,
		})

		expect(mockSoekPersoner).toHaveBeenNthCalledWith(1, 'ola', 0, PERSON_SEARCH_PAGE_SIZE)
		expect(mockSoekPersoner).toHaveBeenNthCalledWith(2, 'ola', 1, PERSON_SEARCH_PAGE_SIZE)
		expect(mockIdenterSearch).toHaveBeenNthCalledWith(1, 'ola', 0, PERSON_SEARCH_PAGE_SIZE, 123)
		expect(mockIdenterSearch).toHaveBeenNthCalledWith(2, 'ola', 1, PERSON_SEARCH_PAGE_SIZE, 123)
		expect(mockGetAktoerFraPdl).toHaveBeenCalledTimes(1)
		expect(firstPage.hasMorePdlf).toBe(true)
		expect(firstPage.hasMorePdl).toBe(true)
		expect(secondPage.hasMorePdlf).toBe(true)
		expect(secondPage.hasMorePdl).toBe(true)
		expect(setError).not.toHaveBeenCalled()
	})
})
