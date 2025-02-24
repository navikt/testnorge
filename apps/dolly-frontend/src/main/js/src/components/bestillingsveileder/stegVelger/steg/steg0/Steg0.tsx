import React, { useContext, useEffect } from 'react'
import { useFormContext } from 'react-hook-form'
import { VelgGruppe } from '@/components/bestillingsveileder/stegVelger/steg/steg3/VelgGruppe'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { MalVelger } from '@/components/bestillingsveileder/startModal/MalVelger'
import { useCurrentBruker } from '@/utils/hooks/useBruker'

const Steg0 = () => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const formMethods = useFormContext()
	const { currentBruker } = useCurrentBruker()

	const formGruppeId = formMethods.watch('gruppeId')
	const gruppeId: string = formGruppeId || opts?.gruppeId || opts?.gruppe?.id

	useEffect(() => {
		formMethods.setValue('gruppeId', gruppeId && parseInt(gruppeId))
	}, [gruppeId])

	return (
		<div className="start-bestilling-modal">
			<div className="dolly-panel dolly-panel-open">
				<VelgGruppe formMethods={formMethods} title={'Hvilken gruppe vil du bestille til?'} />
			</div>
			<div className="dolly-panel dolly-panel-open">
				<MalVelger brukernavn={currentBruker?.brukernavn} gruppeId={gruppeId} />
			</div>
		</div>
	)
}

export default Steg0
