import { useEffect, useRef } from 'react'
import { UseFormReturn } from 'react-hook-form'
import { useOrganisasjonForvalter } from '@/utils/hooks/useDollyOrganisasjoner'
import { handleManualOrgChange } from '@/utils/OrgUtils'

type UseOrganisasjonValidationParams = {
	formMethods: UseFormReturn
	organisasjonPath: string
	watchedOrgnr: string
	useValidation: boolean
	parentPath?: string | null
}

export const useOrganisasjonValidation = ({
	formMethods,
	organisasjonPath,
	watchedOrgnr,
	useValidation,
	parentPath = null,
}: UseOrganisasjonValidationParams) => {
	const { organisasjoner, loading, error, hasBeenCalled } = useOrganisasjonForvalter(
		useValidation ? [watchedOrgnr] : [],
	)
	const firstOrg = organisasjoner?.[0]
	const organisasjon = firstOrg
		? (Object.values(firstOrg).find(
				(val) => val && typeof val === 'object' && 'organisasjonsnummer' in val,
			) as any)
		: undefined
	const organisasjonsnummer = organisasjon?.organisasjonsnummer as string | undefined

	const formMethodsRef = useRef(formMethods)
	formMethodsRef.current = formMethods

	const parentPathRef = useRef(parentPath)
	parentPathRef.current = parentPath

	const previousStateRef = useRef<{
		orgnr?: string
		errorMessage: string | null
		orgApplied: boolean
	}>({
		errorMessage: null,
		orgApplied: false,
	})

	useEffect(() => {
		if (!useValidation) return

		const methods = formMethodsRef.current
		const errorPath = `manual.${organisasjonPath}`
		const isValidLength = watchedOrgnr?.length === 9

		if (loading) {
			if (isValidLength && previousStateRef.current.orgnr !== watchedOrgnr) {
				methods.clearErrors(errorPath)
				previousStateRef.current = { orgnr: watchedOrgnr, errorMessage: null, orgApplied: false }
			}
			return
		}

		let expectedError: string | null = null
		let shouldApplyOrg = false

		if (!watchedOrgnr || watchedOrgnr.length === 0) {
			expectedError = null
		} else if (!isValidLength) {
			expectedError = 'Organisasjonsnummer må være 9 siffer'
		} else if (!hasBeenCalled) {
			return
		} else if (organisasjonsnummer) {
			expectedError = null
			shouldApplyOrg = true
		} else {
			expectedError = 'Fant ikke organisasjonen'
		}

		const prev = previousStateRef.current
		const isUnchanged =
			prev.orgnr === watchedOrgnr &&
			prev.errorMessage === expectedError &&
			(!shouldApplyOrg || prev.orgApplied)

		if (isUnchanged) return

		if (expectedError) {
			methods.setError(errorPath, { message: expectedError })
		} else {
			methods.clearErrors(errorPath)
		}

		if (shouldApplyOrg) {
			handleManualOrgChange(
				watchedOrgnr,
				methods,
				organisasjonPath,
				parentPathRef.current,
				organisasjon,
			)
		}

		previousStateRef.current = {
			orgnr: watchedOrgnr,
			errorMessage: expectedError,
			orgApplied: shouldApplyOrg,
		}
	}, [organisasjonsnummer, loading, useValidation, watchedOrgnr, organisasjonPath, hasBeenCalled])

	return { organisasjoner, loading, error }
}
