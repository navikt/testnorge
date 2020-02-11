import React, { useState } from 'react'
import _get from 'lodash/get'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

const inputValg = { velg: 'velg', skriv: 'skriv' }
const initial = (formikBag, path) => {
	const orgnr = _get(formikBag.values, `${path}.arbeidsgiver.orgnummer`)
	return !orgnr || Options('orgnummer').some(org => org.value === orgnr)
		? inputValg.velg
		: inputValg.skriv
}

export const OrgnrForm = ({ formikBag, path }) => {
	const [inputType, setInputType] = useState(initial(formikBag, path))

	const handleToggleChange = event => setInputType(event.target.value)

	return (
		<div className="toggle--wrapper">
			<ToggleGruppe onChange={handleToggleChange} name={path}>
				<ToggleKnapp
					key={inputValg.velg}
					value={inputValg.velg}
					checked={inputType === inputValg.velg}
				>
					Velg organisasjonsnummer
				</ToggleKnapp>
				<ToggleKnapp
					key={inputValg.skriv}
					value={inputValg.skriv}
					checked={inputType === inputValg.skriv}
				>
					Skriv inn organisasjonsnummer
				</ToggleKnapp>
			</ToggleGruppe>

			{inputType === inputValg.velg ? (
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
