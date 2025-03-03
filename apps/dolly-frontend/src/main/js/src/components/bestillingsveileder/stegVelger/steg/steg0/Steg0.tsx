import React, { useContext, useEffect } from 'react'
import { useFormContext } from 'react-hook-form'
import { VelgGruppe } from '@/components/bestillingsveileder/stegVelger/steg/steg0/VelgGruppe'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { MalVelgerIdent } from '@/components/bestillingsveileder/startModal/MalVelgerIdent'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { MalVelgerOrganisasjon } from '@/pages/organisasjoner/MalVelgerOrganisasjon'
import { VelgIdenttype } from '@/components/bestillingsveileder/stegVelger/steg/steg0/VelgIdenttype'

const Steg0 = () => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const isOrganisasjon =
		opts.is?.nyOrganisasjon || opts.is?.nyStandardOrganisasjon || opts.is?.nyOrganisasjonFraMal
	const isNyIdent = opts.is?.nyBestilling || opts.is?.nyBestillingFraMal
	const formMethods = useFormContext()
	const { currentBruker } = useCurrentBruker()

	const formGruppeId = formMethods.watch('gruppeId')
	const gruppeId: string = formGruppeId || opts?.gruppeId || opts?.gruppe?.id

	useEffect(() => {
		formMethods.setValue('gruppeId', gruppeId && parseInt(gruppeId))
	}, [gruppeId])

	return (
		<div className="start-bestilling-modal">
			{!isOrganisasjon && (
				<>
					{isNyIdent && (
						<div className="dolly-panel dolly-panel-open">
							<VelgIdenttype />
						</div>
					)}
					<div className="dolly-panel dolly-panel-open">
						<VelgGruppe formMethods={formMethods} title={'Hvilken gruppe vil du bestille til?'} />
					</div>
				</>
			)}
			<div className="dolly-panel dolly-panel-open">
				{isOrganisasjon ? (
					<MalVelgerOrganisasjon brukernavn={currentBruker?.brukernavn} gruppeId={gruppeId} />
				) : (
					<MalVelgerIdent brukernavn={currentBruker?.brukernavn} gruppeId={gruppeId} />
				)}
			</div>
		</div>
	)
}

export default Steg0
