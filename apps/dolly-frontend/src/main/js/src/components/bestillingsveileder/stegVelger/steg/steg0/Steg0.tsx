import React, { useContext, useEffect, useState } from 'react'
import { Gruppevalg } from '@/components/velgGruppe/VelgGruppeToggle'
import { useFormContext } from 'react-hook-form'
import { VelgGruppe } from '@/components/bestillingsveileder/stegVelger/steg/steg3/VelgGruppe'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'

const Steg0 = () => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const formMethods = useFormContext()

	const formGruppeId = formMethods.watch('gruppeId')
	const gruppeId = formGruppeId || opts?.gruppeId || opts?.gruppe?.id

	useEffect(() => {
		formMethods.setValue('gruppeId', gruppeId)
		formMethods.trigger('gruppeId')
	}, [])

	const [gruppevalg, setGruppevalg] = useState(Gruppevalg.MINE)

	return (
		<VelgGruppe
			formMethods={formMethods}
			title={'Hvilken gruppe vil du importere til?'}
			gruppevalg={gruppevalg}
			setGruppevalg={setGruppevalg}
		/>
	)
}

export default Steg0
