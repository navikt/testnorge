import React, { useEffect, useState } from 'react'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import Loading from '~/components/ui/loading/Loading'
import BestillingResultat from './BestillingResultat/BestillingResultat'
import { BestillingProgresjon } from '~/components/bestilling/statusListe/BestillingProgresjon/BestillingProgresjon'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	useMatchMutate,
} from '~/utils/hooks/useMutate'
import { useBestillingerGruppe } from '~/utils/hooks/useBestilling'
import { isEmpty } from 'lodash'
import { Bestillingsstatus, useOrganisasjonBestilling } from '~/utils/hooks/useOrganisasjoner'

type StatusProps = {
	gruppeId: number
	brukerId: string
	bestillingListe: Bestillingsstatus[]
	isCanceling: boolean
	cancelBestilling: Function
}
const StatusListe = ({
	bestillingListe,
	brukerId,
	cancelBestilling,
	gruppeId,
	isCanceling,
}: StatusProps) => {
	const mutate = useMatchMutate()
	const [autoRefresh, setAutoRefresh] = useState(true)
	useBestillingerGruppe(gruppeId, autoRefresh)
	useOrganisasjonBestilling(brukerId, autoRefresh)

	const uferdigeBestillinger = Object.values(bestillingListe).filter(
		(bestilling) => !bestilling.ferdig
	)

	console.log('uferdigeBestillinger: ', uferdigeBestillinger) //TODO - SLETT MEG

	useEffect(() => {
		setAutoRefresh(!isEmpty(uferdigeBestillinger))
		mutate(REGEX_BACKEND_GRUPPER)
		mutate(REGEX_BACKEND_BESTILLINGER)
	}, [uferdigeBestillinger])

	if (isCanceling) {
		return (
			<ContentContainer className="loading-content-container">
				<Loading label="AVBRYTER BESTILLING" />
			</ContentContainer>
		)
	}

	return uferdigeBestillinger?.map((bestilling) => {
		console.log('bestilling: ', bestilling) //TODO - SLETT MEG
		return (
			<div className="bestilling-status" key={bestilling.id}>
				{bestilling.ferdig ? (
					<BestillingResultat bestilling={bestilling} />
				) : (
					<BestillingProgresjon bestilling={bestilling} cancelBestilling={cancelBestilling} />
				)}
			</div>
		)
	})
}
export default StatusListe
