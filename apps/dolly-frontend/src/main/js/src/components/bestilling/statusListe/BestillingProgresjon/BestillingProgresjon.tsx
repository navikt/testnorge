import React, { useEffect, useMemo, useState } from 'react'
import Loading from '@/components/ui/loading/Loading'
import { Line } from 'rc-progress'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import Icon from '@/components/ui/icon/Icon'

import './BestillingProgresjon.less'
import { useBestillingStream } from '@/utils/hooks/useBestillingStream'
import { useOrgBestillingStream } from '@/utils/hooks/useOrgBestillingStream'
import { REGEX_BACKEND_ORGANISASJONER, useMatchMutate } from '@/utils/hooks/useMutate'
import { BestillingStatus } from '@/components/bestilling/statusListe/BestillingProgresjon/BestillingStatus'
import { TestComponentSelectors } from '#/mocks/Selectors'
import {
	calculateProgress,
	getExpectedFagsystemer,
	mergeStatusWithExpected,
} from '@/components/bestilling/statusListe/BestillingProgresjon/fagsystemUtils'

type ProgresjonProps = {
	bestillingID: string | number
	erOrganisasjon?: boolean
	cancelBestilling: Function
	onFinishBestilling?: Function
}

const SECONDS_BEFORE_WARNING = 120
const SECONDS_BEFORE_WARNING_ORG = 200

export const BestillingProgresjon = ({
	bestillingID,
	erOrganisasjon = false,
	cancelBestilling,
	onFinishBestilling,
}: ProgresjonProps) => {
	const [timedOut, setTimedOut] = useState(false)
	const [orgStatus, setOrgStatus] = useState<string | null>(null)
	const [hasFinished, setHasFinished] = useState(false)
	const mutate = useMatchMutate()

	const { bestilling, loading: regularLoading } = useBestillingStream(
		bestillingID,
		erOrganisasjon,
		true,
	)
	const { bestillingStatus, loading: orgLoading } = useOrgBestillingStream(
		bestillingID,
		erOrganisasjon,
		true,
	)

	const data = bestilling || bestillingStatus

	const isFerdig = data?.ferdig
	const sistOppdatert = data?.sistOppdatert
	const loading = erOrganisasjon ? orgLoading : regularLoading

	const antallLevert = erOrganisasjon
		? bestillingStatus?.antallLevert || 0
		: bestilling?.antallLevert || 0

	const antallIdenter = erOrganisasjon ? 1 : bestilling?.antallIdenter || 0

	const erSykemelding =
		!erOrganisasjon &&
		bestilling?.bestilling?.sykemelding != null &&
		bestilling?.bestilling?.sykemelding?.syntSykemelding != null

	useEffect(() => {
		if (erOrganisasjon && bestillingStatus) {
			const detaljertStatus =
				bestillingStatus?.status?.[0]?.statuser?.[0]?.detaljert?.[0]?.detaljertStatus
			if (detaljertStatus && orgStatus !== detaljertStatus) {
				setOrgStatus(detaljertStatus)
			}
		}
	}, [bestillingStatus])

	useEffect(() => {
		if (!sistOppdatert) return
		const elapsed = (Date.now() - new Date(sistOppdatert).getTime()) / 1000
		const grense = erOrganisasjon ? SECONDS_BEFORE_WARNING_ORG : SECONDS_BEFORE_WARNING
		if (elapsed > grense) {
			setTimedOut(true)
		}
	}, [sistOppdatert, erOrganisasjon])

	useEffect(() => {
		if (isFerdig && !hasFinished) {
			setHasFinished(true)
			onFinishBestilling?.(data)
			if (erOrganisasjon) {
				mutate(REGEX_BACKEND_ORGANISASJONER)
			} else {
				const gruppeId = bestilling?.gruppeId
				if (gruppeId) {
					mutate(new RegExp(`^/dolly-backend/api/v1/gruppe/${gruppeId}`))
					mutate(new RegExp(`^/dolly-backend/api/v1/bestilling/gruppe/${gruppeId}`))
				}
			}
		}
	}, [isFerdig])

	const handleCancelBtn = () => {
		cancelBestilling(bestillingID, erOrganisasjon)
		if (!hasFinished) {
			setHasFinished(true)
			onFinishBestilling?.(data)
		}
	}

	const expectedFagsystemer = useMemo(
		() => (!erOrganisasjon ? getExpectedFagsystemer(data?.bestilling) : []),
		[data?.bestilling, erOrganisasjon],
	)

	const mergedStatus = useMemo(
		() => mergeStatusWithExpected(data?.status || [], expectedFagsystemer),
		[data?.status, expectedFagsystemer],
	)

	const progress = useMemo(() => {
		const { percent, text } = calculateProgress({
			antallIdenter,
			antallLevert,
			erOrganisasjon,
			statusList: mergedStatus,
			expectedTotal: expectedFagsystemer.length,
		})

		let title: string
		if (isFerdig) {
			title = 'FERDIG'
		} else if (erSykemelding) {
			title =
				'AKTIV BESTILLING (Syntetisert sykemelding behandler mye data og kan derfor ta litt tid)'
		} else if (erOrganisasjon) {
			title = `AKTIV BESTILLING (${orgStatus || 'Bestillingen tar opptil flere minutter per valgte miljø'})`
		} else {
			title = 'AKTIV BESTILLING'
		}

		return { percent, title, text }
	}, [antallLevert, antallIdenter, isFerdig, erSykemelding, erOrganisasjon, orgStatus, mergedStatus, expectedFagsystemer])

	if (loading) {
		return <Loading label={'Henter bestilling ...'} />
	}
	if (!data || hasFinished || isFerdig) {
		return null
	}

	return (
		<div className="bestilling-status">
			<div className="bestilling-resultat">
				<div className="status-header">
					<p>Bestilling #{data.id}</p>
					<h3>Bestillingsstatus</h3>
					<div className="status-header_button-wrap" />
				</div>
				<hr />
			</div>
			<div>
				<BestillingStatus
					bestilling={{ ...data, status: mergedStatus }}
					erOrganisasjon={erOrganisasjon}
				/>
			</div>
			<div className="flexbox--space">
				<h5>
					<Loading onlySpinner /> {progress.title}
				</h5>
				<span>{isFerdig ? 'Ferdigstiller bestilling' : progress.text}</span>
			</div>
			<div>
				<Line percent={progress.percent} strokeWidth={0.5} trailWidth={0.5} strokeColor="#254b6d" />
			</div>
			<div className="cancel-container">
				{timedOut && !erOrganisasjon && (
					<div>
						<Icon kind={'report-problem-circle'} />
						<h5 className="feil-status-text">
							Dette tar lengre tid enn forventet. Hvis bestillingen er kompleks kan du gi Dolly litt
							mer tid før du eventuelt avbryter.
						</h5>
					</div>
				)}
				<NavButton
					data-testid={TestComponentSelectors.BUTTON_AVBRYT_BESTILLING}
					variant={timedOut ? 'danger' : 'primary-neutral'}
					size={'small'}
					onClick={handleCancelBtn}
				>
					Avbryt bestilling
				</NavButton>
			</div>
		</div>
	)
}
