import { useEffect, useState } from 'react'
import { UseFormReturn } from 'react-hook-form'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'

type UseArbeidsgiverTypeParams = {
	formMethods: UseFormReturn
	path: string
	watchedOrgnr: string
	watchedPers: string
	fasteOrganisasjoner: any[]
	egneOrganisasjoner: any[]
	useFormState: boolean
	arbeidsgiverTypeFromForm?: ArbeidsgiverTyper
}

export const useArbeidsgiverType = ({
	formMethods,
	path,
	watchedOrgnr,
	watchedPers,
	fasteOrganisasjoner,
	egneOrganisasjoner,
	useFormState,
	arbeidsgiverTypeFromForm,
}: UseArbeidsgiverTypeParams) => {
	const [localArbeidsgiverType, setLocalArbeidsgiverType] = useState<ArbeidsgiverTyper>(
		ArbeidsgiverTyper.felles,
	)
	const [initialized, setInitialized] = useState(false)

	useEffect(() => {
		if (!fasteOrganisasjoner || !egneOrganisasjoner) return
		if (initialized) return

		const getInitialType = () => {
			if (watchedPers) {
				return ArbeidsgiverTyper.privat
			} else if (
				!watchedOrgnr ||
				fasteOrganisasjoner
					?.map((organisasjon: any) => organisasjon?.orgnummer)
					?.some((org: string) => org === watchedOrgnr)
			) {
				return ArbeidsgiverTyper.felles
			} else if (
				egneOrganisasjoner
					?.map((organisasjon: any) => organisasjon?.orgnr)
					?.some((org: string) => org === watchedOrgnr)
			) {
				return ArbeidsgiverTyper.egen
			} else {
				return ArbeidsgiverTyper.fritekst
			}
		}

		if (useFormState && !arbeidsgiverTypeFromForm) {
			const newType = getInitialType()
			formMethods.setValue(`${path}.arbeidsgiverType`, newType, {
				shouldDirty: false,
			})
			setInitialized(true)
		} else if (!useFormState) {
			const newType = getInitialType()
			setLocalArbeidsgiverType(newType)
			setInitialized(true)
		}
	}, [
		fasteOrganisasjoner,
		egneOrganisasjoner,
		arbeidsgiverTypeFromForm,
		useFormState,
		initialized,
		watchedOrgnr,
		watchedPers,
		path,
		formMethods,
	])

	return {
		typeArbeidsgiver: useFormState ? arbeidsgiverTypeFromForm : localArbeidsgiverType,
		setLocalArbeidsgiverType,
	}
}
