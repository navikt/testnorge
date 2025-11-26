import { useEffect, useRef, useState } from 'react'
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
	const isManualChange = useRef(false)
	const lastManualType = useRef<ArbeidsgiverTyper | null>(null)

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
		if (isManualChange.current) return

		if (useFormState && !arbeidsgiverTypeFromForm && (watchedOrgnr || watchedPers)) {
			const newType = getArbeidsgiverType()
			if (lastManualType.current !== newType) {
				lastManualType.current = null
			}
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
		watchedOrgnr,
		watchedPers,
	])

	useEffect(() => {
		if (!fasteOrganisasjoner || !egneOrganisasjoner) return
		if (useFormState) return
		if (isManualChange.current) return

		const newType = getArbeidsgiverType()
		if (lastManualType.current !== newType) {
			lastManualType.current = null
		}
		setLocalArbeidsgiverType(newType)
	}, [watchedOrgnr, watchedPers, fasteOrganisasjoner, egneOrganisasjoner, useFormState])

	const typeArbeidsgiver = useFormState ? arbeidsgiverTypeFromForm : localArbeidsgiverType

	useEffect(() => {
		if (isManualChange.current) {
			const currentType = useFormState ? arbeidsgiverTypeFromForm : localArbeidsgiverType
			if (currentType === lastManualType.current) {
				const timer = setTimeout(() => {
					isManualChange.current = false
				}, 100)
				return () => clearTimeout(timer)
			} else {
				isManualChange.current = false
			}
		}
	}, [arbeidsgiverTypeFromForm, localArbeidsgiverType, useFormState])

	const handleManualTypeChange = (type: ArbeidsgiverTyper) => {
		isManualChange.current = true
		lastManualType.current = type
		setLocalArbeidsgiverType(type)
	}

	const markAsManualChange = (newType?: ArbeidsgiverTyper) => {
		isManualChange.current = true
		if (newType) {
			lastManualType.current = newType
		} else {
			const currentType = useFormState ? arbeidsgiverTypeFromForm : localArbeidsgiverType
			if (currentType) {
				lastManualType.current = currentType
			}
		}
	}

	return {
		typeArbeidsgiver: typeArbeidsgiver ?? ArbeidsgiverTyper.felles,
		setLocalArbeidsgiverType: handleManualTypeChange,
		markAsManualChange,
	}
}
