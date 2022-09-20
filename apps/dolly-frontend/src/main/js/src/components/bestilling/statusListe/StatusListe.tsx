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
import { Bestillingsstatus, useOrganisasjonBestilling } from '~/utils/hooks/useOrganisasjoner'
import { isEmpty } from 'lodash'

type StatusProps = {
	gruppeId: string
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
	useBestillingerGruppe(gruppeId)
	useOrganisasjonBestilling(brukerId, autoRefresh)

	const filtrerNyeBestillinger = (bestillinger: Bestillingsstatus[]) => {
		const nyBestillingListe = Object.values(bestillinger).filter(
			(bestilling) =>
				!bestilling.ferdig ||
				(bestilling.ferdig && nyeBestillinger.some((best) => best.id === bestilling.id))
		)
		setNyeBestillinger(nyBestillingListe)
	}

	useEffect(() => {
		filtrerNyeBestillinger(bestillingListe)
	}, [bestillingListe])

	useEffect(() => {
		setAutoRefresh(!isEmpty(nyeBestillinger) && nyeBestillinger.some((best) => !best.ferdig))
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
				<BestillingResultat bestilling={bestilling} setNyeBestillinger={setNyeBestillinger} />
			) : (
				<BestillingProgresjon
					bestillingID={bestilling.id}
					cancelBestilling={cancelBestilling}
					setNyeBestillinger={setNyeBestillinger}
				/>
			)}
		</div>
	))
}
export default StatusListe
