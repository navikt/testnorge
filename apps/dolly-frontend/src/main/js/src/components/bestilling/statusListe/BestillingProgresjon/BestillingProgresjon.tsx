import React, { Fragment, useEffect, useState } from 'react'
import Loading from '~/components/ui/loading/Loading'
import { Line } from 'rc-progress'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import Icon from '~/components/ui/icon/Icon'

import './BestillingProgresjon.less'
import { Bestillingsstatus, useOrganisasjonBestillingStatus } from '~/utils/hooks/useOrganisasjoner'

type ProgresjonProps = {
	bestilling: {
		sistOppdatert: Date
		hasOwnProperty: Function
		id: number
		antallIdenter: number
		bestilling: { sykemelding: { syntSykemelding: {} } }
		antallLevert: number
	}
	cancelBestilling: Function
	setNyeBestillinger: Function
}

export const BestillingProgresjon = ({
	bestilling,
	cancelBestilling,
	setNyeBestillinger,
}: ProgresjonProps) => {
	const SECONDS_BEFORE_WARNING_MESSAGE = 120
	const SECONDS_BEFORE_WARNING_MESSAGE_ORGANISASJON = 300

	const [timedOut, setTimedOut] = useState(false)
	const [orgStatus, setOrgStatus] = useState(null)

	const sistOppdatert = bestilling.sistOppdatert
	const erOrganisasjon = bestilling.hasOwnProperty('organisasjonNummer')

	const setDetaljertOrgStatus = (bestillingStatus: any) => {
		const detaljertStatus =
			bestillingStatus?.status?.[0]?.statuser?.[0]?.detaljert?.[0]?.detaljertStatus
		if (orgStatus !== detaljertStatus) {
			setOrgStatus(detaljertStatus)
		}
	}

	if (erOrganisasjon) {
		const { bestillingStatus } = useOrganisasjonBestillingStatus(bestilling.id, true)
		setDetaljertOrgStatus(bestillingStatus)
	}

	useEffect(() => {
		harBestillingFeilet(sistOppdatert)
	}, [bestilling])

	const harBestillingFeilet = (sistOppdatertState: Date) => {
		const liveTimeStamp = new Date().getTime()
		const oldTimeStamp = new Date(sistOppdatertState).getTime()

		const antallSekunderBrukt = (liveTimeStamp - oldTimeStamp) / 1000
		const tidsBegrensing = erOrganisasjon
			? SECONDS_BEFORE_WARNING_MESSAGE_ORGANISASJON
			: SECONDS_BEFORE_WARNING_MESSAGE

		if (antallSekunderBrukt > tidsBegrensing) {
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

	const calculateStatus = () => {
		const total = erOrganisasjon ? 1 : bestilling.antallIdenter
		const sykemelding =
			bestilling.bestilling.sykemelding != null &&
			bestilling.bestilling.sykemelding.syntSykemelding != null
		const antallLevert = bestilling.antallLevert

		let percent = (100 / total) * antallLevert
		let text = `Opprettet ${antallLevert} av ${total}`

		// Indikerer progress hvis ingenting har skjedd enda
		if (percent === 0) {
			percent += 10
		}

		if (antallLevert === total) {
			text = `Ferdigstiller bestilling`
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
		cancelBestilling(bestilling.id, erOrganisasjon)
		setNyeBestillinger((nyeBestillinger: Bestillingsstatus[]) =>
			nyeBestillinger.filter((best) => best.id !== bestilling.id)
		)
	}

	const { percentFinished, tittel, description } = calculateStatus()

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
					<NavButton type="fare" onClick={handleCancelBtn}>
						Avbryt bestilling
					</NavButton>
				</div>
			)}
		</Fragment>
	)
}
