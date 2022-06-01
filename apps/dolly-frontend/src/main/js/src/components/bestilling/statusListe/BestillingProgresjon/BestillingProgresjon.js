import React, { Fragment, useEffect, useState } from 'react'
import Loading from '~/components/ui/loading/Loading'
import { Line } from 'rc-progress'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import Icon from '~/components/ui/icon/Icon'

import './BestillingProgresjon.less'
import { useOrganisasjonBestillingStatus } from '~/utils/hooks/useOrganisasjoner'

export const BestillingProgresjon = ({ bestilling, cancelBestilling }) => {
	const SECONDS_BEFORE_WARNING_MESSAGE = 120
	const SECONDS_BEFORE_WARNING_MESSAGE_ORGANISASJON = 300

	const [failed, setFailed] = useState(false)
	const [orgStatus, setOrgStatus] = useState(null)

	const sistOppdatert = bestilling.sistOppdatert
	const erOrganisasjon = bestilling.hasOwnProperty('organisasjonNummer')

	const setDetaljertOrgStatus = (bestillingStatus, orgnummer) => {
		const detaljertStatus = bestillingStatus?.orgStatus?.[orgnummer]?.[0]?.details
		if (orgStatus !== detaljertStatus) {
			setOrgStatus(detaljertStatus)
		}
	}

	if (erOrganisasjon) {
		const { bestillingStatus } = useOrganisasjonBestillingStatus(
			[bestilling.organisasjonNummer],
			true
		)
		setDetaljertOrgStatus(bestillingStatus, bestilling.organisasjonNummer)
	}

	useEffect(() => {
		harBestillingFeilet(sistOppdatert)
	}, [bestilling])

	const harBestillingFeilet = (sistOppdatertState) => {
		const liveTimeStamp = new Date().getTime()
		const oldTimeStamp = new Date(sistOppdatertState).getTime()

		const antallSekunderBrukt = (liveTimeStamp - oldTimeStamp) / 1000
		const tidsBegrensing = erOrganisasjon
			? SECONDS_BEFORE_WARNING_MESSAGE_ORGANISASJON
			: SECONDS_BEFORE_WARNING_MESSAGE

		if (antallSekunderBrukt > tidsBegrensing) {
			setFailed(true)
		}
	}

	const calculateStatus = () => {
		const total = erOrganisasjon ? 1 : bestilling.antallIdenter
		const sykemelding =
			bestilling.bestilling.sykemelding != null &&
			bestilling.bestilling.sykemelding.syntSykemelding != null
		const antallLevert = bestilling.antallLevert

		// Percent
		let percent = (100 / total) * antallLevert
		let text = `Opprettet ${antallLevert} av ${total}`

		// To indicate progress hvis ingenting har skjedd enda
		if (percent === 0) percent += 10

		if (antallLevert === total) text = `Ferdigstiller bestilling`

		const aktivBestilling = sykemelding
			? 'AKTIV BESTILLING (Syntetisert sykemelding behandler mye data og kan derfor ta litt tid)'
			: erOrganisasjon
			? `AKTIV BESTILLING (${
					orgStatus ? orgStatus : 'Bestillingen tar opptil flere minutter per valgte miljø'
			  })`
			: 'AKTIV BESTILLING'
		const title = percent === 100 ? 'FERDIG' : aktivBestilling

		return {
			percent,
			title,
			text,
		}
	}

	const _onCancelBtn = () => {
		cancelBestilling(bestilling.id, erOrganisasjon)
	}

	const status = calculateStatus()

	return (
		<Fragment>
			<div className="flexbox--space">
				<h5>
					<Loading onlySpinner /> {status.title}
				</h5>
				<span>{status.text}</span>
			</div>
			<div>
				<Line percent={status.percent} strokeWidth={0.5} trailWidth={0.5} strokeColor="#254b6d" />
			</div>
			{failed && (
				<div className="cancel-container">
					<div>
						<Icon kind={'report-problem-circle'} />
						<h5 className="feil-status-text">
							Dette tar lengre tid enn forventet. Hvis bestillingen er kompleks kan du gi Dolly litt
							mer tid før du eventuelt avbryter.
						</h5>
					</div>
					<NavButton type="fare" onClick={_onCancelBtn}>
						Avbryt bestilling
					</NavButton>
				</div>
			)}
		</Fragment>
	)
}
