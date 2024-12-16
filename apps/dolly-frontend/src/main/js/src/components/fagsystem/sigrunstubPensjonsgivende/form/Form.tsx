import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React, { useEffect } from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { subYears } from 'date-fns'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { PensjonsgivendeInntektForm } from '@/components/fagsystem/sigrunstubPensjonsgivende/form/PensjonsgivendeInntektForm'
import {
	usePensjonsgivendeInntektKodeverk,
	usePensjonsgivendeInntektSkatteordning,
} from '@/utils/hooks/useSigrunstub'
import { getInitialInntekt } from '@/components/fagsystem/sigrunstubPensjonsgivende/utils'
import { validation } from '@/components/fagsystem/sigrunstubPensjonsgivende/form/validation'
import { useFormContext } from 'react-hook-form'
import * as _ from 'lodash-es'

export const getInitialSigrunstubPensjonsgivende = (kodeverk = null, skatteordning = null) => {
	return {
		inntektsaar: new Date().getFullYear(),
		pensjonsgivendeInntekt: [
			{
				skatteordning: 'FASTLAND',
				datoForFastsetting: new Date(),
				pensjonsgivendeInntektAvLoennsinntekt: null,
				pensjonsgivendeInntektAvLoennsinntektBarePensjonsdel: null,
				pensjonsgivendeInntektAvNaeringsinntekt: null,
				pensjonsgivendeInntektAvNaeringsinntektFraFiskeFangstEllerFamiliebarnehage: null,
			},
		],
		testdataEier: '',
	}
}

export const sigrunstubPensjonsgivendeAttributt = 'sigrunstubPensjonsgivende'

export const SigrunstubPensjonsgivendeForm = () => {
	const formMethods = useFormContext()
	const { kodeverk } = usePensjonsgivendeInntektKodeverk()
	const { skatteordning } = usePensjonsgivendeInntektSkatteordning()
	const arrayError = _.get(formMethods.formState.errors, 'sigrunstubPensjonsgivende.root')?.message

	useEffect(() => {
		const pensjonsgivendeInntektPath = 'sigrunstubPensjonsgivende[0].pensjonsgivendeInntekt'
		const forstePensjonsgivendeInntekt = formMethods.watch(pensjonsgivendeInntektPath)
		if (
			formMethods.watch('sigrunstubPensjonsgivende') &&
			kodeverk &&
			skatteordning &&
			(!forstePensjonsgivendeInntekt || forstePensjonsgivendeInntekt.length < 1)
		) {
			formMethods.setValue(pensjonsgivendeInntektPath, [getInitialInntekt(kodeverk, skatteordning)])
			formMethods.trigger(pensjonsgivendeInntektPath)
		}
	}, [kodeverk, skatteordning])

	return (
		<Vis attributt={sigrunstubPensjonsgivendeAttributt}>
			<Panel
				heading="Pensjonsgivende inntekt (Sigrun)"
				hasErrors={panelError(sigrunstubPensjonsgivendeAttributt)}
				iconType="sigrun"
				startOpen={erForsteEllerTest(formMethods.getValues(), [sigrunstubPensjonsgivendeAttributt])}
			>
				<ErrorBoundary>
					<FormDollyFieldArray
						name="sigrunstubPensjonsgivende"
						header="Pensjonsgivende inntekt"
						newEntry={getInitialSigrunstubPensjonsgivende(kodeverk, skatteordning)}
						errorText={arrayError}
						canBeEmpty={false}
					>
						{(path) => (
							<>
								<div className="flexbox--flex-wrap">
									<FormSelect
										name={`${path}.inntektsaar`}
										label="Inntektsår"
										options={getYearRangeOptions(2017, subYears(new Date(), 1).getFullYear())}
										isClearable={false}
									/>
									<FormTextInput name={`${path}.testdataEier`} label="Testdataeier" />
								</div>
								<PensjonsgivendeInntektForm
									path={`${path}.pensjonsgivendeInntekt`}
									formMethods={formMethods}
									kodeverk={kodeverk}
									skatteordning={skatteordning}
								/>
							</>
						)}
					</FormDollyFieldArray>
				</ErrorBoundary>
			</Panel>
		</Vis>
	)
}

SigrunstubPensjonsgivendeForm.validation = validation
