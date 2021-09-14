import React from 'react'
import { ErrorMessage } from 'formik'
import { subYears } from 'date-fns'
import _get from 'lodash/get'
import _isString from 'lodash/isString'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { EnkeltinntektForm } from './enkeltinntektForm'
import Formatters from '~/utils/DataFormatter'

const initialValues = {
	inntektsaar: new Date().getFullYear(),
	tjeneste: '',
	grunnlag: [],
	svalbardGrunnlag: [],
}

export const InntektsaarForm = ({ formikBag }) => {
	const initialGrunnlag = {
		tekniskNavn: '',
		verdi: '',
	}

	return (
		<FormikDollyFieldArray name="sigrunstub" header="Inntekt" newEntry={initialValues}>
			{(path) => (
				<React.Fragment>
					<React.Fragment>
						<div className="flexbox--flex-wrap">
							<FormikSelect
								name={`${path}.inntektsaar`}
								label="År"
								options={Formatters.getYearRangeOptions(
									1968,
									subYears(new Date(), -5).getFullYear()
								)}
								isClearable={false}
							/>
							<FormikSelect
								name={`${path}.tjeneste`}
								label="Tjeneste"
								options={Options('tjeneste')}
								disabled={_get(formikBag.values, `${path}.svalbardGrunnlag`, []).length > 0}
								fastfield={false}
								isClearable={false}
							/>
						</div>
						{tjenesteErValgt(formikBag, path) && (
							<EnkeltinntektForm
								path={`${path}.grunnlag`}
								header="Grunnlag fra Fastlands-Norge"
								initialGrunnlag={initialGrunnlag}
								tjeneste={_get(formikBag.values, `${path}.tjeneste`)}
							/>
						)}
						{_get(formikBag.values, `${path}.tjeneste`) === 'SUMMERT_SKATTEGRUNNLAG' && (
							<EnkeltinntektForm
								path={`${path}.svalbardGrunnlag`}
								header="Grunnlag fra Svalbard"
								initialGrunnlag={initialGrunnlag}
								tjeneste={_get(formikBag.values, `${path}.tjeneste`)}
							/>
						)}

						<div style={{ marginTop: '20px' }}>
							{/* TODO: Vise feilmelding uten at den drunker i annen tekst */}
							{_isString(_get(formikBag.errors, `${path}.grunnlag`)) && (
								<ErrorMessage name={`${path}.grunnlag`} className="error-message" component="div" />
							)}
						</div>
					</React.Fragment>
				</React.Fragment>
			)}
		</FormikDollyFieldArray>
	)
}

const tjenesteErValgt = (formikBag, path) => {
	return _get(formikBag.values, `${path}.tjeneste`)
}
