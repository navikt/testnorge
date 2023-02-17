import React, { Fragment, useContext, useState } from 'react'
import { Formik } from 'formik'
import { Navigation } from './Navigation/Navigation'
import { stateModifierFns } from '../stateModifier'
import { validate } from '@/utils/YupValidations'
import { BestillingsveilederHeader } from '../BestillingsveilederHeader'

import { Steg1 } from './steg/steg1/Steg1'
import { Steg2 } from './steg/steg2/Steg2'
import { Steg3 } from './steg/steg3/Steg3'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/Bestillingsveileder'
import DisplayFormikState from '@/utils/DisplayFormikState'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	REGEX_BACKEND_ORGANISASJONER,
	useMatchMutate,
} from '@/utils/hooks/useMutate'
import { Stepper } from '@navikt/ds-react'

const STEPS = [Steg1, Steg2, Steg3]

export const StegVelger = ({ initialValues, onSubmit }) => {
	const [step, setStep] = useState(0)

	const opts = useContext(BestillingsveilederContext)
	const mutate = useMatchMutate()
	const { personFoerLeggTil, tidligereBestillinger, leggTilPaaGruppe } = opts

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

		onSubmit(values)
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
				leggTilPaaGruppe: leggTilPaaGruppe,
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
						<Stepper orientation="horizontal" activeStep={step + 1}>
							{labels.map((label, index) => (
								<Stepper.Step key={index}>{label.label}</Stepper.Step>
							))}
						</Stepper>

						<BestillingsveilederHeader />

						<CurrentStepComponent formikBag={formikBag} stateModifier={stateModifier} />

						{devEnabled && <DisplayFormikState {...formikBag} />}

						<Navigation
							step={step}
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
