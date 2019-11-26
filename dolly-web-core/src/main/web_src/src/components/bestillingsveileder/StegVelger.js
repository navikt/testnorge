import React, { useState, Fragment } from 'react'
import { Formik, yupToFormErrors } from 'formik'
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

	const _validate = async (values, schema) => {
		if (!schema) return
		try {
			await schema.validate(values, { abortEarly: false, context: values })
			return {}
		} catch (err) {
			return yupToFormErrors(err)
		}
	}

	const CurrentStep = steps[step]

	const labels = steps.map(v => ({ label: v.label }))

	return (
		<Formik
			initialValues={initialValues}
			validate={async values => await _validate(values, CurrentStep.validation)}
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
