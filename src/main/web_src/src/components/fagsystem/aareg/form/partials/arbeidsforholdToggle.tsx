import React, { useState } from 'react'
import styled from 'styled-components'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { AmeldingForm } from './ameldingForm'
import { ArbeidsforholdForm } from './arbeidsforholdForm'
import ArbeidsforholdConnector from './arbeidsforholdConnector'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialPeriode,
	initialAmelding,
	initialArbeidsforholdOrg,
	initialArbeidsforholdPers
} from '../initialValues'
import { ArbeidsgiverTyper } from '~/components/fagsystem/aareg/AaregTypes'

const ToggleArbeidsgiver = styled(ToggleGruppe)`
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
`

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
			formikBag.setFieldValue('aareg[0].genererPeriode', undefined)
		} else if (value === ArbeidsgiverTyper.felles || value === ArbeidsgiverTyper.fritekst) {
			formikBag.setFieldValue('aareg[0].amelding', undefined)
			formikBag.setFieldValue('aareg[0].arbeidsforhold', [initialArbeidsforholdOrg])
			formikBag.setFieldValue('aareg[0].genererPeriode', undefined)
		} else if (value === ArbeidsgiverTyper.egen) {
			formikBag.setFieldValue('aareg[0].arbeidsforhold', undefined)
			formikBag.setFieldValue('aareg[0].amelding', initialAmelding)
			formikBag.setFieldValue('aareg[0].genererPeriode', initialPeriode)
		}
	}

	return (
		<div className="toggle--wrapper">
			<ToggleArbeidsgiver
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
			</ToggleArbeidsgiver>
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
