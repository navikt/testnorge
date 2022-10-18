import React, { Fragment, useEffect, useState } from 'react'
import Loading from '~/components/ui/loading/Loading'
import { Line } from 'rc-progress'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import Icon from '~/components/ui/icon/Icon'

import './BestillingProgresjon.less'
import { useOrganisasjonBestillingStatus } from '~/utils/hooks/useOrganisasjoner'
import { useBestillingById } from '~/utils/hooks/useBestilling'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	REGEX_BACKEND_ORGANISASJONER,
	useMatchMutate,
} from '~/utils/hooks/useMutate'

type ProgresjonProps = {
	bestillingID: string
	erOrganisasjon: boolean
	cancelBestilling: Function
	onFinishBestilling: Function
}

export const BestillingProgresjon = ({
	bestillingID,
	erOrganisasjon,
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

	function getBestillingStatusText(erSykemelding: boolean) {
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
		onFinishBestilling(bestilling)
		mutate(REGEX_BACKEND_GRUPPER)
		mutate(REGEX_BACKEND_BESTILLINGER)
		mutate(REGEX_BACKEND_ORGANISASJONER)
	}

	const calculateStatus = () => {
		const total = erOrganisasjon ? 1 : bestilling.antallIdenter
		const sykemelding =
			!erOrganisasjon &&
			bestilling.bestilling.sykemelding != null &&
			bestilling.bestilling.sykemelding.syntSykemelding != null
		const antallLevert = erOrganisasjon ? bestillingStatus?.antallLevert : bestilling?.antallLevert

		let percent = (100 / total) * antallLevert
		let text = `Opprettet ${antallLevert} av ${total}`

		// Indikerer progress hvis ingenting har skjedd enda
		if (percent === 0) {
			percent += 10
		}

		if (antallLevert === total) {
			text = `Ferdigstiller bestilling`
			ferdigstillBestilling()
		}
		const aktivBestillingStatusText = getBestillingStatusText(sykemelding)
		const title = percent === 100 ? 'FERDIG' : aktivBestillingStatusText

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
	const SECONDS_BEFORE_WARNING_MESSAGE_ORGANISASJON = 300

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
	}, [bestilling, bestillingStatus])

	if (loading) {
		return null
	}

	const { percentFinished, tittel, description } = calculateStatus()

	if (percentFinished >= 100) {
		onFinishBestilling(bestilling)
		return null
	}

	return (
		<Fragment>
			<div className="flexbox--space">
				<h5>
					<Loading onlySpinner /> {tittel}
				</h5>
				<span>{description}</span>
			</div>
			<div>
				<Line percent={percentFinished} strokeWidth={0.5} trailWidth={0.5} strokeColor="#254b6d" />
			</div>
			{timedOut && (
				<div className="cancel-container">
					<div>
						<Icon kind={'report-problem-circle'} />
						<h5 className="feil-status-text">
							Dette tar lengre tid enn forventet. Hvis bestillingen er kompleks kan du gi Dolly litt
							mer tid før du eventuelt avbryter.
						</h5>
					</div>
					<NavButton variant={'danger'} onClick={handleCancelBtn}>
						Avbryt bestilling
					</NavButton>
				</div>
			)}
		</Fragment>
	)
}
