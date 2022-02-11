import Api from '~/api'
import { Person, Search } from './types'

export type SearchResponse = { numerOfItems: number; items: Person[] }

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

export default {
	search,
}
