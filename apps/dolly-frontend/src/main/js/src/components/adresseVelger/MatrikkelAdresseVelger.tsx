import React, { useState } from 'react'
import MatrikkelAdresseSok from './MatrikkelAdresseSok'
import styled from 'styled-components'
import AdresseService, { MatrikkelAdresse } from '@/service/services/AdresseService'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { NotFoundError } from '@/error'
import { Alert } from '@navikt/ds-react'
import { instanceOf } from 'prop-types'
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

const Feil = styled(Alert)`
	margin-top: 15px;
`
const Advarsel = styled(Alert)`
	margin-top: 15px;
`

type Props = {
	onSelect: (adresse: MatrikkelAdresse) => void
}

// const hentMatrikkelAdresser = (search) => {
// 	const { adresserTest, loadingTest, errorTest } = useMatrikkelAdresser(search)
// 	console.log('adresser: ', adresserTest) //TODO - SLETT MEG
// }

export default ({ onSelect }: Props) => {
	// const [adresser, setAdresser] = useState<MatrikkelAdresse[]>()
	const [adresse, setAdresse] = useState<MatrikkelAdresse>()
	// const [loading, setLoading] = useState<boolean>(false)
	// const [notFound, setNotFound] = useState<boolean>(false)
	// const [error, setError] = useState<boolean>(false)
	const [search, setSearch] = useState<Search>(null)
	// const [search, setSearch] = useState<Search>({})

	const { adresser, loading, notFound, error } = useMatrikkelAdresser(search)
	console.log('adresser: ', adresser) //TODO - SLETT MEG

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

	// const { adresserTest, loadingTest, errorTest } = useMatrikkelAdresser({
	// 	kommunenummer: '4601',
	// 	gaardsnummer: '',
	// 	bruksnummer: '',
	// })
	// console.log('adresser: ', adresserTest) //TODO - SLETT MEG
	// console.log('errorTest: ', errorTest) //TODO - SLETT MEG
	const onSubmit = (search: Search) => {
		// setLoading(true)
		// setNotFound(false)
		// setError(false)
		// setAdresser(null)
		setSearch(search)
		// hentMatrikkelAdresser(search)
		// const { adresserTest, loadingTest, errorTest } = useMatrikkelAdresser(search)
		// console.log('adresser: ', adresserTest) //TODO - SLETT MEG

		// return AdresseService.hentMatrikkelAdresser(search, 10)
		// 	.then((response) => {
		// 		console.log('response: ', response) //TODO - SLETT MEG
		// 		setAdresser(response)
		// 		setLoading(false)
		// 	})
		// 	.catch((e: Error) => {
		// 		console.log('e: ', e) //TODO - SLETT MEG
		// 		console.log('e.name: ', e.name) //TODO - SLETT MEG
		// 		console.log('e.cause: ', e.cause) //TODO - SLETT MEG
		// 		console.log('e.message: ', e.message) //TODO - SLETT MEG
		// 		setLoading(false)
		// 		if (e && (e instanceof NotFoundError || e.name === 'NotFoundError')) {
		// 			setNotFound(true)
		// 		} else {
		// 			setError(true)
		// 		}
		// 	})
	}

	return (
		<StyledAdresseVelger>
			<MatrikkelAdresseSok onSubmit={onSubmit} loading={loading} />
			{error && !notFound && (
				<Feil variant={'error'}>Noe gikk galt! Prøv på nytt eller kontakt Team Dolly.</Feil>
			)}
			{notFound && (
				<Advarsel variant={'warning'}>
					Fant ikke et resultat. Prøv å endre kombinasjon av felter i søket.
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
