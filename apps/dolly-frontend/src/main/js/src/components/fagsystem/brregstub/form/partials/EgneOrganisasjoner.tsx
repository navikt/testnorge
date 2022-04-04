import React, { useEffect, useState } from 'react'
import Loading from '~/components/ui/loading/Loading'
import { AlertStripeAdvarsel } from 'nav-frontend-alertstriper'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { Adresse, Organisasjon } from '~/service/services/organisasjonforvalter/types'

interface OrgProps {
	path: string
	formikBag: FormikProps<{}>
	handleChange: (event: React.ChangeEvent<any>) => void
	warningMessage?: AlertStripeAdvarsel
	isLoading: boolean
	hentOrganisasjoner: Function
	organisasjoner: Organisasjon[]
	filterValidEnhetstyper?: boolean
}

export const getAdresseWithAdressetype = (adresser: Adresse[], adressetype: string) => {
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

export const EgneOrganisasjoner = ({
	path,
	formikBag,
	handleChange,
	warningMessage,
	isLoading,
	hentOrganisasjoner,
	organisasjoner,
	filterValidEnhetstyper,
}: OrgProps) => {
	const [error, setError] = useState(false)
	const harEgneOrganisasjoner = organisasjoner && organisasjoner.length > 0

	const validEnhetstyper = ['BEDR', 'AAFY']

	useEffect(() => {
		if (!organisasjoner) {
			hentOrganisasjoner().catch(() => {
				setError(true)
			})
		}
	}, [])

	return (
		<>
			{isLoading && <Loading label="Laster organisasjoner" />}
			{error && (
				<AlertStripeAdvarsel>
					Noe gikk galt med henting av egne organisasjoner! Prøv på nytt, velg et annet alternativ
					eller kontakt team Dolly ved vedvarende feil.
				</AlertStripeAdvarsel>
			)}
			{!harEgneOrganisasjoner &&
				!isLoading &&
				!error &&
				(warningMessage ? (
					warningMessage
				) : (
					<AlertStripeAdvarsel>
						Du har ingen egne organisasjoner. For å lage dine egne organisasjoner trykk{' '}
						<a href="/organisasjoner">her</a>.
					</AlertStripeAdvarsel>
				))}
			{harEgneOrganisasjoner && !error && (
				<DollySelect
					name={path}
					label="Organisasjonsnummer"
					options={
						filterValidEnhetstyper
							? organisasjoner.filter((virksomhet) =>
									validEnhetstyper.includes(virksomhet.enhetstype)
							  )
							: organisasjoner
					}
					size="xlarge"
					onChange={handleChange}
					value={_get(formikBag.values, path)}
					feil={
						_get(formikBag.values, path) === '' && {
							feilmelding: 'Feltet er påkrevd',
						}
					}
					isClearable={false}
				/>
			)}
		</>
	)
}
