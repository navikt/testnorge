import React, { useEffect, useState } from 'react'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import Loading from '~/components/ui/loading/Loading'
import BestillingResultat from './BestillingResultat/BestillingResultat'
import { BestillingProgresjon } from '~/components/bestilling/statusListe/BestillingProgresjon/BestillingProgresjon'
import { Bestillingsstatus } from '~/utils/hooks/useOrganisasjoner'

type StatusProps = {
	gruppeId: string
	brukerId: string
	bestillingListe: Bestillingsstatus[]
	isCanceling: boolean
	cancelBestilling: Function
}
const StatusListe = ({
	bestillingListe,
	cancelBestilling,
	isCanceling,
}: StatusProps) => {
	const [nyeBestillinger, setNyeBestillinger] = useState([])
	const [ferdigBestillinger, setFerdigBestillinger] = useState([])

	const filtrerNyeBestillinger = (bestillinger: Bestillingsstatus[]) => {
		const nyBestillingListe = Object.values(bestillinger).filter(
			(bestilling) =>
				!bestilling.ferdig ||
				(bestilling.ferdig && nyeBestillinger.some((best) => best.id === bestilling.id))
		)
		setNyeBestillinger(nyBestillingListe)
	}

	const onFinishBestilling = (bestilling) => {
		if (!ferdigBestillinger.find((b) => b.id === bestilling.id)) {
			setFerdigBestillinger(ferdigBestillinger.concat([bestilling]))

			const nyenye = nyeBestillinger.filter((nye) => !ferdigBestillinger.find((b) => b.id === nye.id))
			setNyeBestillinger(nyenye)
		}
	}

	const lukkBestilling = (id) => {
		setNyeBestillinger(nyeBestillinger.filter((nye) => !id === nye.id))
		setFerdigBestillinger(ferdigBestillinger.filter((ferdig) => ferdig.id !== id))
	}

	useEffect(() => {
		filtrerNyeBestillinger(bestillingListe)
	}, [bestillingListe])

	if (isCanceling) {
		return (
			<ContentContainer className="loading-content-container">
				<Loading label="AVBRYTER BESTILLING" />
			</ContentContainer>
		)
	}

	const ikkeFerdig = nyeBestillinger.map((bestilling) => (
		<div className="bestilling-status" key={bestilling.id}>
			<BestillingProgresjon
				bestillingID={bestilling.id}
				erOrganisasjon={bestilling.organisasjonNummer}
				cancelBestilling={cancelBestilling}
				onFinishBestilling={(bestilling) => setTimeout(() => onFinishBestilling(bestilling), 0) }
			/>
		</div>
	))

	const ferdig = ferdigBestillinger.map((ferdig) => (
		<div className="bestilling-status" key={`ferdig-bestilling-${ferdig.id}`}>
			<BestillingResultat bestilling={ferdig} setNyeBestillinger={()=>{}} lukkBestilling={(id) => lukkBestilling(id)} />
		</div>
	))

	return ikkeFerdig.concat(...ferdig)
}
export default StatusListe
