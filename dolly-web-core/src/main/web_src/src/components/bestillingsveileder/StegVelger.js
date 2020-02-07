import React, { useState, Fragment } from 'react'
import { Formik } from 'formik'
import Stegindikator from 'nav-frontend-stegindikator'
import { Navigation } from './Navigation/Navigation'
import { stateModifierFns } from './stateModifier'
import { validate } from '~/utils/YupValidations'

import DisplayFormikState from '~/utils/DisplayFormikState'

export const StegVelger = ({ steps, initialValues, onSubmit, children }) => {
	const [step, setStep] = useState(0)

	const isLastStep = () => step === steps.length - 1
	const handleNext = () => setStep(step + 1)

	const handleBack = () => {
		if (step !== 0) setStep(step - 1)
	}

	const _handleSubmit = (values, formikBag) => {
		const { setSubmitting } = formikBag

		if (!isLastStep()) {
			setSubmitting(false)
			handleNext()
			return
		}

		return onSubmit(values, formikBag)
	}

	const CurrentStep = steps[step]

	const labels = steps.map(v => ({ label: v.label }))

	return (
		<Formik
			initialValues={initialValues}
			validate={async values => await validate(values, CurrentStep.validation)}
			onSubmit={_handleSubmit}
			enableReinitialize
		>
			{formikBag => {
				const stateModifier = stateModifierFns(formikBag.values, formikBag.setValues)
				return (
					<Fragment>
						<Stegindikator aktivtSteg={step} steg={labels} visLabel kompakt />

						{children(CurrentStep, formikBag, stateModifier)}

						<Navigation
							showPrevious={step > 0}
							onPrevious={handleBack}
							isLastStep={isLastStep()}
							formikBag={formikBag}
						/>

						{/* Uncomment for å vise FormikState */}
						{/* <DisplayFormikState {...formikBag} /> */}
					</Fragment>
				)
			}}
		</Formik>
	)
}
