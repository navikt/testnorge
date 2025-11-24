import React, { BaseSyntheticEvent, useState } from 'react'
import { ifPresent, requiredString } from '@/utils/YupValidations'
import { Mal, useMalbestillingBruker } from '@/utils/hooks/useMaler'
import Loading from '@/components/ui/loading/Loading'
import { MalOppsummering } from '@/components/bestillingsveileder/stegVelger/steg/steg3/MalOppsummering'
import { UseFormReturn } from 'react-hook-form/dist/types'

export type MalerFormProps = {
	brukerId: string
	formMethods: UseFormReturn
	opprettetFraMal: string
}

export const MalForm = ({ brukerId, formMethods, opprettetFraMal }: MalerFormProps) => {
	const [typeMal, setTypeMal] = useState(MalTyper.OPPRETT)
	const [opprettMal, setOpprettMal] = useState(false)
	const { maler, loading } = useMalbestillingBruker(brukerId)

	if (loading) {
		return <Loading label="Laster maler..." />
	}

	const handleToggleChange = (value: MalTyper) => {
		setTypeMal(value)
		if (value === MalTyper.OPPRETT) {
			formMethods.setValue('malBestillingNavn', '')
		} else if (value === MalTyper.ENDRE) {
			formMethods.setValue('malBestillingNavn', opprettetFraMal || '')
		}
	}

	const handleCheckboxChange = (value: BaseSyntheticEvent) => {
		formMethods.setValue('malBestillingNavn', value.target?.checked ? '' : undefined)
		setOpprettMal(value.target?.checked)
	}

	return (
		<MalOppsummering
			onChange={handleCheckboxChange}
			opprettMal={opprettMal}
			onToggleChange={(value: MalTyper) => handleToggleChange(value)}
			typeMal={typeMal}
			malbestillinger={maler}
		/>
	)
}

MalForm.validation = {
	malBestillingNavn: ifPresent('$malBestillingNavn', requiredString),
}

export enum MalTyper {
	OPPRETT = 'OPPRETT',
	ENDRE = 'ENDRE',
}

export const toggleMalValues = [
	{
		value: MalTyper.OPPRETT,
		label: 'Legg til ny',
	},
	{
		value: MalTyper.ENDRE,
		label: 'Overskriv eksisterende',
	},
]

export const getMalOptions = (malbestillinger: Mal[]) => {
	if (!malbestillinger) {
		return []
	}
	return malbestillinger.map((mal) => ({
		value: mal.malNavn,
		label: mal.malNavn,
	}))
}
