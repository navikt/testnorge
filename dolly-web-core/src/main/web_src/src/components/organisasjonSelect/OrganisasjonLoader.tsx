import * as React from 'react'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { EregResponse } from '~/service/Responses'
import { Organisasjon } from './types'

type OrganisasjonLoaderProps = {
	filter?: (response: EregResponse) => boolean
	render: (list: Organisasjon[]) => JSX.Element
}

export const OrganisasjonLoader = ({ filter = () => true, render }: OrganisasjonLoaderProps) => {
	const formatLabel = (response: EregResponse) =>
		`${response.orgnr} (${response.enhetstype}) - ${response.navn}`
	const onFetch = () =>
		SelectOptionsOppslag.hentOrgnr().then(
			({ liste }): Array<EregResponse> =>
				liste.filter(filter).map((response: EregResponse) => ({
					value: response.orgnr,
					label: formatLabel(response),
					juridiskEnhet: response.juridiskEnhet,
					forretningsAdresse: response.forretningsAdresse,
					postadresse: response.postadresse
				}))
		)
	return <LoadableComponent onFetch={onFetch} render={(list: Organisasjon[]) => render(list)} />
}
