import React, { useState } from 'react'
import styled from 'styled-components'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { Bruker, Organisasjon } from '~/pages/brukerPage/types'
import { BrukerApi } from '~/service/Api'

type BrukernavnVelgerProps = {
	organisasjon: Organisasjon
	addToSession: (org: string) => void
}

const Selector = styled.div`
	display: flexbox;
	text-align: left;
	justify-content: center;
	margin-bottom: 20px;
`

const feilmeldinger = {
	ukjent: 'Noe gikk galt. Vennligst prøv på nytt.',
	alleredeTatt: 'Brukernavn er allerede tatt. Vennligst velg et annet.',
	feilLengde: 'Brukernavn må være minst 5 tegn og maksimalt 25 tegn.',
	feilTegn: 'Brukernavn kan kun inneholde bokstaver og tall.',
}

export default ({ organisasjon, addToSession }: BrukernavnVelgerProps) => {
	const [brukernavn, setBrukernavn] = useState<string>('')
	const [loading, setLoading] = useState(false)
	const [error, setError] = useState(null)

	const onSubmit = () => {
		setError(null)
		setLoading(true)
		BrukerApi.getBrukernavnStatus(brukernavn)
			.then((status: number) => {
				if (status === 404) {
					BrukerApi.opprettBruker(brukernavn, organisasjon.organisasjonsnummer)
						.then((response: Bruker) => {
							if (response !== null) {
								addToSession(organisasjon.organisasjonsnummer)
							} else {
								setError(feilmeldinger.ukjent)
							}
						})
						.catch(() => setError(feilmeldinger.ukjent))
				} else if (status === 200) {
					setError(feilmeldinger.alleredeTatt)
				} else {
					setError(feilmeldinger.ukjent)
				}
			})
			.catch(() => setError(feilmeldinger.ukjent))
		setLoading(false)
	}

	const onChange = (brukernavn: string) => {
		setBrukernavn(brukernavn)
		if (brukernavn.length < 5 || brukernavn.length > 25) {
			setError(feilmeldinger.feilLengde)
		} else if (!/^[A-Za-z0-9]*$/.test(brukernavn)) {
			setError(feilmeldinger.feilTegn)
		} else {
			setError(null)
		}
	}

	return (
		<React.Fragment>
			<h3>
				Lag ditt eget brukernavn som brukes når du representerer <b>{organisasjon.navn}</b>. Neste
				gang du logger inn for denne organisasjonen skjer det automatisk med dette brukernavnet.
			</h3>

			<Selector>
				<DollyTextInput
					name="brukernavn"
					label="Brukernavn"
					value={brukernavn}
					// @ts-ignore
					size="xlarge"
					onChange={(e: any) => onChange(e.target.value)}
					disabled={loading}
					feil={
						error && {
							feilmelding: error,
						}
					}
				/>
				<NavButton
					onClick={() => onSubmit()}
					type="hoved"
					className="videre-button"
					disabled={error}
				>
					Gå videre til Dolly
				</NavButton>
			</Selector>
		</React.Fragment>
	)
}
