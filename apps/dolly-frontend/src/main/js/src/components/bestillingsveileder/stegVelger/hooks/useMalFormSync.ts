import { useEffect, useRef } from 'react'
import { UseFormReturn } from 'react-hook-form'
import * as _ from 'lodash-es'
import { initialValuesBasedOnMal } from '@/components/bestillingsveileder/options/malOptions'
import { BestillingsveilederContextType } from '@/components/bestillingsveileder/BestillingsveilederContext'

interface UseMalFormSyncProps {
	context: BestillingsveilederContextType
	formMethods: UseFormReturn<any>
	is: any
}

const sanitizePdldata = (pdldata: any) => {
	if (!pdldata || typeof pdldata !== 'object') return undefined
	const { opprettNyPerson, ...rest } = pdldata
	if (Object.keys(rest).length === 0) return undefined
	return rest
}

const parseAntall = (value: any): number => {
	if (typeof value === 'number') return value
	return parseInt(value || '1', 10) || 1
}

const getCommonFormValues = (formMethods: UseFormReturn<any>) => ({
	gruppevalg: formMethods.getValues('gruppevalg'),
	bruker: formMethods.getValues('bruker'),
	malBruker: formMethods.getValues('malBruker'),
	gruppeId: formMethods.getValues('gruppeId') || null,
})

const createCleanedFormData = (formMethods: UseFormReturn<any>) => ({
	antall: 1,
	beskrivelse: null as any,
	pdldata: {
		opprettNyPerson: {
			identtype: 'FNR',
			syntetisk: true,
			id2032: false,
		},
	},
	...getCommonFormValues(formMethods),
})

const createBaselineFormData = (
	formMethods: UseFormReturn<any>,
	isImport: boolean,
	isFraIdenter: boolean,
) => {
	const baseline: any = {
		...getCommonFormValues(formMethods),
		antall: parseAntall(formMethods.getValues('antall')),
	}

	if (isImport) baseline.importPersoner = formMethods.getValues('importPersoner') || []
	if (isFraIdenter) baseline.opprettFraIdenter = formMethods.getValues('opprettFraIdenter') || []

	return baseline
}

const buildMalBasedValues = (
	formMethods: UseFormReturn<any>,
	malValues: any,
	currentMalId: string,
	is: any,
) => {
	const commonValues = getCommonFormValues(formMethods)
	const isNy = is.nyBestilling || is.nyBestillingFraMal
	const isLeggTil = is.leggTil || is.leggTilPaaGruppe

	const baseNextValues = {
		...malValues,
		mal: currentMalId,
		...commonValues,
	}

	if (isNy) {
		return baseNextValues
	}

	const sanitized = sanitizePdldata(malValues.pdldata)

	if (is.importTestnorge) {
		return {
			...baseNextValues,
			importPersoner: formMethods.getValues('importPersoner') || [],
			pdldata: sanitized,
		}
	}

	if (is.opprettFraIdenter) {
		return {
			...baseNextValues,
			opprettFraIdenter: formMethods.getValues('opprettFraIdenter') || [],
			pdldata: sanitized,
		}
	}

	if (isLeggTil) {
		return {
			...baseNextValues,
			pdldata: sanitized,
		}
	}

	return baseNextValues
}

const normalizeFormValues = (
	nextValues: any,
	malValues: any,
	formMethods: UseFormReturn<any>,
	isNy: boolean,
) => {
	if (typeof nextValues.antall !== 'number') {
		const prevAntall = parseAntall(formMethods.getValues('antall'))
		const malAntall = malValues?.antall != null ? parseAntall(malValues.antall) : null
		nextValues.antall = malAntall ?? prevAntall
	}

	if (!isNy && nextValues.pdldata?.opprettNyPerson) {
		delete nextValues.pdldata.opprettNyPerson
		if (Object.keys(nextValues.pdldata).length === 0) {
			delete nextValues.pdldata
		}
	}
}

const updateContextFromMal = (
	context: BestillingsveilederContextType,
	formMethods: UseFormReturn<any>,
	malValues: any,
	isNy: boolean,
) => {
	if (!isNy) return

	const identtypeFraMal = _.get(malValues, 'pdldata.opprettNyPerson.identtype')
	if (identtypeFraMal) {
		context.setIdenttype?.(identtypeFraMal)
		context.updateContext?.({ identtype: identtypeFraMal })
	}

	const id2032FraMal = _.get(malValues, 'pdldata.opprettNyPerson.id2032')
	if (typeof id2032FraMal === 'boolean') {
		formMethods.setValue('pdldata.opprettNyPerson.id2032', id2032FraMal, {
			shouldValidate: true,
		})
		context.updateContext?.({ id2032: id2032FraMal })
	}
}

export const useMalFormSync = ({ context, formMethods, is }: UseMalFormSyncProps) => {
	const prevMalIdRef = useRef<string | undefined>(undefined)

	useEffect(() => {
		const currentMalId = context.mal?.id
		const isNy = is.nyBestilling || is.nyBestillingFraMal
		const isImport = is.importTestnorge
		const isFraIdenter = is.opprettFraIdenter

		if (!currentMalId) {
			if (prevMalIdRef.current) {
				if (isNy) {
					const cleaned = createCleanedFormData(formMethods)
					formMethods.reset(cleaned)
					context.setIdenttype?.('FNR')
					context.updateContext?.({ identtype: 'FNR' })
				} else {
					const baseline = createBaselineFormData(formMethods, isImport, isFraIdenter)
					formMethods.reset(baseline)
				}
			}
			prevMalIdRef.current = undefined
			return
		}

		if (prevMalIdRef.current === currentMalId) return
		prevMalIdRef.current = currentMalId

		try {
			const environments = formMethods.getValues('environments')
			const malValues = initialValuesBasedOnMal(context.mal, environments)

			const nextValues = buildMalBasedValues(formMethods, malValues, currentMalId, is)
			normalizeFormValues(nextValues, malValues, formMethods, isNy)
			formMethods.reset(nextValues)

			updateContextFromMal(context, formMethods, malValues, isNy)
		} catch (e) {}
	}, [
		context.mal,
		is.nyBestilling,
		is.nyBestillingFraMal,
		is.leggTil,
		is.leggTilPaaGruppe,
		is.importTestnorge,
		is.opprettFraIdenter,
	])
}
