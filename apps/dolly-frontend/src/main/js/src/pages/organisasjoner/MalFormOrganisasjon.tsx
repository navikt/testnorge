import React, { BaseSyntheticEvent, useState } from 'react'
import { ifPresent, requiredString } from '@/utils/YupValidations'
import { useDollyOrganisasjonMalerBrukerOgMalnavn } from '@/utils/hooks/useMaler'
import Loading from '@/components/ui/loading/Loading'
import {
	MalerFormProps,
	MalTyper,
} from '@/components/bestillingsveileder/stegVelger/steg/steg3/MalForm'
import { MalOppsummering } from '@/components/bestillingsveileder/stegVelger/steg/steg3/MalOppsummering'

// eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
export const MalFormOrganisasjon = ({
	brukerId,
	formikBag: { setFieldValue },
	opprettetFraMal,
}: MalerFormProps) => {
	const [typeMal, setTypeMal] = useState(MalTyper.OPPRETT)
	const [opprettMal, setOpprettMal] = useState(false)
	const { maler, loading } = useDollyOrganisasjonMalerBrukerOgMalnavn(brukerId, null)

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

MalFormOrganisasjon.validation = {
	malBestillingNavn: ifPresent('$malBestillingNavn', requiredString.nullable()),
}
