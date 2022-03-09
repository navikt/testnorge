import React, { useEffect, useState } from 'react'
import Loading from '~/components/ui/loading/Loading'
import { AlertStripeAdvarsel } from 'nav-frontend-alertstriper'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { Organisasjon, Adresse } from '~/service/services/organisasjonforvalter/types'
import { OrgforvalterApi } from '~/service/Api'

interface OrgProps {
	path: string
	formikBag: FormikProps<{}>
	handleChange: (event: React.ChangeEvent<any>) => void
}

const formatLabel = (response: Organisasjon) =>
	`${response.organisasjonsnummer} (${response.enhetstype}) - ${response.organisasjonsnavn}`

const getAdresseWithAdressetype = (adresser: Adresse[], adressetype: string) => {
	return adresser
		.filter((adr) => adr.adressetype === adressetype)
		.map((adr) => ({
			adresselinje1: adr.adresselinjer.length > 0 ? adr.adresselinjer[0] : '',
			kommunenr: adr.kommunenr,
			landkode: adr.landkode,
			postnr: adr.postnr,
			poststed: adr.poststed,
		}))
}

export const EgneOrganisasjoner = ({ path, formikBag, handleChange }: OrgProps) => {
	const [egneOrganisasjoner, setEgneOrganisasjoner] = useState([])
	const [loading, setLoading] = useState(false)
	const [error, setError] = useState(false)
	const harEgneOrganisasjoner = egneOrganisasjoner && egneOrganisasjoner.length > 0

	useEffect(() => {
		setLoading(true)
		setError(false)
		const orgData = async () => {
			const resp = await OrgforvalterApi.getAlleOrganisasjonerPaaBruker()
				.then((response) => {
					setError(false)
					if (response.length === 0) return []
					return response.map((org) => {
						const fAdresser = getAdresseWithAdressetype(org.adresser, 'FADR')
						const pAdresser = getAdresseWithAdressetype(org.adresser, 'PADR')

						return {
							value: org.organisasjonsnummer,
							label: formatLabel(org),
							orgnr: org.organisasjonsnummer,
							navn: org.organisasjonsnavn,
							forretningsAdresse: fAdresser.length > 0 ? fAdresser[0] : null,
							postAdresse: pAdresser.length > 0 ? pAdresser[0] : null,
						}
					})
				})
				.catch((e: Error) => {
					setError(true)
					return []
				})
			setEgneOrganisasjoner(resp)
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
