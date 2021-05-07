import React, { useState } from 'react'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { AmeldingForm } from './ameldingForm'
import { ArbeidsforholdForm } from './arbeidsforholdForm'
import ArbeidsforholdConnector from './arbeidsforholdConnector'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialAmelding,
	initialArbeidsforholdOrg,
	initialArbeidsforholdPers
} from '../initialValues'
import { ArbeidsgiverTyper } from '~/components/fagsystem/aareg/AaregTypes'

//TODO: Bytte navn - er noe annet som heter arbeidsforholdstype
// enum ArbeidsforholdTyper {
// 	egen = 'EGEN',
// 	felles = 'FELLES',
// 	fritekst = 'FRITEKST',
// 	privat = 'PRIVAT'
// }

export const ArbeidsforholdToggle = ({ formikBag }) => {
	const [typeArbeidsforhold, setTypeArbeidsforhold] = useState('EGEN')

	const toggleValues = [
		{
			value: ArbeidsgiverTyper.egen,
			label: 'Egen organisasjon'
		},
		{
			value: ArbeidsgiverTyper.felles,
			label: 'Felles organisasjoner'
		},
		{
			value: ArbeidsgiverTyper.fritekst,
			label: 'Skriv inn org.nr.'
		},
		{
			value: ArbeidsgiverTyper.privat,
			label: 'Privat arbeidsgiver'
		}
	]

	const handleToggleChange = value => {
		setTypeArbeidsforhold(value)
		formikBag.setFieldValue('aareg[0].arbeidsforholdstype', '')
		if (value === ArbeidsgiverTyper.privat) {
			formikBag.setFieldValue('aareg[0].amelding', undefined)
			formikBag.setFieldValue('aareg[0].arbeidsforhold', [initialArbeidsforholdPers])
		} else if (value === ArbeidsgiverTyper.felles || value === ArbeidsgiverTyper.fritekst) {
			formikBag.setFieldValue('aareg[0].amelding', undefined)
			formikBag.setFieldValue('aareg[0].arbeidsforhold', [initialArbeidsforholdOrg])
		} else if (value === ArbeidsgiverTyper.egen) {
			formikBag.setFieldValue('aareg[0].arbeidsforhold', undefined)
			formikBag.setFieldValue('aareg[0].amelding', initialAmelding)
		}
	}

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
			{typeArbeidsforhold === ArbeidsgiverTyper.egen ? (
				<AmeldingForm formikBag={formikBag} />
			) : (
				<FormikDollyFieldArray
					name={`aareg[0].arbeidsforhold`}
					header="Arbeidsforhold"
					newEntry={
						typeArbeidsforhold === ArbeidsgiverTyper.privat
							? initialArbeidsforholdPers
							: initialArbeidsforholdOrg
					}
					canBeEmpty={false}
				>
					{(path, idx) => (
						<ArbeidsforholdConnector
							path={path}
							key={idx}
							formikBag={formikBag}
							erLenket={null}
							arbeidsgiverType={typeArbeidsforhold}
						/>
					)}
				</FormikDollyFieldArray>
			)}
		</div>
	)
}
