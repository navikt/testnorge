import React, { useState } from 'react'
import AdresseSok from './AdresseSok'
import { AlertStripeFeil, AlertStripeAdvarsel } from 'nav-frontend-alertstriper'
import styled from 'styled-components'
import AdresseService, { Adresse } from '~/service/services/AdresseService'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { NotFoundError } from '~/error'

const StyledAdresseVelger = styled.div`
	background-color: #edf2ff;
	padding: 10px 20px;
	margin-bottom: 20px;
`
type Search = {
	adressenavn?: string
	postnummer?: string
	kommunenummer?: string
}

const Feil = styled(AlertStripeFeil)`
	margin-top: 15px;
`
const Advarsel = styled(AlertStripeAdvarsel)`
	margin-top: 15px;
`

type Props = {
	onSelect: (adresse: Adresse) => void
}

export default ({ onSelect }: Props) => {
	const [adresser, setAdresser] = useState<Adresse[]>()
	const [adresse, setAdresse] = useState<Adresse>()
	const [loading, setLoading] = useState<boolean>(false)
	const [notFound, setNotFound] = useState<boolean>(false)
	const [error, setError] = useState<boolean>(false)

	const findAdresse = (matrikkelId: string) =>
		adresser.find(value => value.matrikkelId === matrikkelId)

	const getLabel = ({ adressenavn, husnummer, husbokstav, postnummer, poststed }: Adresse) =>
		`${adressenavn} ${husnummer}${husbokstav ? husbokstav : ''}, ${postnummer} ${poststed}`

	const onSubmit = (search: Search) => {
		setLoading(true)
		setNotFound(false)
		setError(false)
		setAdresser(null)
		return AdresseService.hentAdresser(search, 10)
			.then(adresser => {
				setAdresser(adresser)
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
			<AdresseSok onSubmit={onSubmit} loading={loading} />
			{error && <Feil>Noe gikk galt! Prøv på nytt eller kontakt team Dolly.</Feil>}
			{notFound && (
				<Advarsel>Fant ikke et resultat. Prøv å endre kombinasjon av felter i søket.</Advarsel>
			)}
			{adresser && (
				<>
					<h4>Velg adresse</h4>
					<DollySelect
						name="adresse"
						label="Adresse"
						size="grow"
						isClearable={false}
						options={adresser.map(value => ({
							value: value.matrikkelId,
							label: getLabel(value)
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
