import React, { Suspense, useContext, useEffect, useState } from 'react'
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
import * as _ from 'lodash-es'
import { initialValuesBasedOnMal } from '@/components/bestillingsveileder/options/malOptions'

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

const DisplayFormState = lazyWithPreload(() => import('@/utils/DisplayFormState'))
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
	onSubmit: (values: any) => (vals: any) => void
}) => {
	'use no memo' // Skip compilation for this component
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

	const CurrentStepComponent = STEPS[step].component
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
		if ((STEPS[step].component === Steg2 || STEPS[step].component === Steg0) && formMutate) {
			formMethods.clearErrors(manualMutateFields)
			errorContext?.setShowError(true)
			setMutateLoading(true)
			formMutate?.()
				.then((response: any) => {
					setMutateLoading(false)
					if (response?.status === 'INVALID') {
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

	const _handleSubmit = (values: any) => {
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

	const prevMalIdRef = React.useRef<string | undefined>(undefined)

	useEffect(() => {
		const currentMalId = context.mal?.id
		if (!currentMalId) {
			prevMalIdRef.current = undefined
			return
		}
		if (prevMalIdRef.current === currentMalId) return
		prevMalIdRef.current = currentMalId
		try {
			const environments = formMethods.getValues('environments')
			const malValues = initialValuesBasedOnMal(context.mal, environments)
			Object.entries(malValues).forEach(([key, value]) => {
				formMethods.setValue(key as any, value as any, { shouldValidate: false, shouldDirty: true })
			})
			formMethods.setValue('mal', currentMalId, { shouldValidate: false })
			const identtypeFraMal = _.get(malValues, 'pdldata.opprettNyPerson.identtype')
			if (identtypeFraMal) {
				formMethods.setValue('pdldata.opprettNyPerson.identtype', identtypeFraMal, {
					shouldValidate: true,
				})
			}
			const id2032FraMal = _.get(malValues, 'pdldata.opprettNyPerson.id2032')
			if (typeof id2032FraMal === 'boolean') {
				formMethods.setValue('pdldata.opprettNyPerson.id2032', id2032FraMal, {
					shouldValidate: true,
				})
			}
		} catch (e) {
			console.warn('Klarte ikke å sette mal-verdier i form', e)
		}
	}, [context.mal])

	useEffect(() => {
		const currentId2032 = formMethods.getValues('pdldata.opprettNyPerson.id2032')
		if (typeof context.id2032 === 'boolean' && context.id2032 !== currentId2032) {
			formMethods.setValue('pdldata.opprettNyPerson.id2032', context.id2032, {
				shouldValidate: true,
			})
		}
	}, [context.id2032])

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
