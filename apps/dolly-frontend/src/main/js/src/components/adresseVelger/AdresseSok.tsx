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
		adressenavn?: string
		husnummer?: number
		husbokstav?: string
		postnummer?: string
		kommunenummer?: string
	}) => void
	loading?: boolean
}

const InputGroup = styled.div`
	display: flex;
	flex-wrap: wrap;
`
const HeaderGroup = styled.div`
	display: flex;
`

export default ({ onSubmit, loading = false }: Props) => {
	const [adressenavn, setAdressenavn] = useState<string>('')
	const [husnummer, setHusnummer] = useState<string>(null)
	const [husbokstav, setHusbokstav] = useState<string>(null)
	const [postnummer, setPostnummer] = useState<string>(null)
	const [kommunenummer, setKommunenummer] = useState<string>(null)

	const informasjonstekst =
		'Vi er avhengig av at adressen er helt korrekt. Bruk ingen, ett eller flere av søkefeltene for å få forslag til adresse.'

	return (
		<div>
			<HeaderGroup>
				<h4>Søk etter gyldig adresse</h4>
				<Hjelpetekst>{informasjonstekst}</Hjelpetekst>
			</HeaderGroup>
			<InputGroup>
				<DollyTextInput
					name="adressenavn"
					label="Adressenavn"
					value={adressenavn}
					size="xlarge"
					onChange={(e: any) => setAdressenavn(e.target.value)}
				/>
				<DollyTextInput
					name="husnummer"
					label="Husnummer"
					value={husnummer}
					size="small"
					type="number"
					onChange={(e: any) => setHusnummer(e ? e.target.value : null)}
				/>
				<DollyTextInput
					name="husbokstav"
					label="Husbokstav"
					value={husbokstav}
					size="small"
					onChange={(e: any) => setHusbokstav(e ? e.target.value : null)}
				/>
				<DollySelect
					name="postnummer"
					label="Postnummer"
					kodeverk={AdresseKodeverk.PostnummerUtenPostboks}
					size="large"
					value={postnummer}
					onChange={(e: any) => setPostnummer(e ? e.value : null)}
				/>
				<DollySelect
					name="kommunenummer"
					label="Kommunenummer"
					kodeverk={AdresseKodeverk.Kommunenummer}
					size="large"
					value={kommunenummer}
					onChange={(e: any) => setKommunenummer(e ? e.value : null)}
				/>
			</InputGroup>
			<Button
				onClick={() => onSubmit({ adressenavn, husnummer, husbokstav, postnummer, kommunenummer })}
				disabled={loading}
				loading={loading}
			>
				Hent gyldige adresser
			</Button>
		</div>
	)
}
