import React, { useState } from 'react'
import Hjelpetekst from '~/components/hjelpetekst'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import Icon from '~/components/ui/icon/Icon'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { AdresseKodeverk } from '~/config/kodeverk'
import _get from 'lodash/get'

export const SokAdresseForm = ({ onSearch, formikBag }) => {
	const [gatenavn, setGatenavn] = useState('')
	const [postnummer, setPostnummer] = useState(_get(formikBag.values, 'tpsf.boadresse.postnr'))
	const [kommunenummer, setKommunenummer] = useState(
		_get(formikBag.values, 'tpsf.boadresse.kommunenr')
	)

	const sokAdresse = () => {
		const query = createQueryString(gatenavn, postnummer, kommunenummer)
		return onSearch(query)
	}
	const informasjonstekst =
		'TPS er avhengig av at adressen er helt korrekt. Bruk ingen, ett eller flere av søkefeltene for å få forslag til adresse fra TPS.'

	return (
		<div className="soekAdresse">
			<div className="flexbox">
				<h4>Søk etter gyldig adresse</h4>{' '}
				<Hjelpetekst hjelpetekstFor="Søk etter gyldig adresse">{informasjonstekst}</Hjelpetekst>
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
					kodeverk={AdresseKodeverk.PostnummerUtenPostboks}
					size="grow"
					value={postnummer}
					onChange={v => setPostnummer((v && v.value) || '')}
				/>
				<DollySelect
					name="kommunenummer"
					label="Kommunenummer"
					kodeverk={AdresseKodeverk.Kommunenummer}
					size="grow"
					value={kommunenummer}
					onChange={v => setKommunenummer((v && v.value) || '')}
				/>
			</div>
			<NavButton form="kompakt" className="knapp" onClick={sokAdresse}>
				<div className="knapp-header">
					<Icon size={15} kind="search" />
					Hent gyldige adresser
				</div>
			</NavButton>
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
