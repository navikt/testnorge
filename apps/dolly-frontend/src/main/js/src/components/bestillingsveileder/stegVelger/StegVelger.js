import React, { Fragment, useContext, useState } from 'react'
import { Formik } from 'formik'
import { Navigation } from './Navigation/Navigation'
import { stateModifierFns } from '../stateModifier'
import { validate } from '~/utils/YupValidations'
import { BestillingsveilederHeader } from '../BestillingsveilederHeader'

import { Steg1 } from './steg/steg1/Steg1'
import { Steg2 } from './steg/steg2/Steg2'
import { Steg3 } from './steg/steg3/Steg3'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import DisplayFormikState from '~/utils/DisplayFormikState'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	REGEX_BACKEND_ORGANISASJONER,
	useMatchMutate,
} from '~/utils/hooks/useMutate'
import { StepIndicator } from '@navikt/ds-react'

const STEPS = [Steg1, Steg2, Steg3]

export const StegVelger = ({ initialValues, onSubmit, brukertype, brukerId, children }) => {
	const [step, setStep] = useState(0)

	const opts = useContext(BestillingsveilederContext)
	const mutate = useMatchMutate()
	const { personFoerLeggTil, tidligereBestillinger } = opts

	const isLastStep = () => step === STEPS.length - 1
	const handleNext = () => setStep(step + 1)

	const handleBack = (formikBag) => {
		if (isLastStep()) {
			const { setSubmitting } = formikBag
			setSubmitting(false)
		}
		if (step !== 0) setStep(step - 1)
	}

	const _handleSubmit = (values, formikBag) => {
		const { setSubmitting } = formikBag

		if (!isLastStep()) {
			setSubmitting(false)
			handleNext()
			return
		}

		sessionStorage.clear()

		onSubmit(values, formikBag)
		mutate(REGEX_BACKEND_GRUPPER)
		mutate(REGEX_BACKEND_ORGANISASJONER)
		return mutate(REGEX_BACKEND_BESTILLINGER)
	}

	const CurrentStepComponent = STEPS[step]

	const _validate = (values) =>
		validate(
			{
				...values,
				personFoerLeggTil: personFoerLeggTil,
				tidligereBestillinger: tidligereBestillinger,
			},
			CurrentStepComponent.validation
		)

	const labels = STEPS.map((v) => ({ label: v.label }))

	return (
		<Formik initialValues={initialValues} validate={_validate} onSubmit={_handleSubmit}>
			{(formikBag) => {
				const stateModifier = stateModifierFns(formikBag.values, formikBag.setValues, opts)
				const devEnabled =
					window.location.hostname.includes('localhost') ||
					window.location.hostname.includes('dolly-frontend-dev')

				return (
					<Fragment>
						<StepIndicator activeStep={step}>{labels}</StepIndicator>

						<BestillingsveilederHeader />

						<CurrentStepComponent
							formikBag={formikBag}
							stateModifier={stateModifier}
							brukertype={brukertype}
							brukerId={brukerId}
						/>

						{devEnabled && <DisplayFormikState {...formikBag} />}

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
