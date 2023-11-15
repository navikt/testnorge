import React, { useContext, useState } from 'react'
import { Navigation } from './Navigation/Navigation'
import { stateModifierFns } from '../stateModifier'
import { validate } from '@/utils/YupValidations'
import { BestillingsveilederHeader } from '../BestillingsveilederHeader'

import { Steg1 } from './steg/steg1/Steg1'
import { Steg2 } from './steg/steg2/Steg2'
import { Steg3 } from './steg/steg3/Steg3'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import DisplayFormikState from '@/utils/DisplayFormikState'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	REGEX_BACKEND_ORGANISASJONER,
	useMatchMutate,
} from '@/utils/hooks/useMutate'
import { Stepper } from '@navikt/ds-react'
import { FormProvider, useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import { Validations } from '@/components/bestilling/validation/Validations'

const STEPS = [Steg1, Steg2, Steg3]

export const StegVelger = ({ initialValues, onSubmit }) => {
	const [step, setStep] = useState(0)
	const methods = useForm({ defaultValues: initialValues, resolver: yupResolver(Validations) })

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

	const _handleSubmit = (values) => {
		if (!isLastStep()) {
			handleNext()
			return
		}

		sessionStorage.clear()
		methods.reset()

		onSubmit(values)
		mutate(REGEX_BACKEND_GRUPPER)
		mutate(REGEX_BACKEND_ORGANISASJONER)
		return mutate(REGEX_BACKEND_BESTILLINGER)
	}

	const CurrentStepComponent = STEPS[step]

	//TODO: Sjekke om denne trengs eller om den kan fjernes
	const _validate = (values) =>
		validate(
			{
				...values,
				personFoerLeggTil: personFoerLeggTil,
				tidligereBestillinger: tidligereBestillinger,
				leggTilPaaGruppe: leggTilPaaGruppe,
			},
			CurrentStepComponent.validation,
		)

	const labels = STEPS.map((v) => ({ label: v.label }))

	const stateModifier = stateModifierFns(methods, opts)
	const devEnabled =
		window.location.hostname.includes('localhost') ||
		window.location.hostname.includes('dolly-frontend-dev')

	return (
		<FormProvider {...methods}>
			<form onSubmit={methods.handleSubmit(_handleSubmit)}>
				<Stepper orientation="horizontal" activeStep={step + 1}>
					{labels.map((label, index) => (
						<Stepper.Step key={index}>{label.label}</Stepper.Step>
					))}
				</Stepper>

				<BestillingsveilederHeader />

				<CurrentStepComponent stateModifier={stateModifier} />

				{devEnabled && <DisplayFormikState />}

				<Navigation step={step} onPrevious={handleBack} isLastStep={isLastStep()} />
			</form>
		</FormProvider>
	)
}
