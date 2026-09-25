import React, { useEffect, useMemo, useState } from 'react'

import './StatusPage.less'
import { Heading, LocalAlert, VStack } from '@navikt/ds-react'

import BlankHeader from '@/components/BlankHeader/BlankHeader'
import Loading from '@/components/loading/Loading'
import { FagsystemStatusCard } from '@/pages/StatusPage/FagsystemStatusCard'
import { isRunning, useFagsystemStatuses } from '@/pages/StatusPage/useFagsystemStatuses'

export default () => {
	const { activeRunId, errorMessage, groupedStatuses, initialLoading, rerun, startingSystemId } =
		useFagsystemStatuses()
	const [now, setNow] = useState(() => Date.now())

	useEffect(() => {
		const intervalId = window.setInterval(() => setNow(Date.now()), 30_000)
		return () => window.clearInterval(intervalId)
	}, [])

	const runningCount = useMemo(
		() => groupedStatuses.flat().filter((status) => isRunning(status.state)).length,
		[groupedStatuses],
	)
	const failedCount = useMemo(
		() =>
			groupedStatuses
				.flat()
				.filter(
					(status) =>
						!isRunning(status.state) &&
						status.state !== 'OK' &&
						status.state !== 'NOT_RUN' &&
						!(status.state === 'TECHNICAL_ONLY' && status.technicalStatus.state === 'UP'),
				).length,
		[groupedStatuses],
	)

	if (initialLoading) {
		return (
			<>
				<BlankHeader />
				<main className="status-page-loading">
					<Loading label="Henter fagssystemstatus" />
				</main>
			</>
		)
	}

	return (
		<>
			<BlankHeader />
			<main className="status-page">
				<VStack gap="space-24">
					<div>
						<Heading className="status-page-title" level="1" size="large">
							Dolly fagssystemstatus
						</Heading>
						<p className="status-page-introduction">
							Statusene oppdateres automatisk når siden lastes. Resultatene lagres i én time.
						</p>
					</div>
					{errorMessage && (
						<LocalAlert status="error" size="small">
							<LocalAlert.Header>
								<LocalAlert.Title as="h2">Statussjekken feilet</LocalAlert.Title>
							</LocalAlert.Header>
							<LocalAlert.Content>{errorMessage}</LocalAlert.Content>
						</LocalAlert>
					)}
					<p className="status-page-live-region" aria-live="polite">
						{runningCount > 0
							? `${runningCount} statussjekker pågår.`
							: activeRunId
								? 'Testkjøringen fullføres.'
								: `Statussjekkene er ferdige. ${failedCount} statuser har feil.`}
					</p>
					{groupedStatuses.length === 0 ? (
						<LocalAlert status="warning" size="small">
							<LocalAlert.Header>
								<LocalAlert.Title as="h2">Ingen statuser tilgjengelig</LocalAlert.Title>
							</LocalAlert.Header>
							<LocalAlert.Content>Last inn siden på nytt for å prøve igjen.</LocalAlert.Content>
						</LocalAlert>
					) : (
						<div className="fagsystem-grid">
							{groupedStatuses.map((statuses) => (
								<FagsystemStatusCard
									key={statuses[0].systemId}
									statuses={statuses}
									anyRunActive={activeRunId !== null}
									starting={startingSystemId === statuses[0].systemId}
									now={now}
									onRerun={(systemId) => void rerun(systemId)}
								/>
							))}
						</div>
					)}
				</VStack>
			</main>
		</>
	)
}
