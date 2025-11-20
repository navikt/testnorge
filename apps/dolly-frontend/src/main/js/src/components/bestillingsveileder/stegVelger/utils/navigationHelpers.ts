import { UseFormReturn } from 'react-hook-form'
import { ShowErrorContextType } from '@/components/bestillingsveileder/ShowErrorContext'
import * as _ from 'lodash-es'

interface ValidationHelpers {
	formMethods: UseFormReturn<any>
	errorContext: ShowErrorContextType
	validationPaths: string[]
}

export const validateAndNavigate = async (
	{ formMethods, errorContext, validationPaths }: ValidationHelpers,
	step: number,
	setStep: (step: number) => void,
) => {
	await formMethods.trigger(validationPaths)

	const errors = formMethods.formState.errors
	const errorFelter = Object.keys(errors)
	const kunEnvironmentError = errorFelter.length === 1 && errorFelter[0] === 'environments'
	const kunEnhetstypeError = errorFelter.length === 1 && _.has(errors, 'organisasjon.enhetstype')
	const kunEnvironmentOgEnhetstypeError =
		errorFelter.length === 2 &&
		errorFelter.includes('environments') &&
		_.has(errors, 'organisasjon.enhetstype')
	const hasSteg0Error =
		_.has(errors, 'gruppeId') ||
		_.has(errors, 'antall') ||
		_.has(errors, 'pdldata.opprettNyPerson.identtype')

	if (hasSteg0Error) {
		errorContext?.setShowError(true)
		return false
	}

	if (
		errorFelter.length > 0 &&
		!kunEnvironmentError &&
		!kunEnhetstypeError &&
		!kunEnvironmentOgEnhetstypeError
	) {
		console.warn('Feil i form, stopper navigering videre')
		console.error(formMethods.formState.errors)
		errorContext?.setShowError(true)
		return false
	}

	setStep(step + 1)
	return true
}

export const executeMutateAndValidate = async (
	helpers: ValidationHelpers,
	step: number,
	setStep: (step: number) => void,
	formMutate: any,
	setMutateLoading: (loading: boolean) => void,
	manualMutateFields: string[],
) => {
	helpers.formMethods.clearErrors(manualMutateFields)
	helpers.errorContext?.setShowError(true)
	setMutateLoading(true)

	try {
		const response = await formMutate()
		setMutateLoading(false)

		if (response?.status === 'INVALID') {
			return
		}

		await validateAndNavigate(helpers, step, setStep)
	} catch (error) {
		setMutateLoading(false)
		await validateAndNavigate(helpers, step, setStep)
	}
}
