import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React, { useEffect } from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { subYears } from 'date-fns'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { PensjonsgivendeInntektForm } from '@/components/fagsystem/sigrunstubPensjonsgivende/form/PensjonsgivendeInntektForm'
import {
	usePensjonsgivendeInntektKodeverk,
	usePensjonsgivendeInntektSkatteordning,
} from '@/utils/hooks/useSigrunstub'
import { getInitialInntekt } from '@/components/fagsystem/sigrunstubPensjonsgivende/utils'
import * as _ from 'lodash-es'
import { validation } from '@/components/fagsystem/sigrunstubPensjonsgivende/form/validation'

export const getInitialSigrunstubPensjonsgivende = (kodeverk = null, skatteordning = null) => {
	return {
		inntektsaar: new Date().getFullYear(),
		pensjonsgivendeInntekt:
			kodeverk && skatteordning ? [getInitialInntekt(kodeverk, skatteordning)] : [],
		testdataEier: '',
	}
}

export const sigrunstubPensjonsgivendeAttributt = 'sigrunstubPensjonsgivende'

export const SigrunstubPensjonsgivendeForm = ({ formMethods }) => {
	const { kodeverk } = usePensjonsgivendeInntektKodeverk()
	const { skatteordning } = usePensjonsgivendeInntektSkatteordning()

	useEffect(() => {
		const pensjonsgivendeInntektPath = 'sigrunstubPensjonsgivende[0].pensjonsgivendeInntekt'
		const forstePensjonsgivendeInntekt = _.get(formMethods.getValues(), pensjonsgivendeInntektPath)
		if (
			_.has(formMethods.getValues(), 'sigrunstubPensjonsgivende') &&
			kodeverk &&
			skatteordning &&
			(!forstePensjonsgivendeInntekt || forstePensjonsgivendeInntekt.length < 1)
		) {
			formMethods.setValue(pensjonsgivendeInntektPath, [getInitialInntekt(kodeverk, skatteordning)])
		}
	}, [kodeverk, skatteordning])

	return (
		<Vis attributt={sigrunstubPensjonsgivendeAttributt}>
			<Panel
				heading="Pensjonsgivende inntekt (Sigrun)"
				hasErrors={panelError(formMethods.formState.errors, sigrunstubPensjonsgivendeAttributt)}
				iconType="sigrun"
				startOpen={erForsteEllerTest(formMethods.getValues(), [sigrunstubPensjonsgivendeAttributt])}
			>
				<ErrorBoundary>
					<FormikDollyFieldArray
						name="sigrunstubPensjonsgivende"
						header="Pensjonsgivende inntekt"
						newEntry={getInitialSigrunstubPensjonsgivende(kodeverk, skatteordning)}
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
									formMethods={formMethods}
									kodeverk={kodeverk}
									skatteordning={skatteordning}
								/>
							</>
						)}
					</FormikDollyFieldArray>
				</ErrorBoundary>
			</Panel>
		</Vis>
	)
}

SigrunstubPensjonsgivendeForm.validation = validation
