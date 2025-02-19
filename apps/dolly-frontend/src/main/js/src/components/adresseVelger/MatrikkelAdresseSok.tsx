import React, { useState } from 'react'
// @ts-ignore
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '@/config/kodeverk'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import styled from 'styled-components'
import { Button } from '@navikt/ds-react'

type Props = {
	onSubmit: (search: {
		kommunenummer?: string
		gaardsnummer?: string
		bruksnummer?: string
	}) => void
	loading?: boolean
}

const InputGroup = styled.div`
	display: flex;
`
const HeaderGroup = styled.div`
	display: flex;
`

export default ({ onSubmit, loading = false }: Props) => {
	const [kommunenummer, setKommunenummer] = useState<string>('')
	const [gaardsnummer, setGaardsnummer] = useState<string>('')
	const [bruksnummer, setBruksnummer] = useState<string>('')

	const informasjonstekst =
		'Bruk ingen, ett eller flere av søkefeltene for å få en liste med tilfeldige matrikkeladresser.'

	return (
		<div>
			<HeaderGroup>
				<h4>Søk etter tilfeldig matrikkeladresse</h4>
				<Hjelpetekst>{informasjonstekst}</Hjelpetekst>
			</HeaderGroup>
			<InputGroup>
				<DollySelect
					name="kommunenummer"
					label="Kommunenummer"
					kodeverk={AdresseKodeverk.Kommunenummer}
					size="grow"
					value={kommunenummer}
					onChange={(e: any) => setKommunenummer(e ? e.value : null)}
				/>
				<DollyTextInput
					name="gaardsnummer"
					label="Gårdsnummer"
					value={gaardsnummer}
					onChange={(e: any) => setGaardsnummer(e.target.value)}
				/>
				<DollyTextInput
					name="bruksnummer"
					label="Bruksnummer"
					value={bruksnummer}
					onChange={(e: any) => setBruksnummer(e.target.value)}
				/>
			</InputGroup>
			<Button
				onClick={() => onSubmit({ kommunenummer, gaardsnummer, bruksnummer })}
				disabled={loading}
				loading={loading}
			>
				Hent tilfeldige adresser
			</Button>
		</div>
	)
}
