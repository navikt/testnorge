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
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'
import { SwrMutateContext } from '@/components/bestillingsveileder/SwrMutateContext'
import Loading from '@/components/ui/loading/Loading'
import {
	DollyIdentValidation,
	DollyOrganisasjonValidation,
} from '@/components/bestillingsveileder/stegVelger/steg/steg2/DollyIdentValidation'
import { lazyWithPreload } from '@/utils/lazyWithPreload'
import Steg0 from './steg/steg0/Steg0'
import Steg1 from './steg/steg1/Steg1'
import Steg2 from './steg/steg2/Steg2'
import Steg3 from './steg/steg3/Steg3'
import { erDollyAdmin } from '@/utils/DollyAdmin'
import * as _ from 'lodash-es'

Steg0.label = 'Velg gruppe/mal'
Steg1.label = 'Velg egenskaper'
Steg2.label = 'Velg verdier'
Steg3.label = 'Oppsummering'

const DisplayFormState = lazyWithPreload(() => import('@/utils/DisplayFormState'))
const DisplayFormErrors = lazyWithPreload(() => import('@/utils/DisplayFormErrors'))

let STEPS = [Steg0, Steg1, Steg2, Steg3]
const manualMutateFields = ['manual.sykemelding.detaljertSykemelding']

export const devEnabled =
	window.location.hostname.includes('localhost') ||
	window.location.hostname.includes('dolly-frontend-dev')

export const StegVelger = ({ initialValues, onSubmit }) => {
	const context = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)
	const erOrganisasjon =
		context.is?.nyOrganisasjon ||
		context.is?.nyStandardOrganisasjon ||
		context.is?.nyOrganisasjonFraMal
	const validationResolver: any = erOrganisasjon
		? DollyOrganisasjonValidation
		: DollyIdentValidation

	const [formMutate, setFormMutate] = useState(() => null as any)
	const [mutateLoading, setMutateLoading] = useState(false)
	const [loading, setLoading] = useState(false)
	const [step, setStep] = useState(0)

	const CurrentStepComponent: any = STEPS[step]
	const stepMaxIndex = STEPS.length - 1
	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: initialValues,
		resolver: yupResolver(validationResolver),
		context: context,
	})
	const stateModifier = useStateModifierFns(formMethods, setFormMutate)

	const matchMutate = useMatchMutate()

	const validationPaths = Object.keys(validationResolver.fields)

	const isLastStep = () => step === STEPS.length - 1

	const validateForm = () => {
		formMethods.trigger(validationPaths).then(() => {
			const errors = formMethods.formState.errors
			const errorFelter = Object.keys(errors)
			const kunEnvironmentError = errorFelter.length === 1 && errorFelter[0] === 'environments'
			const hasSteg0Error =
				_.has(errors, 'gruppeId') ||
				_.has(errors, 'antall') ||
				_.has(errors, 'pdldata.opprettNyPerson.identtype')
			if (hasSteg0Error) {
				errorContext?.setShowError(true)
				return
			}
			if (errorFelter.length > 0 && STEPS[step] === Steg2 && !kunEnvironmentError) {
				console.warn('Feil i form, stopper navigering videre')
				console.error(formMethods.formState.errors)
				errorContext?.setShowError(true)
				return
			}
			setStep(step + 1)
		})
	}

	const handleNext = () => {
		if ((STEPS[step] === Steg2 || STEPS[step] === Steg0) && formMutate) {
			formMethods.clearErrors(manualMutateFields)
			errorContext?.setShowError(true)
			setMutateLoading(true)
			formMutate?.()
				.then((response) => {
					setMutateLoading(false)
					if (response.status === 'INVALID') {
						return
					}
					validateForm()
				})
				.catch(() => {
					setMutateLoading(false)
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
				<BestillingsveilederHeader context={context} formMethods={formMethods} />
				<Suspense fallback={<Loading label="Laster komponenter" />}>
					<CurrentStepComponent stateModifier={stateModifier} loadingBestilling={loading} />
				</Suspense>
				{(devEnabled || erDollyAdmin()) && (
					<Suspense>
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
