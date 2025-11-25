import { useEffect } from 'react'
import { UseFormReturn } from 'react-hook-form'
import { BestillingsveilederContextType } from '@/components/bestillingsveileder/BestillingsveilederContext'

interface UseFormFieldSyncProps {
	context: BestillingsveilederContextType
	formMethods: UseFormReturn<any>
	erOrganisasjon: boolean
	is: any
}

export const useIdenttypeSync = ({
	context,
	formMethods,
	erOrganisasjon,
	is,
}: UseFormFieldSyncProps) => {
	useEffect(() => {
		if (
			erOrganisasjon ||
			is.leggTil ||
			is.leggTilPaaGruppe ||
			is.importTestnorge ||
			is.opprettFraIdenter
		)
			return

		const currentIdenttype = formMethods.getValues('pdldata.opprettNyPerson.identtype')
		if (context.identtype && context.identtype !== currentIdenttype) {
			formMethods.setValue('pdldata.opprettNyPerson.identtype', context.identtype, {
				shouldValidate: true,
			})
		}
	}, [
		context.identtype,
		erOrganisasjon,
		is.leggTil,
		is.leggTilPaaGruppe,
		is.importTestnorge,
		is.opprettFraIdenter,
	])
}

export const useId2032Sync = ({
	context,
	formMethods,
	erOrganisasjon,
	is,
}: UseFormFieldSyncProps) => {
	useEffect(() => {
		if (
			erOrganisasjon ||
			is.leggTil ||
			is.leggTilPaaGruppe ||
			is.importTestnorge ||
			is.opprettFraIdenter
		)
			return

		const currentId2032 = formMethods.getValues('pdldata.opprettNyPerson.id2032')
		if (typeof context.id2032 === 'boolean' && context.id2032 !== currentId2032) {
			formMethods.setValue('pdldata.opprettNyPerson.id2032', context.id2032, {
				shouldValidate: true,
			})
		}
	}, [
		context.id2032,
		erOrganisasjon,
		is.leggTil,
		is.leggTilPaaGruppe,
		is.importTestnorge,
		is.opprettFraIdenter,
	])
}
