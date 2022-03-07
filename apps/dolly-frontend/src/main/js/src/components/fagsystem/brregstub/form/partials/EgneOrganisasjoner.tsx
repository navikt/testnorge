import React, { useEffect, useState } from 'react'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import Loading from '~/components/ui/loading/Loading'
import { AlertStripeAdvarsel, AlertStripeFeil } from 'nav-frontend-alertstriper'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import styled from 'styled-components'

interface OrgProps {
	path: string
	formikBag: FormikProps<{}>
	handleChange: (event: React.ChangeEvent<any>) => void
}

type OrgEnhet = {
	orgnummer: string
	orgnavn: string
	enhetstype: string
}

const formatLabel = (response: OrgEnhet) =>
	`${response.orgnummer} (${response.enhetstype}) - ${response.orgnavn}`

export const EgneOrganisasjoner = ({ path, formikBag, handleChange }: OrgProps) => {
	const [egneOrganisasjoner, setEgneOrganisasjoenr] = useState([])
	const [loading, setLoading] = useState(false)
	const [error, setError] = useState(false)
	const harEgneOrganisasjoner = egneOrganisasjoner && egneOrganisasjoner.length > 0

	useEffect(() => {
		setLoading(true)
		setError(false)
		const orgData = async () => {
			const resp = await SelectOptionsOppslag.hentVirksomheterFraOrgforvalter()
				.then((response: OrgEnhet[]) => {
					setError(false)
					return response.map((enhet: OrgEnhet) => ({
						value: enhet.orgnummer,
						label: formatLabel(enhet),
						orgnr: enhet.orgnummer,
						navn: enhet.orgnavn,
					}))
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
					Noe gikk galt med henting av egne organisasjoner! Prøv på nytt, velg annet alternativ
					eller kontakt team Dolly.
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
