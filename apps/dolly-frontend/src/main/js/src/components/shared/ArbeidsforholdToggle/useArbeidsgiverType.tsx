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

	const getArbeidsgiverType = () => {
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

	useEffect(() => {
		if (!fasteOrganisasjoner || !egneOrganisasjoner) return

		if (useFormState && !arbeidsgiverTypeFromForm) {
			const newType = getArbeidsgiverType()
			formMethods.setValue(`${path}.arbeidsgiverType`, newType, {
				shouldDirty: false,
			})
		}
	}, [
		fasteOrganisasjoner,
		egneOrganisasjoner,
		arbeidsgiverTypeFromForm,
		useFormState,
		path,
		formMethods,
	])

	useEffect(() => {
		if (!fasteOrganisasjoner || !egneOrganisasjoner) return
		if (useFormState) return

		const newType = getArbeidsgiverType()
		setLocalArbeidsgiverType(newType)
	}, [watchedOrgnr, watchedPers, fasteOrganisasjoner, egneOrganisasjoner, useFormState])

	const typeArbeidsgiver = useFormState ? arbeidsgiverTypeFromForm : localArbeidsgiverType

	return {
		typeArbeidsgiver: typeArbeidsgiver ?? ArbeidsgiverTyper.felles,
		setLocalArbeidsgiverType,
	}
}
