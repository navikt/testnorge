import React, { useState } from 'react'
import _get from 'lodash/get'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

const inputValg = { fraListe: 'velg', skrivSelv: 'skriv' }
const initial = (formikBag, path) => {
	const orgnr = _get(formikBag.values, `${path}.arbeidsgiver.orgnummer`)
	return !orgnr || Options('orgnummer').some(org => org.value === orgnr)
		? inputValg.fraListe
		: inputValg.skrivSelv
}

export const OrgnrForm = ({ formikBag, path }) => {
	const [inputType, setInputType] = useState(initial(formikBag, path))

	const handleToggleChange = event => {
		setInputType(event.target.value)
		console.log('path :', path)
		formikBag.setFieldValue(`${path}.arbeidsgiver.orgnummer`, '')
	}

	return (
		<div className="toggle--wrapper">
			<ToggleGruppe onChange={handleToggleChange} name={path}>
				<ToggleKnapp
					key={inputValg.fraListe}
					value={inputValg.fraListe}
					checked={inputType === inputValg.fraListe}
				>
					Velg organisasjonsnummer
				</ToggleKnapp>
				<ToggleKnapp
					key={inputValg.skrivSelv}
					value={inputValg.skrivSelv}
					checked={inputType === inputValg.skrivSelv}
				>
					Skriv inn organisasjonsnummer
				</ToggleKnapp>
			</ToggleGruppe>

			{inputType === inputValg.fraListe ? (
				<FormikSelect
					name={`${path}.arbeidsgiver.orgnummer`}
					options={Options('orgnummer')}
					type="text"
					size="large"
					isClearable={false}
				/>
			) : (
				<FormikTextInput name={`${path}.arbeidsgiver.orgnummer`} />
			)}
		</div>
	)
}
