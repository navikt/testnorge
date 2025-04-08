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

type VelgGruppeProps = {
	formMethods: UseFormReturn
	title: string
	fraGruppe?: number
}

export const VelgGruppe = ({ formMethods, title, fraGruppe = null }: VelgGruppeProps) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const gruppeId = formMethods.getValues('gruppeId') || opts?.gruppeId || opts?.gruppe?.id

	const {
		currentBruker: { brukerId },
	} = useCurrentBruker()
	// const { grupper, loading } = useEgneGrupper(brukerId)
	const { grupper, loading } = useEgneGrupper('68e76503-0c49-41d3-bada-126d4d010604') //TODO: Fjern hardkoding av brukerId

	const [valgtGruppe] = useState(gruppeId)

	useEffect(() => {
		if (formMethods.watch('gruppeId') !== valgtGruppe) {
			formMethods.setValue('gruppeId', valgtGruppe)
			formMethods.trigger('gruppeId')
		}
	}, [valgtGruppe])

	return (
		<div style={{ backgroundColor: 'white', padding: '10px 20px' }}>
			<h2>{title}</h2>
			{loading ? (
				<Loading label="Laster grupper ..." />
			) : (
				<VelgGruppeToggle fraGruppe={fraGruppe} grupper={grupper} loading={loading} />
			)}
		</div>
	)
}

VelgGruppe.validation = {
	gruppeId: Yup.number().required('Velg gruppe for bestillingen'),
}
