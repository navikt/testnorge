import React from 'react'
import { FieldArray } from 'formik'
import { subYears } from 'date-fns'
import _get from 'lodash/get'
import { FieldArrayAddButton } from '~/components/ui/form/formUtils'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { EnkeltinntektForm } from './enkeltinntektForm'

export const InntektsaarForm = ({ formikBag, initial }) => {
	const initialGrunnlag = {
		tekniskNavn: '',
		verdi: ''
	}

	const leggTilGrunnlag = name =>
		formikBag.setFieldValue(name, _get(formikBag.values, name).concat([initialGrunnlag]))

	const pushGrunnlag = idx => leggTilGrunnlag(`sigrunstub[${idx}].grunnlag`)
	const pushSvalbard = idx => leggTilGrunnlag(`sigrunstub[${idx}].svalbardGrunnlag`)

	return (
		<FieldArray name="sigrunstub">
			{({ push }) => (
				<div>
					{formikBag.values.sigrunstub.map((inntektaar, idx) => (
						<React.Fragment key={idx}>
							<FormikSelect
								name={`sigrunstub[${idx}].inntektsaar`}
								label="År"
								options={yearOptions()}
								isClearable={false}
							/>
							<FormikSelect
								name={`sigrunstub[${idx}].tjeneste`}
								label="Tjeneste"
								options={Options('tjeneste')}
							/>

							<FieldArrayAddButton title="Legg til grunnlag" onClick={() => pushGrunnlag(idx)} />
							<FieldArrayAddButton title="Legg til svalbard" onClick={() => pushSvalbard(idx)} />

							<EnkeltinntektForm name={`sigrunstub[${idx}].grunnlag`} formikBag={formikBag} />

							<EnkeltinntektForm
								name={`sigrunstub[${idx}].svalbardGrunnlag`}
								formikBag={formikBag}
							/>
						</React.Fragment>
					))}
					<FieldArrayAddButton title="Legg til inntektsår" onClick={() => push(initial)} />
				</div>
			)}
		</FieldArray>
	)
}

const yearOptions = () => {
	// Mulig å legge inn sigruninntekter fra 1968 og 5 år frem i tid
	let years = []
	for (let i = subYears(new Date(), -5).getFullYear(); i >= 1968; i--) {
		years.push({ value: i, label: i.toString() })
	}
	return years
}
