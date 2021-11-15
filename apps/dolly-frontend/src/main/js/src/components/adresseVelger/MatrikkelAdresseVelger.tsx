import React, { useState } from 'react'
import MatrikkelAdresseSok from './MatrikkelAdresseSok'
import { AlertStripeAdvarsel, AlertStripeFeil } from 'nav-frontend-alertstriper'
import styled from 'styled-components'
import AdresseService, { MatrikkelAdresse } from '~/service/services/AdresseService'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { NotFoundError } from '~/error'

const StyledAdresseVelger = styled.div`
	background-color: #edf2ff;
	padding: 10px 20px;
	margin-bottom: 20px;
`
type Search = {
	kommunenummer?: string
	gaardsnummer?: string
	bruksnummer?: string
}

const Feil = styled(AlertStripeFeil)`
	margin-top: 15px;
`
const Advarsel = styled(AlertStripeAdvarsel)`
	margin-top: 15px;
`

type Props = {
	onSelect: (adresse: MatrikkelAdresse) => void
}

export default ({ onSelect }: Props) => {
	const [adresser, setAdresser] = useState<MatrikkelAdresse[]>()
	const [adresse, setAdresse] = useState<MatrikkelAdresse>()
	const [loading, setLoading] = useState<boolean>(false)
	const [notFound, setNotFound] = useState<boolean>(false)
	const [error, setError] = useState<boolean>(false)

	const findAdresse = (matrikkelId: string) =>
		adresser.find((value) => value.matrikkelId === matrikkelId)

	const getLabel = ({
		kommunenummer,
		gaardsnummer,
		bruksnummer,
		postnummer,
		poststed,
		tilleggsnavn,
	}: MatrikkelAdresse) =>
		`Gårdsnr: ${gaardsnummer}, Bruksnr: ${bruksnummer}, Kommunenr: ${kommunenummer}, ${
			tilleggsnavn ? tilleggsnavn + ',' : ''
		} ${postnummer} ${poststed}`

	const onSubmit = (search: Search) => {
		setLoading(true)
		setNotFound(false)
		setError(false)
		setAdresser(null)
		return AdresseService.hentMatrikkelAdresser(search, 10)
			.then((response) => {
				setAdresser(response)
				setLoading(false)
			})
			.catch((e: Error) => {
				setLoading(false)
				if (e && (e instanceof NotFoundError || e.name == 'NotFoundError')) {
					setNotFound(true)
				} else {
					setError(true)
				}
			})
	}

	return (
		<StyledAdresseVelger>
			<MatrikkelAdresseSok onSubmit={onSubmit} loading={loading} />
			{error && <Feil>Noe gikk galt! Prøv på nytt eller kontakt team Dolly.</Feil>}
			{notFound && (
				<Advarsel>Fant ikke et resultat. Prøv å endre kombinasjon av felter i søket.</Advarsel>
			)}
			{adresser && (
				<>
					<h4>Velg matrikkeladresse</h4>
					<DollySelect
						name="adresse"
						label="Matrikkeladresse"
						size="grow"
						isClearable={false}
						options={adresser.map((value) => ({
							value: value.matrikkelId,
							label: getLabel(value),
						}))}
						value={adresse ? adresse.matrikkelId : null}
						onChange={(e: any) => {
							const value = findAdresse(e.value)
							setAdresse(value)
							onSelect(value)
						}}
					/>
				</>
			)}
		</StyledAdresseVelger>
	)
}
