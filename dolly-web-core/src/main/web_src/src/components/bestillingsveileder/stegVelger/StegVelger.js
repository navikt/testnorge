import React, { useState, Fragment } from 'react'
import { Formik } from 'formik'
import Stegindikator from 'nav-frontend-stegindikator'
import { Navigation } from './Navigation/Navigation'
import { stateModifierFns, stateModifierSeparateNamesAndIds, stateModifierCombineNamesAndIds} from '../stateModifier'
import { validate } from '~/utils/YupValidations'
import { BestillingsveilederHeader } from '../BestillingsveilederHeader'
import {pdlfIdPaths} from "~/components/bestillingsveileder/utils";

import DisplayFormikState from '~/utils/DisplayFormikState'

import { Steg1 } from './steg/steg1/Steg1'
import { Steg2 } from './steg/steg2/Steg2'
import { Steg3 } from './steg/steg3/Steg3'

const STEPS = [Steg1, Steg2, Steg3]


export const StegVelger = ({ initialValues, onSubmit, children }) => {
	const [step, setStep] = useState(0)
	const [selectedPdlfIds, setSelectedPdlfIds] = useState(new Array(pdlfIdPaths.length))

	const isLastStep = () => step === STEPS.length - 1
	const handleNext = () => setStep(step + 1)

	const handleBack = (formikBag) =>{
		if(isLastStep()){
			stateModifierCombineNamesAndIds(formikBag, selectedPdlfIds, setSelectedPdlfIds)
		}
		if (step !== 0) setStep(step - 1)
	}

	const _handleSubmit = (values, formikBag) => {
		const { setSubmitting } = formikBag

		if (!isLastStep()) {
			setSubmitting(false)
			if(step===STEPS.length - 2){
				stateModifierSeparateNamesAndIds(values, formikBag.setFieldValue, selectedPdlfIds, setSelectedPdlfIds)
			}

			handleNext()
			return
		}

		return onSubmit(values, formikBag)
	}

	const CurrentStepComponent = STEPS[step]

	const _validate = values => validate(values, CurrentStepComponent.validation)

	const labels = STEPS.map(v => ({ label: v.label }))

	return (
		<Formik initialValues={initialValues} validate={_validate} onSubmit={_handleSubmit}>
			{formikBag => {
				const stateModifier = stateModifierFns(formikBag.values, formikBag.setValues)
				return (
					<Fragment>
						<Stegindikator aktivtSteg={step} steg={labels} visLabel kompakt />

						<BestillingsveilederHeader />

						<CurrentStepComponent formikBag={formikBag} stateModifier={stateModifier} />

						 {/*<DisplayFormikState {...formikBag} />*/}

						<Navigation
							showPrevious={step > 0}
							onPrevious={handleBack}
							isLastStep={isLastStep()}
							formikBag={formikBag}
						/>
					</Fragment>
				)
			}}
		</Formik>
	)
}