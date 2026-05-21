import Icon from '@/components/ui/icon/Icon'
import { Status } from '@/components/bestilling/sammendrag/miljoeStatus/MiljoeStatus'
import Spinner from '@/components/ui/loading/Spinner'
import * as React from 'react'
import ApiFeilmelding from '@/components/ui/apiFeilmelding/ApiFeilmelding'
import styled from 'styled-components'
import { sortFagsystemer } from '@/components/bestilling/statusListe/BestillingProgresjon/fagsystemUtils'

const FagsystemStatus = styled.div`
	display: flex;
	align-items: center;
`

const StatusIcon = styled.div`
	width: 24px;
	height: 24px;
	min-width: 24px;
	margin-right: 7px;
	display: flex;
	align-items: center;
	justify-content: center;
`

const FagsystemText = styled.div`
	padding-top: 5px;
	max-width: 96%;
	display: flex;
	flex-wrap: wrap;

	h5 {
		font-size: 1em;
	}

	p {
		margin: 0 0 0 10px;
		font-size: 1em;
	}
`

export const BestillingStatus = ({
	bestilling,
	erOrganisasjon = false,
}: {
	bestilling: any
	erOrganisasjon?: boolean
}) => {
	const IconTypes = {
		oppretter: 'loading-circle',
		suksess: 'feedback-check-circle',
		avvik: 'report-problem-circle',
		feil: 'report-problem-triangle',
	}

	const iconType = (
		statuser: Status[],
		feil: string,
		ferdig: boolean,
		okIdenterCount?: number,
		totalIdenter?: number,
	) => {
		if (feil) {
			return IconTypes.feil
		}
		if (!statuser?.length || (erOrganisasjon && !ferdig)) {
			return IconTypes.oppretter
		}
		if (statuser.every((status) => status.melding === 'OK')) {
			if (totalIdenter && totalIdenter > 1 && okIdenterCount < totalIdenter) {
				return IconTypes.avvik
			}
			return IconTypes.suksess
		} else if (
			statuser.some(
				(status) =>
					status?.melding?.includes('RUNNING') ||
					status?.melding?.includes('PENDING_COMPLETE') ||
					status?.melding?.includes('ADDING_TO_QUEUE') ||
					status?.melding?.includes('Deployer') ||
					status?.melding?.includes('Pågående'),
			)
		) {
			return IconTypes.oppretter
		} else if (
			statuser.some(
				(status) =>
					status?.melding?.toLowerCase()?.includes('tidsavbrudd') ||
					status?.melding?.toLowerCase()?.includes('avvik'),
			)
		) {
			return IconTypes.avvik
		}
		return statuser.some((status) => status?.melding === 'OK') ? IconTypes.avvik : IconTypes.feil
	}

	return (
		<div style={{ marginTop: '15px' }}>
			{sortFagsystemer(bestilling?.status || []).map((fagsystem, idx) => {
				const statuser = fagsystem?.statuser || []

				const antallBestilteIdenter = bestilling?.antallIdenter

				const getOkIdenter = () => {
					const miljouavhengig = statuser.find((s) => s?.melding === 'OK')?.identer
					const miljoavhengig = statuser.find((s) => s?.melding === 'OK')?.detaljert
					if (miljouavhengig) {
						return miljouavhengig.filter((ident) => ident)
					}
					if (miljoavhengig) {
						return [...new Set(miljoavhengig.flatMap((miljo) => miljo?.identer))]?.filter(
							(ident) => ident,
						)
					}
					return []
				}

				const isGjenopprett =
					!!bestilling.opprettetFraGruppeId ||
					!!bestilling.opprettetFraId ||
					!!bestilling.gjenopprettetFraIdent

				const oppretter =
					(erOrganisasjon && !bestilling.ferdig) ||
					!statuser.length ||
					statuser.some((status) => {
						return (
							status?.melding?.includes('Info') ||
							status?.melding?.includes('ADDING_TO_QUEUE') ||
							status?.melding?.includes('RUNNING') ||
							status?.melding?.includes('PENDING_COMPLETE') ||
							status?.melding?.includes('Deployer') ||
							status?.melding?.includes('Pågående')
						)
					})

				const getMelding = () => {
					if (statuser.every((s) => s?.melding === 'OK')) {
						return null
					}
					const transientKeywords = ['ADDING_TO_QUEUE', 'RUNNING', 'PENDING_COMPLETE']
					return statuser.filter((s) => {
						if (!s?.melding || s.melding === 'OK') return false
						return !transientKeywords.some((kw) => s.melding.includes(kw))
					})
				}

				// @ts-ignore
				const marginBottom = getMelding()?.length > 0 ? '8px' : '15px'

				return (
					<FagsystemStatus key={idx} style={{ alignItems: 'flex-start' }}>
						<StatusIcon>
							{oppretter ? (
								<Spinner size={24} margin="0px" />
							) : (
								<Icon
									kind={iconType(
										statuser,
										bestilling.feil,
										bestilling.ferdig,
										getOkIdenter().length,
										isGjenopprett ? undefined : antallBestilteIdenter,
									)}
								/>
							)}
						</StatusIcon>
						<div style={{ width: '96%', marginBottom: marginBottom }}>
							<FagsystemText>
								<h5>{fagsystem.navn}</h5>
								{fagsystem.id !== 'ANNEN_FEIL' && !erOrganisasjon && (
									<p>
										{getOkIdenter()?.length}{' '}
										{!isGjenopprett && `av ${antallBestilteIdenter} `}identer
										opprettet
									</p>
								)}
							</FagsystemText>
							{getMelding()?.map((status, idx) => {
								return <ApiFeilmelding feilmelding={status?.melding} key={`Feilmelding-${idx}`} />
							})}
						</div>
					</FagsystemStatus>
				)
			})}
			{bestilling?.status?.length > 0 && <hr style={{ marginBottom: '15px' }} />}
		</div>
	)
}
