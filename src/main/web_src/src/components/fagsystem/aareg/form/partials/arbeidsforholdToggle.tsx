import React, { useState } from 'react'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ArbeidsforholdForm } from './arbeidsforholdForm'
import { AmeldingForm } from './ameldingForm'
import { initialValues } from '../initialValues'

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
			//TODO må gå igjennom og settes på alle
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
			{/* <FormikDollyFieldArray
				name="aareg"
				header="Arbeidsforhold"
				newEntry={initialValues[0]}
				canBeEmpty={false}
			>
				{(path, idx) => {
					console.log('path :>> ', path)
					console.log('typeArbeidsforhold :>> ', typeArbeidsforhold)
					console.log('ArbeidsforholdTyper.felles :>> ', ArbeidsforholdTyper.felles)
					typeArbeidsforhold === ArbeidsforholdTyper.felles && (
						<ArbeidsforholdForm path={path} key={idx} formikBag={formikBag} />
					)
				}}
			</FormikDollyFieldArray> */}
		</div>
	)
}
