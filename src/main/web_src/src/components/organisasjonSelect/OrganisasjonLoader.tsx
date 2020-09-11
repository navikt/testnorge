import * as React from 'react'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { EregResponse } from '~/service/Responses'
import { Organisasjon } from './types'

type OrganisasjonLoaderProps = {
	filter?: (response: EregResponse) => boolean
	valueNavn?: boolean
	render: (list: Organisasjon[]) => JSX.Element
}

export const OrganisasjonLoader = ({
	filter = () => true,
	render,
	valueNavn = false
}: OrganisasjonLoaderProps) => {
	const formatLabel = (response: EregResponse) =>
		`${response.orgnr} (${response.enhetstype}) - ${response.navn}`
	const onFetch = () =>
		SelectOptionsOppslag.hentOrgnr().then(
			({ liste }): Array<EregResponse> =>
				liste
					.sort(function(a: EregResponse, b: EregResponse) {
						if (a.opprinnelse < b.opprinnelse) return 1
						if (a.opprinnelse > b.opprinnelse) return -1
						return 0
					})
					.filter(filter)
					.map((response: EregResponse) => ({
						value: valueNavn ? response.navn : response.orgnr,
						label: formatLabel(response),
						orgnr: response.orgnr,
						juridiskEnhet: response.juridiskEnhet,
						navn: response.navn,
						forretningsAdresse: response.forretningsAdresse,
						postadresse: response.postadresse
					}))
		)
	return <LoadableComponent onFetch={onFetch} render={(list: Organisasjon[]) => render(list)} />
}
