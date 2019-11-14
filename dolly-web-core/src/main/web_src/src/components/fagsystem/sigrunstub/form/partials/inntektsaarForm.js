import React from 'react'
import { FieldArray } from 'formik'
import { subYears } from 'date-fns'
import _get from 'lodash/get'
import { FieldArrayAddButton, FieldArrayRemoveButton } from '~/components/ui/form/formUtils'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { EnkeltinntektForm } from './enkeltinntektForm'
import Formatters from '~/utils/DataFormatter'

export const InntektsaarForm = ({ formikBag, initial }) => {
	const initialGrunnlag = {
		tekniskNavn: '',
		verdi: ''
	}
	const inntektsaarArray = _get(formikBag.values, 'sigrunstub', [])

	const fjern = (idx, path, currentArray) => {
		const nyArray = currentArray.filter((_, _idx) => _idx !== idx)
		formikBag.setFieldValue(path, nyArray)
	}

	const leggTilGrunnlag = name =>
		formikBag.setFieldValue(name, _get(formikBag.values, name).concat([initialGrunnlag]))

	const pushGrunnlag = idx => leggTilGrunnlag(`sigrunstub[${idx}].grunnlag`)
	const pushSvalbard = idx => leggTilGrunnlag(`sigrunstub[${idx}].svalbardGrunnlag`)

	return (
		<FieldArray name="sigrunstub">
			{({ push }) => (
				<div>
					{inntektsaarArray.map((inntektsaar, idx) => (
						<React.Fragment key={idx}>
							<div className="flexbox">
								<FormikSelect
									name={`sigrunstub[${idx}].inntektsaar`}
									label="År"
									options={Formatters.getYearRangeOptions(
										1968,
										subYears(new Date(), -5).getFullYear()
									)}
									isClearable={false}
								/>
								<FormikSelect
									name={`sigrunstub[${idx}].tjeneste`}
									label="Tjeneste"
									options={Options('tjeneste')}
									isDisabled={inntektsaar.svalbardGrunnlag.length > 0}
									isClearable={false}
								/>

								{inntektsaar.tjeneste && (
									<FieldArrayAddButton
										title="Inntekt fra Fastlands-Norge"
										onClick={() => pushGrunnlag(idx)}
									/>
								)}
								{inntektsaar.tjeneste === 'SUMMERT_SKATTEGRUNNLAG' && (
									<FieldArrayAddButton
										title="Inntekt fra Svalbard"
										onClick={() => pushSvalbard(idx)}
									/>
								)}
								<FieldArrayRemoveButton
									onClick={() => fjern(idx, 'sigrunstub', inntektsaarArray)}
								/>
							</div>

							<EnkeltinntektForm
								name={`sigrunstub[${idx}].grunnlag`}
								tjeneste={formikBag.values.sigrunstub[idx].tjeneste}
								formikBag={formikBag}
								fjern={fjern}
							/>

							<EnkeltinntektForm
								name={`sigrunstub[${idx}].svalbardGrunnlag`}
								tjeneste={formikBag.values.sigrunstub[idx].tjeneste}
								formikBag={formikBag}
								fjern={fjern}
							/>
						</React.Fragment>
					))}
					<FieldArrayAddButton title="Legg til inntektsår" onClick={() => push(initial)} />
				</div>
			)}
		</FieldArray>
	)
}
