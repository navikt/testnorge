import React, { useState } from 'react'
import MatrikkelAdresseSok from './MatrikkelAdresseSok'
import styled from 'styled-components'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { InfoCard } from '@navikt/ds-react'
import { ExclamationmarkTriangleFillIcon, XMarkOctagonFillIcon } from '@navikt/aksel-icons'
import { useMatrikkelAdresser } from '@/utils/hooks/useAdresseSoek'

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

const Feil = styled(InfoCard)`
	margin-top: 15px;
`

const Advarsel = styled(InfoCard)`
	margin-top: 15px;
`

type Props = {
	onSelect: (adresse: MatrikkelAdresse) => void
}

export type MatrikkelAdresse = {
	matrikkelId: string
	kommunenummer: string
	gaardsnummer: string
	bruksnummer: string
	postnummer: string
	poststed: string
	bruksenhetsnummer?: string
	tilleggsnavn: string
}

export default ({ onSelect }: Props) => {
	const [adresse, setAdresse] = useState<MatrikkelAdresse>()
	const [search, setSearch] = useState<Search>(null)

	const { adresser, loading, notFound, error } = useMatrikkelAdresser(search)

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
		setSearch(search)
	}

	return (
		<StyledAdresseVelger>
			<MatrikkelAdresseSok onSubmit={onSubmit} loading={loading} />
			{error && !notFound && (
				<Feil data-color="danger" size={'small'}>
					<InfoCard.Message icon={<XMarkOctagonFillIcon aria-hidden />}>
						Noe gikk galt! Prøv på nytt eller kontakt Team Dolly.
					</InfoCard.Message>
				</Feil>
			)}
			{notFound && (
				<Advarsel data-color="warning" size={'small'}>
					<InfoCard.Message icon={<ExclamationmarkTriangleFillIcon aria-hidden />}>
						Fant ikke et resultat. Prøv å endre kombinasjon av felter i søket.
					</InfoCard.Message>
				</Advarsel>
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
