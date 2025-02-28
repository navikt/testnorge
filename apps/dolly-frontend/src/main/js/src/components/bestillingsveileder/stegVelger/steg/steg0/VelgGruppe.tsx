import React, { useContext, useEffect, useState } from 'react'
import { Gruppevalg, VelgGruppeToggle } from '@/components/velgGruppe/VelgGruppeToggle'
import * as Yup from 'yup'
import { UseFormReturn } from 'react-hook-form/dist/types'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'

type VelgGruppeProps = {
	formMethods: UseFormReturn
	title: string
	fraGruppe?: number
}

export const VelgGruppe = ({ formMethods, title, fraGruppe = null }: VelgGruppeProps) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const gruppeId = formMethods.getValues('gruppeId') || opts?.gruppeId || opts?.gruppe?.id
	const [gruppevalg, setGruppevalg] = useState(Gruppevalg.MINE)
	const [valgtGruppe] = useState(gruppeId)

	useEffect(() => {
		if (formMethods.watch('gruppeId') !== valgtGruppe) {
			formMethods.setValue('gruppeId', valgtGruppe)
			formMethods.trigger('gruppeId')
		}
	}, [valgtGruppe])

	return (
		<div className="input-oppsummering">
			<h2>{title}</h2>
			<VelgGruppeToggle
				fraGruppe={fraGruppe}
				gruppevalg={gruppevalg}
				setGruppevalg={setGruppevalg}
			/>
		</div>
	)
}

VelgGruppe.validation = {
	gruppeId: Yup.number().required('Velg gruppe for bestillingen'),
}
