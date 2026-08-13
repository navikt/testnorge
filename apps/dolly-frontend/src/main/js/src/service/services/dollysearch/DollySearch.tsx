import Api from '@/api'

type PdlPerson = {
	ident: string
	navn: { fornavn: string; mellomnavn: string; etternavn: string }
}

const identSearchUrl = `/testnav-dolly-search-service/api/v1`

export const identerSearch = (fragment: string, side: number, antall: number, seed: number) => {
	if (!fragment) {
		return null
	}
	return Api.fetch(
		`${identSearchUrl}/identer?fragment=${encodeURIComponent(
			fragment,
		)}&side=${side}&antall=${antall}&seed=${seed}`,
		{
			method: 'GET',
			headers: { 'Content-Type': 'application/json' },
		},
	).then((response) =>
		response.ok
			? response.json().then((items: PdlPerson[]) => ({
					data: items?.map((person) => ({
						ident: person.ident,
						...person.navn,
					})),
				}))
			: null,
	)
}
