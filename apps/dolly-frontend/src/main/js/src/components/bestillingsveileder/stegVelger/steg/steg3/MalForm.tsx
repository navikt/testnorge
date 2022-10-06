import React, { BaseSyntheticEvent, useState } from 'react'
import { ifPresent, requiredString } from '~/utils/YupValidations'
import { Mal, useDollyMalerBrukerOgMalnavn } from '~/utils/hooks/useMaler'
import Loading from '~/components/ui/loading/Loading'
import { MalOppsummering } from '~/components/bestillingsveileder/stegVelger/steg/steg3/MalOppsummering'

export type MalerFormProps = {
	brukerId: string
	formikBag: { setFieldValue: (arg0: string, arg1: string) => void }
	opprettetFraMal: string
}

export const MalForm = ({
	brukerId,
	formikBag: { setFieldValue },
	opprettetFraMal,
}: MalerFormProps) => {
	const [typeMal, setTypeMal] = useState(MalTyper.OPPRETT)
	const [opprettMal, setOpprettMal] = useState(false)
	const { maler, loading } = useDollyMalerBrukerOgMalnavn(brukerId, null)

	if (loading) {
		return <Loading label="Laster maler..." />
	}

	const handleToggleChange = (value: MalTyper) => {
		setTypeMal(value)
		if (value === MalTyper.OPPRETT) {
			setFieldValue('malBestillingNavn', '')
		} else if (value === MalTyper.ENDRE) {
			setFieldValue('malBestillingNavn', opprettetFraMal || '')
		}
	}

	const handleCheckboxChange = (value: BaseSyntheticEvent) => {
		setFieldValue('malBestillingNavn', value.target?.checked ? '' : undefined)
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
	malBestillingNavn: ifPresent('$malBestillingNavn', requiredString.nullable()),
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
