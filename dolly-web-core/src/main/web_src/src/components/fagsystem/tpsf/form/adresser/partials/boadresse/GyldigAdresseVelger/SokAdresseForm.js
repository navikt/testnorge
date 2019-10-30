import React, { useState } from 'react'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import Button from '~/components/ui/button/Button'

export const SokAdresseForm = ({ onSearch }) => {
	const [gatenavn, setGatenavn] = useState('')
	const [postnummer, setPostnummer] = useState('')
	const [kommunenummer, setKommunenummer] = useState('')

	const sokAdresse = () => {
		const query = createQueryString(gatenavn, postnummer, kommunenummer)
		return onSearch(query)
	}

	return (
		<div>
			<h4>Søk adresse</h4>
			<div className="flexbox">
				<DollyTextInput
					name="gatenavn"
					label="Gatenavn"
					value={gatenavn}
					onChange={e => setGatenavn(e.target.value)}
				/>
				<DollySelect
					name="postnummer"
					label="Postnummer"
					kodeverk="Postnummer"
					size="grow"
					value={postnummer}
					onChange={v => setPostnummer((v && v.value) || '')}
				/>
				<DollySelect
					name="kommunenummer"
					label="Kommunenummer"
					kodeverk="Kommuner"
					size="grow"
					value={kommunenummer}
					onChange={v => setKommunenummer((v && v.value) || '')}
				/>
			</div>
			<Button onClick={sokAdresse}>Hent gyldig adresser</Button>
		</div>
	)
}

const createQueryString = (gate, postnr, kommuneNr) => {
	let queryString = ''
	if (gate) {
		queryString += `&adresseNavnsok=${gate}`
	}
	if (postnr) {
		queryString += `&postNrsok=${postnr}`
	}
	if (kommuneNr) {
		queryString += `&kommuneNrsok=${kommuneNr}`
	}
	return queryString
}
