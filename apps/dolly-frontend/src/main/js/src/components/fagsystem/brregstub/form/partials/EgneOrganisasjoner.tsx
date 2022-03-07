import React, { useEffect, useState } from 'react'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import Loading from '~/components/ui/loading/Loading'
import { AlertStripeAdvarsel } from 'nav-frontend-alertstriper'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { Organisasjon } from '~/components/fagsystem/brregstub/form/partials/types'

interface OrgProps {
	path: string
	formikBag: FormikProps<{}>
	handleChange: (event: React.ChangeEvent<any>) => void
}

const formatLabel = (response: Organisasjon) =>
	`${response.organisasjonsnummer} (${response.enhetstype}) - ${response.organisasjonsnavn}`

export const EgneOrganisasjoner = ({ path, formikBag, handleChange }: OrgProps) => {
	const [egneOrganisasjoner, setEgneOrganisasjoenr] = useState([])
	const [loading, setLoading] = useState(false)
	const [error, setError] = useState(false)
	const harEgneOrganisasjoner = egneOrganisasjoner && egneOrganisasjoner.length > 0

	useEffect(() => {
		setLoading(true)
		setError(false)
		const orgData = async () => {
			const resp = await SelectOptionsOppslag.hentOrganisasjonerFraOrgforvalter()
				.then((response: Organisasjon[]) => {
					setError(false)
					return response.map((org: Organisasjon) => {
						const adresser = org.adresser.map((adr) => ({
							adresse: adr.adresselinjer,
							kommunenr: adr.kommunenr,
							landkode: adr.landkode,
							postnr: adr.postnr,
							poststed: adr.poststed,
						}))
						const adresse = adresser.length > 0 ? adresser[0] : null
						return {
							value: org.organisasjonsnummer,
							label: formatLabel(org),
							orgnr: org.organisasjonsnummer,
							navn: org.organisasjonsnavn,
							forretningsAdresse: adresse,
						}
					})
				})
				.catch((e: Error) => {
					setError(true)
					return []
				})
			setEgneOrganisasjoenr(resp)
			setLoading(false)
		}
		orgData()
	}, [])

	return (
		<>
			{loading && <Loading label="Laster organisasjoner" />}
			{error && (
				<AlertStripeAdvarsel>
					Noe gikk galt med henting av egne organisasjoner! Prøv på nytt, velg et annet alternativ
					eller kontakt team Dolly ved vedvarende feil.
				</AlertStripeAdvarsel>
			)}
			{!harEgneOrganisasjoner && !loading && !error && (
				<AlertStripeAdvarsel>
					Du har ingen egne organisasjoner. For å lage dine egne testorganisasjoner trykk{' '}
					<a href="/organisasjoner">her</a>.
				</AlertStripeAdvarsel>
			)}
			{harEgneOrganisasjoner && !error && (
				<DollySelect
					name={`${path}.orgNr`}
					label="Organisasjonsnummer"
					options={egneOrganisasjoner}
					size="xlarge"
					onChange={handleChange}
					value={_get(formikBag.values, `${path}.orgNr`)}
					feil={
						_get(formikBag.values, `${path}.orgNr`) === '' && {
							feilmelding: 'Feltet er påkrevd',
						}
					}
					isClearable={false}
				/>
			)}
		</>
	)
}
