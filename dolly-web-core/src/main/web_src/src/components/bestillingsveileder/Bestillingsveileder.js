import React, { useState } from 'react'
import { Formik } from 'formik'
import Stegindikator from 'nav-frontend-stegindikator'
import { Navigation } from './Navigation/Navigation'
import { Steg1 } from './steg/Steg1'
import { Steg2 } from './steg/Steg2'

import DisplayFormikState from '~/utils/DisplayFormikState'

import './bestillingsveileder.less'

const steps = [Steg1, Steg2]

export const Bestillingsveileder = () => {
	const [step, setStep] = useState(0)

	const isLastStep = () => step === steps.length - 1
	const handleBack = () => step !== 0 && setStep(step - 1)
	const handleNext = () => setStep(step + 1)

	const handleSubmit = (values, formikBag) => {
		const { setSubmitting } = formikBag

		if (!isLastStep()) {
			setSubmitting(false)
			handleNext()
			return
		}

		// TODO - handle final submit
		setSubmitting(false)
	}

	const CurrentStep = steps[step]
	const { validationSchema } = CurrentStep

	const initialValues = steps.reduce((acc, curr) => Object.assign({}, acc, curr.initialValues), {})

	const labels = steps.map(v => ({ label: v.label }))

	return (
		<Formik
			initialValues={initialValues}
			validationSchema={validationSchema}
			onSubmit={handleSubmit}
		>
			{formikBag => (
				<div className="bestillingsveileder">
					<Stegindikator aktivtSteg={step} steg={labels} visLabel kompakt />

					<CurrentStep formikBag={formikBag} />

					<Navigation
						showPrevious={step > 0}
						onPrevious={handleBack}
						isLastStep={isLastStep()}
						formikBag={formikBag}
					/>

					<DisplayFormikState {...formikBag} />
				</div>
			)}
		</Formik>
	)
}
