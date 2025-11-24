import { useEffect, useRef } from 'react'
import { UseFormReturn } from 'react-hook-form'
import { useOrganisasjonForvalter } from '@/utils/hooks/useDollyOrganisasjoner'
import { handleManualOrgChange } from '@/utils/OrgUtils'

type UseOrganisasjonValidationParams = {
	formMethods: UseFormReturn
	organisasjonPath: string
	watchedOrgnr: string
	useValidation: boolean
}

export const useOrganisasjonValidation = ({
	formMethods,
	organisasjonPath,
	watchedOrgnr,
	useValidation,
}: UseOrganisasjonValidationParams) => {
	const { organisasjoner, loading, error, hasBeenCalled } = useOrganisasjonForvalter(
		useValidation ? [watchedOrgnr] : [],
	)
	const organisasjon = organisasjoner?.[0]?.q1 || organisasjoner?.[0]?.q2

	const previousStateRef = useRef<{
		orgnr?: string
		hasError: boolean
		hasOrganisation: boolean
	}>({
		hasError: false,
		hasOrganisation: false,
	})

	useEffect(() => {
		if (!useValidation) return
		if (loading) return

		const isValidLength = watchedOrgnr?.length === 9
		const hasInvalidLength = watchedOrgnr && watchedOrgnr.length > 0 && !isValidLength
		const currentHasError = !organisasjon && watchedOrgnr && isValidLength && hasBeenCalled
		const currentHasOrganisation = !!organisasjon && isValidLength
		const prevState = previousStateRef.current

		queueMicrotask(() => {
			if (
				currentHasOrganisation &&
				(!prevState.hasOrganisation || prevState.orgnr !== watchedOrgnr)
			) {
				formMethods.clearErrors(`manual.${organisasjonPath}`)
				handleManualOrgChange(watchedOrgnr, formMethods, organisasjonPath, null, organisasjon)
				previousStateRef.current = {
					orgnr: watchedOrgnr,
					hasError: false,
					hasOrganisation: true,
				}
			} else if (currentHasError && (!prevState.hasError || prevState.orgnr !== watchedOrgnr)) {
				formMethods.setError(`manual.${organisasjonPath}`, {
					message: 'Fant ikke organisasjonen',
				})
				previousStateRef.current = {
					orgnr: watchedOrgnr,
					hasError: true,
					hasOrganisation: false,
				}
			} else if (hasInvalidLength && (!prevState.hasError || prevState.orgnr !== watchedOrgnr)) {
				formMethods.setError(`manual.${organisasjonPath}`, {
					message: 'Organisasjonsnummer må være 9 siffer',
				})
				previousStateRef.current = {
					orgnr: watchedOrgnr,
					hasError: true,
					hasOrganisation: false,
				}
			} else if (isValidLength && prevState.hasError && prevState.orgnr !== watchedOrgnr) {
				formMethods.clearErrors(`manual.${organisasjonPath}`)
				previousStateRef.current = {
					orgnr: watchedOrgnr,
					hasError: false,
					hasOrganisation: false,
				}
			} else if (!watchedOrgnr && prevState.hasError) {
				formMethods.clearErrors(`manual.${organisasjonPath}`)
				previousStateRef.current = {
					orgnr: watchedOrgnr,
					hasError: false,
					hasOrganisation: false,
				}
			}
		})
	}, [
		organisasjon,
		loading,
		useValidation,
		watchedOrgnr,
		organisasjonPath,
		hasBeenCalled,
		formMethods,
	])

	return { organisasjoner, loading, error }
}
