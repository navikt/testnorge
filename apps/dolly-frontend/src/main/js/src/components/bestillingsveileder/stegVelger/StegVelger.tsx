import React, { lazy, Suspense, useContext, useState } from 'react'
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
import { FormProvider, useForm, useWatch } from 'react-hook-form'
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
import { useMalFormSync } from './hooks/useMalFormSync'
import { useId2032Sync, useIdenttypeSync } from './hooks/useFormFieldSync'
import { executeMutateAndValidate, validateAndNavigate } from './utils/navigationHelpers'
import StepErrorBoundary from './StepErrorBoundary'

interface StepDef {
	component: React.ComponentType<any>
	label: string
}
const STEPS: StepDef[] = [
	{ component: Steg0, label: 'Velg gruppe/mal' },
	{ component: Steg1, label: 'Velg egenskaper' },
	{ component: Steg2, label: 'Velg verdier' },
	{ component: Steg3, label: 'Oppsummering' },
]

const DisplayFormState = lazy(() => import('@/utils/DisplayFormState'))
const DisplayFormErrors = lazyWithPreload(() => import('@/utils/DisplayFormErrors'))

const manualMutateFields = ['manual.sykemelding.detaljertSykemelding']

export const devEnabled =
	window.location.hostname.includes('localhost') ||
	window.location.hostname.includes('dolly-frontend-dev')

export const StegVelger = ({
	initialValues,
	onSubmit,
}: {
	initialValues: any
	onSubmit: (values: any) => Promise<void> | void
}) => {
	'use no memo'

	const context = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)
	const is = context.is || {}
	const erOrganisasjon: boolean = !!(
		is.nyOrganisasjon ||
		is.nyStandardOrganisasjon ||
		is.nyOrganisasjonFraMal
	)
	const validationResolver: any = erOrganisasjon
		? DollyOrganisasjonValidation
		: DollyIdentValidation

	const [formMutate, setFormMutate] = useState(() => null as any)
	const [mutateLoading, setMutateLoading] = useState(false)
	const [loading, setLoading] = useState(false)
	const [step, setStep] = useState(0)

	const CurrentStepComponent = STEPS[step].component
	const stepMaxIndex = STEPS.length - 1
	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: initialValues,
		resolver: yupResolver(validationResolver),
		context: context,
	})
	const stateModifier = useStateModifierFns(formMethods, setFormMutate, context)

	const matchMutate = useMatchMutate()

	const validationPaths = Object.keys(validationResolver.fields)

	const isLastStep = () => step === STEPS.length - 1

	const validationHelpers = { formMethods, errorContext, validationPaths }

	const handleNext = async () => {
		const isSteg0 = STEPS[step].component === Steg0
		const isSteg1 = STEPS[step].component === Steg1
		const isSteg2 = STEPS[step].component === Steg2

		if (isSteg0) {
			const validGruppe = await formMethods.trigger(['gruppeId'])
			if (!validGruppe) {
				errorContext?.setShowError(true)
				return
			}
			errorContext?.setShowError(false)
			setStep(step + 1)
			return
		}

		if (isSteg1) {
			errorContext?.setShowError(false)
			setStep(step + 1)
			return
		}

		if (isSteg2 && formMutate) {
			await executeMutateAndValidate(
				validationHelpers,
				step,
				setStep,
				formMutate,
				setMutateLoading,
				manualMutateFields,
			)
		} else {
			await validateAndNavigate(validationHelpers, step, setStep)
		}
	}

	const handleBack = () => {
		errorContext?.setShowError(false)
		if (step !== 0) setStep(step - 1)
	}

	const _handleSubmit = async (values: any) => {
		if (!isLastStep()) {
			handleNext()
			return
		}

		setLoading(true)
		sessionStorage.clear()
		errorContext?.setShowError(false)
		await onSubmit(values)

		formMethods.reset()
		matchMutate(REGEX_BACKEND_GRUPPER)
		matchMutate(REGEX_BACKEND_ORGANISASJONER)
		matchMutate(REGEX_BACKEND_BESTILLINGER)
	}

	const labels = STEPS.map((v) => ({ label: v.label }))

	useMalFormSync({ context, formMethods, is })
	useIdenttypeSync({ context, formMethods, erOrganisasjon: Boolean(erOrganisasjon), is })
	useId2032Sync({ context, formMethods, erOrganisasjon: Boolean(erOrganisasjon), is })

	const malWatch = useWatch({ control: formMethods.control, name: 'mal' })
	const identtypeWatch = useWatch({
		control: formMethods.control,
		name: 'pdldata.opprettNyPerson.identtype',
	})
	const id2032Watch = useWatch({
		control: formMethods.control,
		name: 'pdldata.opprettNyPerson.id2032',
	})

	return (
		<SwrMutateContext.Provider value={setFormMutate}>
			<FormProvider {...formMethods}>
				<Stepper orientation="horizontal" activeStep={step + 1}>
					{labels.map((label, index) => (
						<Stepper.Step
							key={index}
							completed={index < step}
							onClick={() => index < stepMaxIndex && setStep(index)}
							style={{ whiteSpace: 'nowrap' }}
						>
							{label.label}
						</Stepper.Step>
					))}
				</Stepper>
				<BestillingsveilederHeader context={context} formMethods={formMethods} />
				<div style={{ display: 'none' }} data-testid="stegevelger-form-snapshot">
					mal:{malWatch}|identtype:{identtypeWatch}|id2032:{String(id2032Watch)}|sivilstand:
					{JSON.stringify(formMethods.getValues('pdldata.person.sivilstand'))}
				</div>
				<Suspense fallback={<Loading label="Laster komponenter" />}>
					<StepErrorBoundary stepIndex={step} stepLabel={labels[step].label}>
						<CurrentStepComponent stateModifier={stateModifier} loadingBestilling={loading} />
					</StepErrorBoundary>
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
						handleSubmit={() => _handleSubmit(formMethods.getValues())}
					/>
				)}
			</FormProvider>
		</SwrMutateContext.Provider>
	)
}
