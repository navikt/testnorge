import React, { useState } from 'react'
import _get from 'lodash/get'
import styled from 'styled-components'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { AmeldingForm } from './ameldingForm'
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

// Har hardkodet liste over felles Dolly-arbeidsgivere, fordi det tar for lang tid å hente ut fra API
const fellesOrg = [
	'972674818',
	'896929119',
	'839942907',
	'967170232',
	'805824352',
	'907670201',
	'947064649'
]

export const ArbeidsforholdToggle = ({ formikBag }) => {
	const getArbeidsgiverType =
		_get(formikBag.values, 'aareg[0].arbeidsforhold') &&
		_get(formikBag.values, 'aareg[0].arbeidsforholdstype') !== 'forenkletOppgjoersordning'
			? _get(formikBag.values, 'aareg[0].arbeidsforhold[0].arbeidsgiver.aktoertype') === 'PERS'
				? ArbeidsgiverTyper.privat
				: fellesOrg.some(
						org =>
							org === _get(formikBag.values, 'aareg[0].arbeidsforhold[0].arbeidsgiver.orgnummer')
				  )
				? ArbeidsgiverTyper.felles
				: ArbeidsgiverTyper.fritekst
			: ArbeidsgiverTyper.egen

	const [typeArbeidsgiver, setTypeArbeidsgiver] = useState(getArbeidsgiverType)

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
		setTypeArbeidsgiver(value)
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
						checked={type.value === typeArbeidsgiver}
					>
						{type.label}
					</ToggleKnapp>
				))}
			</ToggleArbeidsgiver>
			{typeArbeidsgiver === ArbeidsgiverTyper.egen ? (
				<AmeldingForm formikBag={formikBag} />
			) : (
				<FormikDollyFieldArray
					name={`aareg[0].arbeidsforhold`}
					header="Arbeidsforhold"
					newEntry={
						typeArbeidsgiver === ArbeidsgiverTyper.privat
							? initialArbeidsforholdPers
							: initialArbeidsforholdOrg
					}
					canBeEmpty={false}
				>
					{(path, idx) => (
						<ArbeidsforholdConnector
							path={path}
							key={idx}
							arbeidsforholdIndex={idx}
							formikBag={formikBag}
							erLenket={null}
							arbeidsgiverType={typeArbeidsgiver}
						/>
					)}
				</FormikDollyFieldArray>
			)}
		</div>
	)
}
