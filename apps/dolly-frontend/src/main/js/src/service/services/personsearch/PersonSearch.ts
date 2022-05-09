import Api from '~/api'
import { Search } from './types'
import { PdlData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

export type SearchResponsePdl = { numberOfItems: number; items: PdlData[] }

const search = (searchRequest: Search): Promise<SearchResponsePdl> =>
	Api.fetch(
		'/person-search-service/api/v1/pdlPerson',
		{ method: 'POST', headers: { 'Content-Type': 'application/json' } },
		searchRequest
	).then((response) =>
		(response.json() as Promise<string[]>).then((items) => {
			const pdlResponse = items.map((item) => JSON.parse(item))
			return {
				items: pdlResponse,
				numberOfItems: parseInt(response.headers.get('number_of_items')),
			}
		})
	)

const searchPdlPerson = async (searchRequest: Search): Promise<SearchResponsePdl> => {
	if (searchRequest?.relasjoner?.barn === 'M') {
		searchRequest.terminateAfter = null
		const initialResponse = await search(searchRequest)
		const numberOfItems = initialResponse.numberOfItems
		const numberOfPages = Math.ceil(numberOfItems / searchRequest.pageSize)

		let items = filterOutPersonerMedEtBarn(initialResponse.items)
		let currentPage = 2
		while (items.length < searchRequest.pageSize && currentPage <= numberOfPages) {
			searchRequest.page = currentPage
			const latestResponse = await search(searchRequest)
			items = items.concat(items, filterOutPersonerMedEtBarn(latestResponse.items))
			currentPage++
		}
		if (items.length > searchRequest.pageSize) {
			items = items.slice(0, searchRequest.pageSize)
		}
		return {
			items: items,
			numberOfItems: items.length,
		}
	} else {
		return search(searchRequest)
	}
}

const filterOutPersonerMedEtBarn = (items: PdlData[]) => {
	return items?.filter(
		(pdldata) =>
			pdldata?.hentPerson?.forelderBarnRelasjon?.filter(
				(relasjon) => relasjon.relatertPersonsRolle === 'BARN'
			).length > 1
	)
}

export default {
	searchPdlPerson,
}
