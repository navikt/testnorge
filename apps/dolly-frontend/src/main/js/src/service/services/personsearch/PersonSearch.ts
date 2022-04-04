import Api from '~/api'
import { Person, Search } from './types'
import { PdlData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

export type SearchResponse = { numerOfItems: number; items: Person[] }
export type SearchResponsePdl = { numerOfItems: number; items: PdlData[] }

const search = (searchRequest: Search): Promise<SearchResponse> =>
	Api.fetch(
		'/person-search-service/api/v1/person',
		{ method: 'POST', headers: { 'Content-Type': 'application/json' } },
		searchRequest
	).then((response) =>
		(response.json() as Promise<Person[]>).then((items) => ({
			items: items,
			numerOfItems: parseInt(response.headers.get('number_of_items')),
		}))
	)

const searchPdlPerson = (searchRequest: Search): Promise<SearchResponsePdl> =>
	Api.fetch(
		'/person-search-service/api/v1/pdlPerson',
		{ method: 'POST', headers: { 'Content-Type': 'application/json' } },
		searchRequest
	).then((response) =>
		(response.json() as Promise<string[]>).then((items) => {
			const pdlResponse = items.map((item) => JSON.parse(item))
			return {
				items: pdlResponse,
				numerOfItems: parseInt(response.headers.get('number_of_items')),
			}
		})
	)

export default {
	search,
	searchPdlPerson,
}
