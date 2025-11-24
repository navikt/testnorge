import React, { useEffect, useState } from 'react'
import Loading from '@/components/ui/loading/Loading'
import { Line } from 'rc-progress'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import Icon from '@/components/ui/icon/Icon'

import './BestillingProgresjon.less'
import { useOrganisasjonBestillingStatus } from '@/utils/hooks/useDollyOrganisasjoner'
import { useBestillingById } from '@/utils/hooks/useBestilling'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	REGEX_BACKEND_ORGANISASJONER,
	useMatchMutate,
} from '@/utils/hooks/useMutate'
import { BestillingStatus } from '@/components/bestilling/statusListe/BestillingProgresjon/BestillingStatus'
import { TestComponentSelectors } from '#/mocks/Selectors'

type ProgresjonProps = {
	bestillingID: string | number
	erOrganisasjon?: boolean
	cancelBestilling: Function
	onFinishBestilling?: Function
}

export const BestillingProgresjon = ({
	bestillingID,
	erOrganisasjon = false,
	cancelBestilling,
	onFinishBestilling,
}: ProgresjonProps) => {
	const setDetaljertOrgStatus = (status: any) => {
		const detaljertStatus = status?.status?.[0]?.statuser?.[0]?.detaljert?.[0]?.detaljertStatus
		if (orgStatus !== detaljertStatus) {
			setOrgStatus(detaljertStatus)
		}
	}
	const harBestillingFeilet = (sistOppdatertState: Date) => {
		const liveTimeStamp = new Date().getTime()
		const oldTimeStamp = new Date(sistOppdatertState).getTime()

		const antallSekunderBrukt = (liveTimeStamp - oldTimeStamp) / 1000
		const tidsbegrensning = erOrganisasjon
			? SECONDS_BEFORE_WARNING_MESSAGE_ORGANISASJON
			: SECONDS_BEFORE_WARNING_MESSAGE

		if (antallSekunderBrukt > tidsbegrensning) {
			setTimedOut(true)
		}
	}

	const getBestillingStatusText = (erSykemelding: boolean) => {
		if (erSykemelding) {
			return 'AKTIV BESTILLING (Syntetisert sykemelding behandler mye data og kan derfor ta litt tid)'
		} else {
			return erOrganisasjon
				? `AKTIV BESTILLING (${
						orgStatus || 'Bestillingen tar opptil flere minutter per valgte miljø'
					})`
				: 'AKTIV BESTILLING'
		}
	}

	const ferdigstillBestilling = () => {
		onFinishBestilling?.(bestilling || bestillingStatus)
		if (erOrganisasjon) {
			mutate(REGEX_BACKEND_ORGANISASJONER)
		} else {
			mutate(REGEX_BACKEND_GRUPPER)
			mutate(REGEX_BACKEND_BESTILLINGER)
		}
	}

	const calculateStatus = () => {
		const total = erOrganisasjon ? 1 : bestilling?.antallIdenter || 0
		const sykemelding =
			!erOrganisasjon &&
			bestilling?.bestilling?.sykemelding != null &&
			bestilling?.bestilling?.sykemelding?.syntSykemelding != null
		const antallLevert = erOrganisasjon
			? bestillingStatus?.antallLevert || 0
			: bestilling?.antallLevert || 0

		let percent = total > 0 ? (100 / total) * antallLevert : 0
		let text = `Oppretter ${antallLevert || 0} av ${total}`

		// Indikerer progress hvis ingenting har skjedd enda
		if (percent === 0) {
			percent += 10
		}

		if (bestilling?.ferdig || bestillingStatus?.ferdig) {
			text = `Ferdigstiller bestilling`
			ferdigstillBestilling()
		}
		const aktivBestillingStatusText = getBestillingStatusText(sykemelding)

		const title = bestilling?.ferdig ? 'FERDIG' : aktivBestillingStatusText

		return {
			percentFinished: percent,
			tittel: title,
			description: text,
		}
	}

	const handleCancelBtn = () => {
		cancelBestilling(bestillingID, erOrganisasjon)
		ferdigstillBestilling()
	}

	const SECONDS_BEFORE_WARNING_MESSAGE = 120
	const SECONDS_BEFORE_WARNING_MESSAGE_ORGANISASJON = 200

	const { bestilling, loading } = useBestillingById(bestillingID, erOrganisasjon, true)
	const { bestillingStatus } = useOrganisasjonBestillingStatus(bestillingID, erOrganisasjon, true)

	const [timedOut, setTimedOut] = useState(false)
	const [orgStatus, setOrgStatus] = useState(null)
	const mutate = useMatchMutate()

	const sistOppdatert = bestilling?.sistOppdatert || bestillingStatus?.sistOppdatert || new Date()

	useEffect(() => {
		if (erOrganisasjon) {
			setDetaljertOrgStatus(bestillingStatus)
		}
		harBestillingFeilet(sistOppdatert)
	}, [bestillingStatus, bestilling])

	if (loading) {
		return <Loading label={'Henter bestilling ...'} />
	}
	if (!bestilling && !bestillingStatus) {
		return null
	}

	const { percentFinished, tittel, description } = calculateStatus()

	if (
		percentFinished === 100 &&
		(bestilling?.ferdig === true || bestillingStatus?.ferdig === true)
	) {
		onFinishBestilling?.(bestilling || bestillingStatus)
		return null
	}

	return (
		<div className="bestilling-status">
			<div className="bestilling-resultat">
				<div className="status-header">
					<p>Bestilling #{bestilling?.id || bestillingStatus?.id}</p>
					<h3>Bestillingsstatus</h3>
					<div className="status-header_button-wrap" />
				</div>
				<hr />
			</div>
			<div>
				{!erOrganisasjon && bestilling && (
					<BestillingStatus
						bestilling={{ ...bestilling, systeminfo: bestilling?.systeminfo || '' }}
					/>
				)}
				{erOrganisasjon && bestillingStatus && (
					<BestillingStatus bestilling={bestillingStatus} erOrganisasjon />
				)}
			</div>
			<div className="flexbox--space">
				<h5>
					<Loading onlySpinner /> {tittel}
				</h5>
				<span>{description}</span>
			</div>
			<div>
				<Line percent={percentFinished} strokeWidth={0.5} trailWidth={0.5} strokeColor="#254b6d" />
			</div>
			{timedOut && !erOrganisasjon && (
				<div className="cancel-container">
					<div>
						<Icon kind={'report-problem-circle'} />
						<h5 className="feil-status-text">
							Dette tar lengre tid enn forventet. Hvis bestillingen er kompleks kan du gi Dolly litt
							mer tid før du eventuelt avbryter.
						</h5>
					</div>
					<NavButton
						data-testid={TestComponentSelectors.BUTTON_AVBRYT_BESTILLING}
						variant={'danger'}
						onClick={handleCancelBtn}
					>
						Avbryt bestilling
					</NavButton>
				</div>
			)}
		</div>
	)
}
