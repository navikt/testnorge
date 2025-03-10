import React, { useEffect, useState } from 'react'
import ContentContainer from '@/components/ui/contentContainer/ContentContainer'
import Loading from '@/components/ui/loading/Loading'
import BestillingResultat from './BestillingResultat/BestillingResultat'
import { BestillingProgresjon } from '@/components/bestilling/statusListe/BestillingProgresjon/BestillingProgresjon'
import { Bestillingsstatus } from '@/utils/hooks/useDollyOrganisasjoner'
import * as _ from 'lodash-es'

type StatusProps = {
	gruppeId: string
	brukerId: string
	bestillingListe: Bestillingsstatus[]
	isCanceling: boolean
	cancelBestilling: Function
}
const StatusListe = ({ bestillingListe, cancelBestilling, isCanceling }: StatusProps) => {
	const [nyeBestillinger, setNyeBestillinger] = useState([])
	const [ferdigBestillinger, setFerdigBestillinger] = useState([])

	const filtrerNyeBestillinger = (bestillinger: Bestillingsstatus[]) => {
		const nyBestillingListe = Object.values(bestillinger).filter(
			(bestilling) =>
				!bestilling?.ferdig ||
				(bestilling?.ferdig && nyeBestillinger.some((best) => best?.id === bestilling?.id)),
		)
		setNyeBestillinger(nyBestillingListe)
	}

	const onFinishBestilling = (bestilling) => {
		if (!ferdigBestillinger.find((b) => b.id === bestilling.id) && bestilling?.ferdig) {
			setFerdigBestillinger(ferdigBestillinger.concat([bestilling]))
			setNyeBestillinger(nyeBestillinger.filter((nye) => bestilling.id !== nye.id))
		}
	}

	const lukkBestilling = (id) => {
		setNyeBestillinger(nyeBestillinger.filter((nye) => id !== nye.id))
		setFerdigBestillinger(ferdigBestillinger.filter((ferdig) => id !== ferdig.id))
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
		<BestillingProgresjon
			key={bestilling.sistOppdatert}
			bestillingID={bestilling.id}
			erOrganisasjon={_.has(bestilling, 'organisasjonNummer')}
			cancelBestilling={cancelBestilling}
			onFinishBestilling={onFinishBestilling}
		/>
	))

	const ferdig = ferdigBestillinger.map((ferdig) => (
		<BestillingResultat
			key={ferdig.id}
			bestilling={ferdig}
			lukkBestilling={lukkBestilling}
			erOrganisasjon={_.has(ferdig, 'organisasjonNummer')}
		/>
	))

	return ferdig.concat(...ikkeFerdig)
}
export default StatusListe
