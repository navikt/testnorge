import React, { useState } from 'react'
import Knapp from 'nav-frontend-knapper'
import HjelpeTekst from 'nav-frontend-hjelpetekst'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import Icon from '~/components/ui/icon/Icon'
import Button from '~/components/ui/button/Button'

export const SokAdresseForm = ({ onSearch }) => {
	const [gatenavn, setGatenavn] = useState('')
	const [postnummer, setPostnummer] = useState('')
	const [kommunenummer, setKommunenummer] = useState('')

	const sokAdresse = () => {
		const query = createQueryString(gatenavn, postnummer, kommunenummer)
		return onSearch(query)
	}
	const informasjonstekst =
		'TPS er avhengig av at adressen er helt korrekt. Bruk ingen, ett eller flere av søkefeltene for å få forslag til adresse fra TPS.'

	return (
		<div className="soekAdresse">
			<div className="flexbox">
				<h4>Søk etter gyldig adresse</h4> <HjelpeTekst>{informasjonstekst}</HjelpeTekst>
			</div>
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
			<Knapp form="kompakt" className="knapp" onClick={sokAdresse}>
				<div className="knapp-header">
					<Icon size={15} kind="search" />
					Hent gyldige adresser
				</div>
			</Knapp>
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
