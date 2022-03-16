import React, { BaseSyntheticEvent, useState } from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ifPresent, requiredString } from '~/utils/YupValidations'
import { ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { ToggleGruppe } from 'nav-frontend-skjema'

// @ts-ignore
export const MalForm = ({ formikBag }) => {
	console.log('formikBag.values: ', formikBag.values) //TODO - SLETT MEG
	enum MalTyper {
		INGEN = 'EGEN',
		OPPRETT = 'OPPRETT',
		ENDRE = 'ENDRE',
	}

	const toggleValues = [
		{
			value: MalTyper.INGEN,
			label: 'Ikke opprett',
		},
		{
			value: MalTyper.OPPRETT,
			label: 'Legg til ny mal',
		},
		{
			value: MalTyper.ENDRE,
			label: 'Endre eksisterende mal',
		},
	]
	const [typeMal, setTypeMal] = useState(MalTyper.INGEN)

	const handleToggleChange = (value: MalTyper) => {
		setTypeMal(value)
		if (value === MalTyper.INGEN) {
			formikBag.setFieldValue('malBestillingNavn', undefined)
		} else if (value === MalTyper.OPPRETT) {
			formikBag.setFieldValue('malBestillingNavn', '')
		} else if (value === MalTyper.ENDRE) {
			formikBag.setFieldValue('malBestillingNavn', '')
		}
	}

	//TODO: Sjekke om malnavn allerede finnes
	return (
		<div className="input-oppsummering">
			<h2>Lagre som mal</h2>
			<div className="flexbox--align-center">
				<div className="toggle--wrapper">
					<ToggleGruppe
						onChange={(e: BaseSyntheticEvent) => handleToggleChange(e.target.value)}
						name={'arbeidsforhold'}
					>
						{toggleValues.map((type) => (
							<ToggleKnapp key={type.value} value={type.value} checked={type.value === typeMal}>
								{type.label}
							</ToggleKnapp>
						))}
					</ToggleGruppe>
				</div>
			</div>
			<FormikTextInput name="malBestillingNavn" label="Malnavn" />
		</div>
	)
}

MalForm.validation = {
	malBestillingNavn: ifPresent('$malBestillingNavn', requiredString),
}
