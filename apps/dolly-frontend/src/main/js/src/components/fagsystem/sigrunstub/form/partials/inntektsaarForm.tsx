import { subYears } from 'date-fns'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { EnkeltinntektForm } from './enkeltinntektForm'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { ErrorMessage } from '@hookform/error-message'

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
		formMethods.trigger(path)
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
									isDisabled={formMethods.watch(`${path}.svalbardGrunnlag`, []).length > 0}
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
									tjeneste={formMethods.watch(`${path}.tjeneste`)}
									inntektsaar={formMethods.watch(`${path}.inntektsaar`)}
									formMethods={formMethods}
								/>
							)}
							{formMethods.watch(`${path}.tjeneste`) === 'SUMMERT_SKATTEGRUNNLAG' && (
								<EnkeltinntektForm
									path={`${path}.svalbardGrunnlag`}
									header="Grunnlag fra Svalbard"
									initialGrunnlag={initialGrunnlag}
									tjeneste={formMethods.watch(`${path}.tjeneste`)}
									inntektsaar={formMethods.watch(`${path}.inntektsaar`)}
									formMethods={formMethods}
								/>
							)}

							<div style={{ marginTop: '20px' }}>
								<ErrorMessage
									errors={formMethods.formState.errors}
									name={`${path}.grunnlag`}
									render={({ message }) => (
										<span style={{ color: '#ba3a26', fontStyle: 'italic' }}>{message}</span>
									)}
								/>
							</div>
						</React.Fragment>
					</React.Fragment>
				)}
			</FormikDollyFieldArray>
		</ErrorBoundary>
	)
}

const tjenesteErValgt = (formMethods, path) => {
	return formMethods.watch(`${path}.tjeneste`)
}
