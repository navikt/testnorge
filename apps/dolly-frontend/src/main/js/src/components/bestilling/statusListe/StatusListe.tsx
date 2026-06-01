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
		setNyeBestillinger((prevNye) => {
			const currentBestillinger = Object.values(bestillinger)
			const currentIds = new Set(currentBestillinger.map((b) => b?.id))

			const fromList = currentBestillinger.filter(
				(bestilling) =>
					!bestilling?.ferdig ||
					(bestilling?.ferdig && prevNye.some((best) => best?.id === bestilling?.id)),
			)

			const inProgressMissing = prevNye.filter((b) => b?.id && !currentIds.has(b.id) && !b?.ferdig)

			return [...fromList, ...inProgressMissing]
		})
	}

	const onFinishBestilling = (bestilling) => {
		setFerdigBestillinger((prev) => {
			if (prev.find((b) => b.id === bestilling.id) || !bestilling?.ferdig) return prev
			return prev.concat([bestilling])
		})
		setNyeBestillinger((prev) => prev.filter((nye) => bestilling.id !== nye.id))
	}

	const lukkBestilling = (id) => {
		setNyeBestillinger((prev) => prev.filter((nye) => id !== nye.id))
		setFerdigBestillinger((prev) => prev.filter((ferdig) => id !== ferdig.id))
	}

	useEffect(() => {
		filtrerNyeBestillinger(bestillingListe)
	}, [bestillingListe])

	const ferdigBestillingIds = new Set(ferdigBestillinger.map((bestilling) => bestilling?.id))
	const aktiveBestillinger = nyeBestillinger.filter(
		(bestilling) => !ferdigBestillingIds.has(bestilling?.id),
	)

	if (isCanceling) {
		return (
			<ContentContainer className="loading-content-container">
				<Loading label="AVBRYTER BESTILLING" />
			</ContentContainer>
		)
	}

	const ikkeFerdig = aktiveBestillinger.map((bestilling) => (
		<BestillingProgresjon
			key={bestilling.id}
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
