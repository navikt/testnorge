import React, { useState } from 'react'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { AmeldingForm } from './ameldingForm'

enum ArbeidsforholdTyper {
	egen = 'EGEN',
	felles = 'FELLES',
	fritekst = 'FRITEKST',
	privat = 'PRIVAT'
}

export const ArbeidsforholdToggle = ({ formikBag }) => {
	const [typeArbeidsforhold, setTypeArbeidsforhold] = useState('EGEN')

	const handleToggleChange = value => {
		setTypeArbeidsforhold(value)
		if (value === 'PRIVAT') {
			formikBag.setFieldValue('aareg[0].arbeidsforhold.arbeidsgiver.aktoertype', 'PERS')
		} else {
			//TODO må gå igjennom og settes på alle?
			formikBag.setFieldValue('aareg[0].arbeidsforhold.arbeidsgiver.aktoertype', 'ORG')
		}
	}

	const toggleValues = [
		{
			value: ArbeidsforholdTyper.egen,
			label: 'Egen organisasjon'
		},
		{
			value: ArbeidsforholdTyper.felles,
			label: 'Felles organisasjoner'
		},
		{
			value: ArbeidsforholdTyper.fritekst,
			label: 'Skriv inn org.nr.'
		},
		{
			value: ArbeidsforholdTyper.privat,
			label: 'Privat arbeidsgiver'
		}
	]

	return (
		<div className="toggle--wrapper">
			<ToggleGruppe
				onChange={event => handleToggleChange(event.target.value)}
				name={'arbeidsforhold'}
			>
				{toggleValues.map(type => (
					<ToggleKnapp
						key={type.value}
						value={type.value}
						checked={type.value === typeArbeidsforhold}
					>
						{type.label}
					</ToggleKnapp>
				))}
			</ToggleGruppe>
			{typeArbeidsforhold === ArbeidsforholdTyper.egen && <AmeldingForm formikBag={formikBag} />}
		</div>
	)
}
