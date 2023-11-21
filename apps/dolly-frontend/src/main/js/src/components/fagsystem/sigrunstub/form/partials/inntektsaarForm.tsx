import { subYears } from 'date-fns'
import * as _ from 'lodash-es'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { EnkeltinntektForm } from './enkeltinntektForm'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { ErrorMessageWithFocus } from '@/utils/ErrorMessageWithFocus'
import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'

const initialValues = {
	inntektsaar: new Date().getFullYear(),
	tjeneste: '',
	grunnlag: [],
	svalbardGrunnlag: [],
}

export const InntektsaarForm = ({ formMethods }) => {
	const initialGrunnlag = {
		tekniskNavn: '',
		verdi: '',
	}

	const handleTjenesteChange = (target, path) => {
		formMethods.setValue(`${path}.tjeneste`, target.value)
		formMethods.setValue(`${path}.grunnlag`, [])
		formMethods.setValue(`${path}.svalbardGrunnlag`, [])
	}

	return (
		<ErrorBoundary>
			<FormikDollyFieldArray name="sigrunstub" header="Inntekt" newEntry={initialValues}>
				{(path) => (
					<React.Fragment>
						<React.Fragment>
							<div className="flexbox--flex-wrap">
								<FormikSelect
									name={`${path}.inntektsaar`}
									label="År"
									options={getYearRangeOptions(1968, subYears(new Date(), -5).getFullYear())}
									isClearable={false}
								/>
								<FormikSelect
									name={`${path}.tjeneste`}
									label="Tjeneste"
									options={Options('tjeneste')}
									isDisabled={
										_.get(formMethods.getValues(), `${path}.svalbardGrunnlag`, []).length > 0
									}
									fastfield={false}
									isClearable={false}
									size="large"
									onChange={(target) => handleTjenesteChange(target, path)}
								/>
							</div>
							{tjenesteErValgt(formMethods, path) && (
								<EnkeltinntektForm
									path={`${path}.grunnlag`}
									header="Grunnlag fra Fastlands-Norge"
									initialGrunnlag={initialGrunnlag}
									tjeneste={_.get(formMethods.getValues(), `${path}.tjeneste`)}
									inntektsaar={_.get(formMethods.getValues(), `${path}.inntektsaar`)}
									formMethods={formMethods}
								/>
							)}
							{_.get(formMethods.getValues(), `${path}.tjeneste`) === 'SUMMERT_SKATTEGRUNNLAG' && (
								<EnkeltinntektForm
									path={`${path}.svalbardGrunnlag`}
									header="Grunnlag fra Svalbard"
									initialGrunnlag={initialGrunnlag}
									tjeneste={_.get(formMethods.getValues(), `${path}.tjeneste`)}
									inntektsaar={_.get(formMethods.getValues(), `${path}.inntektsaar`)}
									formMethods={formMethods}
								/>
							)}

							<div style={{ marginTop: '20px' }}>
								{_.isString(_.get(formMethods.formState.errors, `${path}.grunnlag`)) && (
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
		</ErrorBoundary>
	)
}

const tjenesteErValgt = (formMethods, path) => {
	return _.get(formMethods.getValues(), `${path}.tjeneste`)
}
