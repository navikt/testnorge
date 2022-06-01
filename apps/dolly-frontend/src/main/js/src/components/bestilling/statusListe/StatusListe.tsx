import React, { useEffect, useState } from 'react'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import Loading from '~/components/ui/loading/Loading'
import BestillingResultat from './BestillingResultat/BestillingResultat'
import { BestillingProgresjon } from '~/components/bestilling/statusListe/BestillingProgresjon/BestillingProgresjon'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	REGEX_BACKEND_ORGANISASJONER,
	useMatchMutate,
} from '~/utils/hooks/useMutate'
import { useBestillingerGruppe } from '~/utils/hooks/useBestilling'
import { isEmpty, isEqual } from 'lodash'
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
	const [nyeBestillinger, setNyeBestillinger] = useState([])
	const [autoRefresh, setAutoRefresh] = useState(true)
	useBestillingerGruppe(gruppeId, autoRefresh)
	useOrganisasjonBestilling(brukerId, autoRefresh)

	const filtrerNyeBestillinger = (bestillinger: Bestillingsstatus[]) => {
		Object.values(bestillinger)
			.filter(
				(bestilling) =>
					!bestilling.ferdig && nyeBestillinger.every((nyeBest) => !isEqual(bestilling, nyeBest))
			)
			.forEach((bestilling) => {
				setNyeBestillinger(nyeBestillinger.concat(bestilling))
			})
	}

	useEffect(() => {
		filtrerNyeBestillinger(bestillingListe)
	}, [bestillingListe])

	useEffect(() => {
		setAutoRefresh(!isEmpty(nyeBestillinger))
		mutate(REGEX_BACKEND_GRUPPER)
		mutate(REGEX_BACKEND_BESTILLINGER)
		mutate(REGEX_BACKEND_ORGANISASJONER)
	}, [nyeBestillinger])

	if (isCanceling) {
		return (
			<ContentContainer className="loading-content-container">
				<Loading label="AVBRYTER BESTILLING" />
			</ContentContainer>
		)
	}

	return nyeBestillinger?.map((bestilling) => (
		<div className="bestilling-status" key={bestilling.id}>
			{bestilling.ferdig ? (
				<BestillingResultat bestilling={bestilling} />
			) : (
				<BestillingProgresjon bestilling={bestilling} cancelBestilling={cancelBestilling} />
			)}
		</div>
	))
}
export default StatusListe
