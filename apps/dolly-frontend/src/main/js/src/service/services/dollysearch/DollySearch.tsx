import Api from '@/api'

type PdlPerson = {
	ident: string
	navn: { fornavn: string; mellomnavn: string; etternavn: string }
}

const identSearchUrl = `/testnav-dolly-search-service/api/v1`

export const identerSearch = (fragment: string) => {
	if (!fragment) {
		return null
	}
	return Api.fetch(`${identSearchUrl}/identer?fragment=${fragment}`, {
		method: 'GET',
		headers: { 'Content-Type': 'application/json' },
	}).then((response) =>
		response.ok
			? response.json().then((items: [PdlPerson]) => ({
					data: items?.map((person) => ({
						ident: person.ident,
						...person.navn,
					})),
				}))
			: null,
	)
}
