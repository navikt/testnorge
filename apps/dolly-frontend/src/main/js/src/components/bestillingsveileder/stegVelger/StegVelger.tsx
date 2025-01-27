import React, { Suspense, useContext, useState } from 'react'
import { Navigation } from './Navigation/Navigation'
import { useStateModifierFns } from '../stateModifier'
import { BestillingsveilederHeader } from '../BestillingsveilederHeader'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	REGEX_BACKEND_ORGANISASJONER,
	useMatchMutate,
} from '@/utils/hooks/useMutate'
import { Stepper } from '@navikt/ds-react'
import { FormProvider, useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'
import { SwrMutateContext } from '@/components/bestillingsveileder/SwrMutateContext'
import Loading from '@/components/ui/loading/Loading'
import { DollyValidation } from '@/components/bestillingsveileder/stegVelger/steg/steg2/DollyValidation'
import { lazyWithPreload } from '@/utils/lazyWithPreload'

const Steg1 = lazyWithPreload(() => import('./steg/steg1/Steg1'))
const Steg2 = lazyWithPreload(() => import('./steg/steg2/Steg2'))
const Steg3 = lazyWithPreload(() => import('./steg/steg3/Steg3'))

Steg1.label = 'Velg egenskaper'
Steg2.label = 'Velg verdier'
Steg3.label = 'Oppsummering'

const DisplayFormState = lazyWithPreload(() => import('@/utils/DisplayFormState'))
const DisplayFormErrors = lazyWithPreload(() => import('@/utils/DisplayFormErrors'))

const STEPS = [Steg1, Steg2, Steg3]
const manualMutateFields = ['manual.sykemelding.detaljertSykemelding']

export const devEnabled =
	window.location.hostname.includes('localhost') ||
	window.location.hostname.includes('dolly-frontend-dev')

export const StegVelger = ({ initialValues, onSubmit }) => {
	const context: any = useContext(BestillingsveilederContext)
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)

	const [formMutate, setFormMutate] = useState(() => null as any)
	const [mutateLoading, setMutateLoading] = useState(false)
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

	const validationPaths = Object.keys(DollyValidation.fields)

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
			setMutateLoading(true)
			formMutate?.().then((response) => {
				setMutateLoading(false)
				if (response.status === 'INVALID') {
					return
				}
				validateForm()
			})
			// 	TODO: Maa sette mutateLoading til false om validate feiler
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
	console.log('mutateLoading: ', mutateLoading) //TODO - SLETT MEG
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
				<Suspense fallback={<Loading label="Laster komponenter" />}>
					<CurrentStepComponent stateModifier={stateModifier} loadingBestilling={loading} />
				</Suspense>
				{devEnabled && (
					<Suspense fallback={<Loading label="Laster komponenter" />}>
						<DisplayFormState />
						<DisplayFormErrors errors={formMethods.formState.errors} label={'Vis errors'} />
					</Suspense>
				)}
				{!loading && (
					<Navigation
						step={step}
						mutateLoading={mutateLoading}
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
