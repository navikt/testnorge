import React, { useState, Fragment } from 'react'
import { Formik } from 'formik'
import Stegindikator from 'nav-frontend-stegindikator'
import { Navigation } from './Navigation/Navigation'

import DisplayFormikState from '~/utils/DisplayFormikState'

export const StegVelger = ({ steps, initialValues, onSubmit, copyValues, children }) => {
	const [step, setStep] = useState(0)

	const isLastStep = () => step === steps.length - 1
	const handleNext = (values, formikBag) => {
		setStep(step + 1)
		formikBag.setTouched({})
	}
	const handleBack = values => {
		if (step !== 0) {
			setStep(step - 1)
			copyValues(values)
		}
	}

	const _handleSubmit = (values, formikBag) => {
		const { setSubmitting } = formikBag

		if (!isLastStep()) {
			setSubmitting(false)
			handleNext(values, formikBag)
			return
		}

		// TODO - handle final submit
		return onSubmit(values, formikBag)
	}

	const CurrentStep = steps[step]
	const { validation } = CurrentStep

	const labels = steps.map(v => ({ label: v.label }))

	return (
		<Formik
			initialValues={initialValues}
			validationSchema={validation}
			onSubmit={_handleSubmit}
			enableReinitialize
		>
			{formikBag => (
				<Fragment>
					<Stegindikator aktivtSteg={step} steg={labels} visLabel kompakt />

					{children(CurrentStep, formikBag)}

					<Navigation
						showPrevious={step > 0}
						onPrevious={() => handleBack(formikBag.values)}
						isLastStep={isLastStep()}
						formikBag={formikBag}
					/>

					<DisplayFormikState {...formikBag} />
				</Fragment>
			)}
		</Formik>
	)
}
