import React, { useContext, useState } from 'react'
import { Navigation } from './Navigation/Navigation'
import { useStateModifierFns } from '../stateModifier'
import { BestillingsveilederHeader } from '../BestillingsveilederHeader'

import { Steg1 } from './steg/steg1/Steg1'
import { Steg2 } from './steg/steg2/Steg2'
import { Steg3 } from './steg/steg3/Steg3'
import DisplayFormState from '@/utils/DisplayFormState'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	REGEX_BACKEND_ORGANISASJONER,
	useMatchMutate,
} from '@/utils/hooks/useMutate'
import { Stepper } from '@navikt/ds-react'
import { FormProvider, useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import DisplayFormErrors from '@/utils/DisplayFormErrors'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'
import { DollyValidation } from './steg/steg2/DollyValidation'
import { SwrMutateContext } from '@/components/bestillingsveileder/SwrMutateContext'

const STEPS = [Steg1, Steg2, Steg3]
const manualMutateFields = ['manual.sykemelding.detaljertSykemelding']

export const devEnabled =
	window.location.hostname.includes('localhost') ||
	window.location.hostname.includes('dolly-frontend-dev')

export const StegVelger = ({ initialValues, onSubmit }) => {
	const context: any = useContext(BestillingsveilederContext)
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)

	const [formMutate, setFormMutate] = useState(() => null)
	const [loading, setLoading] = useState(false)
	const [step, setStep] = useState(0)

	const CurrentStepComponent: any = STEPS[step]
	const stepMaxIndex = STEPS.length - 1
	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: initialValues,
		resolver: yupResolver(DollyValidation),
		context: context,
	})
	const stateModifier = useStateModifierFns(formMethods, setFormMutate)

	const matchMutate = useMatchMutate()

	const validationPaths = Object.keys(DollyValidation?.fields)

	const isLastStep = () => step === STEPS.length - 1

	const validateForm = () => {
		formMethods.trigger(validationPaths).then(() => {
			const errorFelter = Object.keys(formMethods.formState.errors)
			const kunEnvironmentError = errorFelter.length === 1 && errorFelter[0] === 'environments'
			const kunGruppeIdError = errorFelter.length === 1 && errorFelter[0] === 'gruppeId'
			if (errorFelter.length > 0 && step === 1 && !kunEnvironmentError && !kunGruppeIdError) {
				console.warn('Feil i form, stopper navigering videre')
				console.error(formMethods.formState.errors)
				errorContext?.setShowError(true)
				return
			}
			setStep(step + 1)
		})
	}

	const handleNext = () => {
		if (step === 1 && formMutate) {
			formMethods.clearErrors(manualMutateFields)
			errorContext?.setShowError(true)
			formMutate().then((response) => {
				if (response.status === 'INVALID') {
					return
				}
				validateForm()
			})
		} else {
			validateForm()
		}
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

		setLoading(true)
		sessionStorage.clear()
		errorContext?.setShowError(false)
		formMethods.handleSubmit(onSubmit(values))

		formMethods.reset()
		matchMutate(REGEX_BACKEND_GRUPPER)
		matchMutate(REGEX_BACKEND_ORGANISASJONER)
		matchMutate(REGEX_BACKEND_BESTILLINGER)
	}

	const labels = STEPS.map((v) => ({ label: v.label }))

	return (
		<SwrMutateContext.Provider value={setFormMutate}>
			<FormProvider {...formMethods}>
				<Stepper orientation="horizontal" activeStep={step + 1}>
					{labels.map((label, index) => (
						<Stepper.Step
							key={index}
							completed={index < step}
							onClick={() => index < stepMaxIndex && setStep(index)}
						>
							{label.label}
						</Stepper.Step>
					))}
				</Stepper>
				<BestillingsveilederHeader />
				<CurrentStepComponent stateModifier={stateModifier} loadingBestilling={loading} />
				{devEnabled && (
					<>
						<DisplayFormState />
						<DisplayFormErrors errors={formMethods.formState.errors} label={'Vis errors'} />
					</>
				)}
				{!loading && (
					<Navigation
						step={step}
						onPrevious={handleBack}
						isLastStep={isLastStep()}
						handleSubmit={() => {
							return _handleSubmit(formMethods.getValues())
						}}
					/>
				)}
			</FormProvider>
		</SwrMutateContext.Provider>
	)
}
