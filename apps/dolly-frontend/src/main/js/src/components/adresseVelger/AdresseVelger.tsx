import React, { useState } from 'react'
import AdresseSok from './AdresseSok'
import styled from 'styled-components'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { Alert } from '@navikt/ds-react'
import { useAdresser } from '@/utils/hooks/useAdresseSoek'

const StyledAdresseVelger = styled.div`
	background-color: #edf2ff;
	padding: 10px 20px;
	margin-bottom: 20px;
`
type Search = {
	fritekst?: string
	postnummer?: string
	kommunenummer?: string
}

export type Adresse = {
	matrikkelId: string
	adressekode: string
	adressenavn: string
	husnummer: number
	husbokstav?: string
	postnummer: string
	poststed: string
	kommunenummer: string
	kommunenavn: string
	bydelsnummer?: string
	bydelsnavn?: string
	tilleggsnavn?: string
	fylkesnummer: string
	fylkesnavn: string
	bruksenhetsnummer?: string
}

const Feil = styled(Alert)`
	margin-top: 15px;
`
const Advarsel = styled(Alert)`
	margin-top: 15px;
`

type Props = {
	onSelect: (adresse: Adresse) => void
}

export default ({ onSelect }: Props) => {
	const [adresse, setAdresse] = useState<Adresse>()
	const [search, setSearch] = useState<Search>(null)

	const { adresser, loading, notFound, error } = useAdresser(search)

	const findAdresse = (matrikkelId: string) =>
		adresser.find((value) => value.matrikkelId === matrikkelId)

	const getLabel = ({ adressenavn, husnummer, husbokstav, postnummer, poststed }: Adresse) =>
		`${adressenavn} ${husnummer}${husbokstav ? husbokstav : ''}, ${postnummer} ${poststed}`

	const onSubmit = (search: Search) => {
		setSearch(search)
	}

	return (
		<StyledAdresseVelger>
			<AdresseSok onSubmit={onSubmit} loading={loading} />
			{error && !notFound && (
				<Feil variant={'error'} size={'small'}>
					Noe gikk galt! Prøv på nytt eller kontakt Team Dolly.
				</Feil>
			)}
			{notFound && (
				<Advarsel variant={'warning'} size={'small'}>
					Fant ikke et resultat. Prøv å endre kombinasjon av felter i søket.
				</Advarsel>
			)}
			{adresser && (
				<>
					<h4>Velg adresse</h4>
					<DollySelect
						name="adresse"
						label="Adresse"
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
