import Icon from '@/components/ui/icon/Icon'

import '../BestillingResultat/FagsystemStatus/FagsystemStatus.less'
import { Miljostatus, Status } from '@/components/bestilling/sammendrag/miljoeStatus/MiljoeStatus'
import Spinner from '@/components/ui/loading/Spinner'
import * as React from 'react'
import ApiFeilmelding from '@/components/ui/apiFeilmelding/ApiFeilmelding'
import styled from 'styled-components'

const StatusIcon = styled.div`
	min-width: 24px;
	margin-right: 7px;

	&& {
		.svg-icon {
			margin-right: 0;
		}
	}
`

export const BestillingStatus = ({ bestilling }: Miljostatus) => {
	const IconTypes = {
		oppretter: 'loading-spinner',
		suksess: 'feedback-check-circle',
		avvik: 'report-problem-circle',
		feil: 'report-problem-triangle',
	}

	const iconType = (statuser: Status[], feil: string) => {
		if (feil) {
			return IconTypes.feil
		}
		// Alle er OK
		if (statuser.every((status) => status.melding === 'OK')) {
			return IconTypes.suksess
		}
		// Denne statusmeldingen gir kun avvik
		else if (
			statuser.some(
				(status) =>
					status?.melding?.includes('TIDSAVBRUDD') ||
					status?.melding?.includes('Tidsavbrudd') ||
					status?.melding?.includes('tidsavbrudd')
			)
		) {
			return IconTypes.avvik
		}
		// Avvik eller Error
		return statuser.some((status) => status?.melding === 'OK') ? IconTypes.avvik : IconTypes.feil
	}

	return (
		<div className="fagsystem-status">
			{bestilling.status?.map((fagsystem, idx) => {
				const oppretter = fagsystem?.statuser?.some((status) => status?.melding?.includes('Info:'))
				console.log('fagsystem: ', fagsystem) //TODO - SLETT MEG

				const infoListe = fagsystem?.statuser?.filter((s) => s?.melding?.includes('Info:'))
				const feilListe = fagsystem?.statuser?.filter((s) => s?.melding?.includes('Feil'))
				const getMelding = () => {
					if (fagsystem?.statuser?.every((s) => s?.melding === 'OK')) {
						return null
						// } else if (fagsystem?.statuser?.some((s) => s?.melding?.includes('Info:'))) {
					} else {
						return infoListe.concat(feilListe)
					}
				}

				//TODO fortsett her!
				console.log('getMelding(): ', getMelding()) //TODO - SLETT MEG

				const marginBottom = getMelding() ? '8px' : '15px'

				return (
					<div className="fagsystem-status_kind" key={idx} style={{ alignItems: 'flex-start' }}>
						<StatusIcon>
							{oppretter ? (
								<Spinner size={23} margin="0px" />
							) : (
								<Icon kind={iconType(fagsystem.statuser, bestilling.feil)} />
							)}
						</StatusIcon>
						<div style={{ marginBottom: marginBottom, paddingTop: '5px', maxWidth: '96%' }}>
							<h5 style={{ fontSize: '0.9em' }}>{fagsystem.navn}</h5>
							{getMelding()?.map((status) => {
								return <ApiFeilmelding feilmelding={status?.melding} />
							})}
							{/*<ApiFeilmelding feilmelding={fagsystem.statuser[0].melding} />*/}
						</div>
						{/*<p>*/}
						{/*	<b>{fagsystem.navn}</b>*/}
						{/*</p>*/}
						{/*<p> - {fagsystem.statuser[0].melding}</p>*/}
					</div>
				)
			})}
			{bestilling?.status?.length > 0 && <hr style={{ marginBottom: '15px' }} />}
		</div>
	)
}
