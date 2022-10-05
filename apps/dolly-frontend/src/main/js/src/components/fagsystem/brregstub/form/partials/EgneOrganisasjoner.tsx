import React, { useEffect, useState } from 'react'
import Loading from '~/components/ui/loading/Loading'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { Adresse, Organisasjon } from '~/service/services/organisasjonforvalter/types'
import { Alert } from '@navikt/ds-react'
import { useCurrentBruker } from '~/utils/hooks/useBruker'

interface OrgProps {
	path: string
	label?: string
	formikBag: FormikProps<{}>
	handleChange: (event: React.ChangeEvent<any>) => void
	warningMessage?: string
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
	label,
	formikBag,
	handleChange,
	warningMessage,
	isLoading,
	hentOrganisasjoner,
	organisasjoner,
	filterValidEnhetstyper,
}: OrgProps) => {
	const [error, setError] = useState(false)
	const {
		currentBruker: { brukerId },
	} = useCurrentBruker()

	const harEgneOrganisasjoner = organisasjoner && organisasjoner.length > 0
	const validEnhetstyper = ['BEDR', 'AAFY']

	useEffect(() => {
		if (!organisasjoner) {
			hentOrganisasjoner(brukerId).catch(() => {
				setError(true)
			})
		}
	}, [])

	return (
		<>
			{isLoading && <Loading label="Laster organisasjoner" />}
			{error && (
				<Alert variant={'warning'}>
					Noe gikk galt med henting av egne organisasjoner! Prøv på nytt, velg et annet alternativ
					eller kontakt Team Dolly ved vedvarende feil.
				</Alert>
			)}
			{!harEgneOrganisasjoner &&
				!isLoading &&
				!error &&
				(warningMessage ? (
					warningMessage
				) : (
					<Alert variant={'warning'}>
						Du har ingen egne organisasjoner. For å lage dine egne organisasjoner trykk{' '}
						<a href="/organisasjoner">her</a>.
					</Alert>
				))}
			{harEgneOrganisasjoner && !error && (
				<DollySelect
					name={path}
					label={label ? label : 'Organisasjonsnummer'}
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
