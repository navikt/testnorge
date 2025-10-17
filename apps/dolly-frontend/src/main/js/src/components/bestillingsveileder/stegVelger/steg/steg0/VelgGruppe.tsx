import React, { useContext, useEffect, useState } from 'react'
import { VelgGruppeToggle } from '@/components/velgGruppe/VelgGruppeToggle'
import * as Yup from 'yup'
import { UseFormReturn } from 'react-hook-form/dist/types'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { useEgneGrupper } from '@/utils/hooks/useGruppe'
import Loading from '@/components/ui/loading/Loading'
import { ifPresent } from '@/utils/YupValidations'

type VelgGruppeProps = {
	formMethods: UseFormReturn
	title: string
	fraGruppe?: number
}

export const VelgGruppe = ({ formMethods, title, fraGruppe = null }: VelgGruppeProps) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const gruppeId = formMethods.getValues('gruppeId') || opts?.gruppeId || opts?.gruppe?.id

	const { currentBruker } = useCurrentBruker()
	const { grupper, loading } = useEgneGrupper(
		currentBruker?.representererTeam?.brukerId ?? currentBruker?.brukerId,
	)

	const [valgtGruppe] = useState(gruppeId)

	useEffect(() => {
		if (formMethods.watch('gruppeId') !== valgtGruppe) {
			formMethods.setValue('gruppeId', valgtGruppe)
			formMethods.trigger('gruppeId')
		}
	}, [valgtGruppe])

	return (
		<>
			<h2>{title}</h2>
			{loading ? (
				<Loading label="Laster grupper ..." />
			) : (
				<VelgGruppeToggle fraGruppe={fraGruppe} grupper={grupper} />
			)}
		</>
	)
}

VelgGruppe.validation = {
	gruppeId: Yup.number()
		.transform((i, j) => (j === '' ? null : i))
		.required('Velg gruppe for bestillingen'),
	bruker: ifPresent('$bruker', Yup.string().required('Velg bruker')),
}
