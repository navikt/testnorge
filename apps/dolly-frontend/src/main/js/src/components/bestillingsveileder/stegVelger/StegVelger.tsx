import React, { useContext, useState } from 'react'
import { Navigation } from './Navigation/Navigation'
import { useStateModifierFns } from '../stateModifier'
import { BestillingsveilederHeader } from '../BestillingsveilederHeader'

import { Steg1 } from './steg/steg1/Steg1'
import { Steg2 } from './steg/steg2/Steg2'
import { Steg3 } from './steg/steg3/Steg3'
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
import DisplayFormikErrors from '@/utils/DisplayFormikErrors'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'
import { DollyValidation } from './steg/steg2/DollyValidation'

const STEPS = [Steg1, Steg2, Steg3]

export const StegVelger = ({ initialValues, onSubmit }) => {
	const context = useContext(BestillingsveilederContext)
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)
	const [step, setStep] = useState(0)
	const CurrentStepComponent: any = STEPS[step]
	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: initialValues,
		resolver: yupResolver(DollyValidation),
		context: context,
	})
	const stateModifier = useStateModifierFns(formMethods)

	const mutate = useMatchMutate()

	const isLastStep = () => step === STEPS.length - 1
	const handleNext = () => {
		formMethods.trigger().then((valid) => {
			const errorFelter = Object.keys(formMethods.formState.errors)
			const kunEnvironmentError = errorFelter.length === 1 && errorFelter[0] === 'environments'
			const kunGruppeIdError = errorFelter.length === 1 && errorFelter[0] === 'gruppeId'
			if (!valid && step === 1 && !kunEnvironmentError && !kunGruppeIdError) {
				console.warn('Feil i form, stopper navigering videre')
				console.error(formMethods.formState.errors)
				errorContext?.setShowError(true)
				return
			}
			setStep(step + 1)
		})
	}

	const handleBack = () => {
		errorContext?.setShowError(false)
		if (step !== 0) setStep(step - 1)
	}

	const _handleSubmit = (values) => {
		if (!isLastStep()) {
			handleNext()
			return
		}

		sessionStorage.clear()
		errorContext?.setShowError(false)
		formMethods.reset()

		formMethods.handleSubmit(onSubmit(values))
		mutate(REGEX_BACKEND_GRUPPER)
		mutate(REGEX_BACKEND_ORGANISASJONER)
		return mutate(REGEX_BACKEND_BESTILLINGER)
	}

	const labels = STEPS.map((v) => ({ label: v.label }))

	const devEnabled =
		window.location.hostname.includes('localhost') ||
		window.location.hostname.includes('dolly-frontend-dev')

	return (
		<FormProvider {...formMethods}>
			<Stepper orientation="horizontal" activeStep={step + 1}>
				{labels.map((label, index) => (
					<Stepper.Step key={index}>{label.label}</Stepper.Step>
				))}
			</Stepper>

			<BestillingsveilederHeader />

			<CurrentStepComponent stateModifier={stateModifier} />

			{devEnabled && (
				<>
					<DisplayFormikState />
					<DisplayFormikErrors errors={formMethods.formState.errors} label={'Vis errors'} />
				</>
			)}

			<Navigation
				step={step}
				onPrevious={handleBack}
				isLastStep={isLastStep()}
				handleSubmit={() => {
					formMethods.trigger().catch((error) => {
						console.warn(error)
					})
					return _handleSubmit(formMethods.getValues())
				}}
			/>
		</FormProvider>
	)
}
