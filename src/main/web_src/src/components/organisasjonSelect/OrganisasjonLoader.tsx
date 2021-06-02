import * as React from 'react'
import LoadableComponent, { Feilmelding } from '~/components/ui/loading/LoadableComponent'
import OrganisasjonFasteDataService, {
	Organisasjon as FasteDataOrganisasjon
} from '@/service/services/organisasjonFasteDataService/OrganisasjonFasteDataService'
import { Organisasjon } from './types'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import LoadableComponentWithRetry from '~/components/ui/loading/LoadableComponentWithRetry'

type OrganisasjonLoaderProps = {
	kanHaArbeidsforhold?: boolean
	valueNavn?: boolean
	render: (list: Organisasjon[]) => JSX.Element
}

export const OrganisasjonLoader = ({
	kanHaArbeidsforhold,
	render,
	valueNavn = false
}: OrganisasjonLoaderProps) => {
	const formatLabel = (response: FasteDataOrganisasjon) =>
		`${response.orgnummer} (${response.enhetstype}) - ${response.navn}`
	const onFetch = () =>
		OrganisasjonFasteDataService.fetchOrganisasjoner('DOLLY', kanHaArbeidsforhold).then(
			(liste: FasteDataOrganisasjon[]) =>
				liste
					.sort(function(a: FasteDataOrganisasjon, b: FasteDataOrganisasjon) {
						if (a.opprinnelse < b.opprinnelse) return 1
						if (a.opprinnelse > b.opprinnelse) return -1
						return 0
					})
					.map((response: FasteDataOrganisasjon) => ({
						value: valueNavn ? response.navn : response.orgnummer,
						label: formatLabel(response),
						orgnr: response.orgnummer,
						juridiskEnhet: response.overenhet,
						navn: response.navn,
						forretningsAdresse: response.forretningsAdresse,
						postadresse: response.postadresse
					}))
		)
	return (
		<LoadableComponentWithRetry onFetch={onFetch} render={(list: Organisasjon[]) => render(list)} />
	)
}
