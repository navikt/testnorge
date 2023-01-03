import { subYears } from 'date-fns'
import * as _ from 'lodash-es'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { EnkeltinntektForm } from './enkeltinntektForm'
import Formatters from '@/utils/DataFormatter'
import { ErrorMessageWithFocus } from '@/utils/ErrorMessageWithFocus'
import React from 'react'

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

	const handleTjenesteChange = (target, path) => {
		formikBag.setFieldValue(`${path}.tjeneste`, target.value)
		formikBag.setFieldValue(`${path}.grunnlag`, [])
		formikBag.setFieldValue(`${path}.svalbardGrunnlag`, [])
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
								isDisabled={_.get(formikBag.values, `${path}.svalbardGrunnlag`, []).length > 0}
								fastfield={false}
								isClearable={false}
								size="large"
								onChange={(target) => handleTjenesteChange(target, path)}
							/>
						</div>
						{tjenesteErValgt(formikBag, path) && (
							<EnkeltinntektForm
								path={`${path}.grunnlag`}
								header="Grunnlag fra Fastlands-Norge"
								initialGrunnlag={initialGrunnlag}
								tjeneste={_.get(formikBag.values, `${path}.tjeneste`)}
								formikBag={formikBag}
							/>
						)}
						{_.get(formikBag.values, `${path}.tjeneste`) === 'SUMMERT_SKATTEGRUNNLAG' && (
							<EnkeltinntektForm
								path={`${path}.svalbardGrunnlag`}
								header="Grunnlag fra Svalbard"
								initialGrunnlag={initialGrunnlag}
								tjeneste={_.get(formikBag.values, `${path}.tjeneste`)}
								formikBag={formikBag}
							/>
						)}

						<div style={{ marginTop: '20px' }}>
							{/* TODO: Vise feilmelding uten at den drunker i annen tekst */}
							{_isString(_.get(formikBag.errors, `${path}.grunnlag`)) && (
								<ErrorMessageWithFocus
									name={`${path}.grunnlag`}
									className="error-message"
									component="div"
								/>
							)}
						</div>
					</React.Fragment>
				</React.Fragment>
			)}
		</FormikDollyFieldArray>
	)
}

const tjenesteErValgt = (formikBag, path) => {
	return _.get(formikBag.values, `${path}.tjeneste`)
}
