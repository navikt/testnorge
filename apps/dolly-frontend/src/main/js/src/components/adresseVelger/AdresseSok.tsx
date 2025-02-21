import React, { useState } from 'react'
// @ts-ignore
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '@/config/kodeverk'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import styled from 'styled-components'
import { Button } from '@navikt/ds-react'

type Props = {
	onSubmit: (search: { fritekst?: string; postnummer?: string; kommunenummer?: string }) => void
	loading?: boolean
}

const InputGroup = styled.div`
	display: flex;
`
const HeaderGroup = styled.div`
	display: flex;
`

export default ({ onSubmit, loading = false }: Props) => {
	const [fritekst, setFritekst] = useState<string>('')
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
					value={fritekst}
					onChange={(e: any) => setFritekst(e.target.value)}
				/>
				<DollySelect
					name="postnummer"
					label="Postnummer"
					kodeverk={AdresseKodeverk.PostnummerUtenPostboks}
					size="grow"
					value={postnummer}
					onChange={(e: any) => setPostnummer(e ? e.value : null)}
				/>
				<DollySelect
					name="kommunenummer"
					label="Kommunenummer"
					kodeverk={AdresseKodeverk.Kommunenummer}
					size="grow"
					value={kommunenummer}
					onChange={(e: any) => setKommunenummer(e ? e.value : null)}
				/>
			</InputGroup>
			<Button
				onClick={() => onSubmit({ fritekst, postnummer, kommunenummer })}
				disabled={loading}
				loading={loading}
			>
				Hent gyldige adresser
			</Button>
		</div>
	)
}
