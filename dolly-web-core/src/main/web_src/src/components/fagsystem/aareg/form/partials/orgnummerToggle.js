import React, { useState } from 'react'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { OrganisasjonMedArbeidsforholdSelect } from '~/components/organisasjonSelect'

const inputValg = { fraListe: 'velg', skrivSelv: 'skriv' }

export const OrgnummerToggle = ({ formikBag, path }) => {
	const [inputType, setInputType] = useState(inputValg.fraListe)

	const handleToggleChange = event => {
		setInputType(event.target.value)
		formikBag.setFieldValue(path, '')
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
				<OrganisasjonMedArbeidsforholdSelect path={path} label={'Organisasjonsnummer'} />
			) : (
				<FormikTextInput name={path} size="xlarge" />
			)}
		</div>
	)
}
