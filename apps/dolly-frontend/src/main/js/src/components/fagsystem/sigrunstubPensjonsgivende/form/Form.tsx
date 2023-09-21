import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { subYears } from 'date-fns'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { PensjonsgivendeInntektForm } from '@/components/fagsystem/sigrunstubPensjonsgivende/form/PensjonsgivendeInntektForm'

export const initialSigrunstubPensjonsgivende = {
	inntektsaar: new Date().getFullYear(),
	pensjonsgivendeInntekt: [],
	testdataEier: '',
}

export const sigrunstubPensjonsgivendeAttributt = 'sigrunstubPensjonsgivende'

export const SigrunstubPensjonsgivendeForm = ({ formikBag }) => {
	return (
		<Vis attributt={sigrunstubPensjonsgivendeAttributt}>
			<Panel
				heading="Pensjonsgivende inntekt (Sigrun)"
				hasErrors={panelError(formikBag, sigrunstubPensjonsgivendeAttributt)}
				iconType="sigrun"
				startOpen={erForsteEllerTest(formikBag.values, [sigrunstubPensjonsgivendeAttributt])}
			>
				<ErrorBoundary>
					<FormikDollyFieldArray
						name="sigrunstubPensjonsgivende"
						header="Pensjonsgivende inntekt"
						newEntry={initialSigrunstubPensjonsgivende}
						canBeEmpty={false}
					>
						{(path) => (
							<>
								<div className="flexbox--flex-wrap">
									<FormikSelect
										name={`${path}.inntektsaar`}
										label="Inntektsår"
										options={getYearRangeOptions(1968, subYears(new Date(), -5).getFullYear())}
										isClearable={false}
									/>
									<FormikTextInput name={`${path}.testdataEier`} label="Testdataeier" />
								</div>
								<PensjonsgivendeInntektForm
									path={`${path}.pensjonsgivendeInntekt`}
									formikBag={formikBag}
								/>
							</>
						)}
					</FormikDollyFieldArray>
				</ErrorBoundary>
			</Panel>
		</Vis>
	)
}
