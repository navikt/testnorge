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

	const ERROR_KEYWORDS = ['feil', 'error']
	const WARNING_KEYWORDS = ['avvik', 'advarsel', 'warning']

	const isErrorMessage = (melding: string) =>
		ERROR_KEYWORDS.some((kw) => melding.toLowerCase().includes(kw))

	const isWarningMessage = (melding: string) =>
		WARNING_KEYWORDS.some((kw) => melding.toLowerCase().includes(kw))

	const isNonOkMessage = (melding: string) => isErrorMessage(melding) || isWarningMessage(melding)

	const getOkIdentsFromStatuser = (statuser: Status[]) => {
		const okStatus = statuser.find((s) => s?.melding === 'OK')
		if (okStatus?.identer) {
			return okStatus.identer.filter((ident) => ident)
		}
		if (okStatus?.detaljert) {
			return [...new Set(okStatus.detaljert.flatMap((miljo) => miljo?.identer))]?.filter(
				(ident) => ident,
			)
		}
		return []
	}

	const getEnvironments = (statuser: Status[]) => {
		const envs = new Set<string>()
		statuser.forEach((s) => {
			s?.detaljert?.forEach((d) => {
				if (d?.miljo) envs.add(d.miljo.toUpperCase())
			})
		})
		return envs.size > 0 ? Array.from(envs).sort().join(', ') : null
	}

	const getErrorIdents = (status: Status) => {
		if (status?.identer) {
			return status.identer.filter((ident) => ident)
		}
		if (status?.detaljert) {
			return [...new Set(status.detaljert.flatMap((d) => d?.identer))]?.filter((ident) => ident)
		}
		return []
	}

	return (
		<div style={{ marginTop: '15px' }}>
			{sortFagsystemer(bestilling?.status || []).map((fagsystem, idx) => {
				const statuser = fagsystem?.statuser || []
				const antallBestilteIdenter = bestilling?.antallIdenter

				const okStatuser = statuser.filter((s) => s?.melding === 'OK')
				const errorStatuser = statuser.filter((s) => s?.melding && s.melding !== 'OK')

				const isGjenopprett =
					!!bestilling.opprettetFraGruppeId ||
					!!bestilling.opprettetFraId ||
					!!bestilling.gjenopprettetFraIdent

				const okIdents = getOkIdentsFromStatuser(statuser)
				const okEnv = getEnvironments(okStatuser)

				const isInProgress =
					(erOrganisasjon && !bestilling.ferdig) ||
					!statuser.length ||
					(!errorStatuser.length &&
						((!bestilling.ferdig &&
							antallBestilteIdenter > 1 &&
							okStatuser.length > 0 &&
							okIdents.length < antallBestilteIdenter) ||
							(!okStatuser.length && !bestilling.ferdig)))

				if (isInProgress) {
					return (
						<FagsystemStatus key={idx} style={{ alignItems: 'flex-start' }}>
							<StatusIcon>
								<Spinner size={24} margin="0px" />
							</StatusIcon>
							<div style={{ width: '96%', marginBottom: '15px' }}>
								<FagsystemText>
									<h5>{fagsystem.navn}</h5>
									{fagsystem.id !== 'ANNEN_FEIL' && !erOrganisasjon && okIdents.length > 0 && (
										<p>
											{okIdents.length} {!isGjenopprett && `av ${antallBestilteIdenter} `}identer
											opprettet
										</p>
									)}
								</FagsystemText>
							</div>
						</FagsystemStatus>
					)
				}

				const hasOk = okStatuser.length > 0
				const hasErrors = errorStatuser.length > 0
				const okStillProcessing =
					!bestilling.ferdig && antallBestilteIdenter > 1 && okIdents.length < antallBestilteIdenter

				return (
					<React.Fragment key={idx}>
						{hasOk && (
							<FagsystemStatus style={{ alignItems: 'flex-start' }}>
								<StatusIcon>
									{okStillProcessing ? (
										<Spinner size={24} margin="0px" />
									) : (
										<Icon kind={IconTypes.suksess} />
									)}
								</StatusIcon>
								<div style={{ width: '96%', marginBottom: hasErrors ? '8px' : '15px' }}>
									<FagsystemText>
										<h5>{fagsystem.navn}</h5>
										{fagsystem.id !== 'ANNEN_FEIL' && !erOrganisasjon && (
											<p>
												{okEnv && `${okEnv} \u00B7 `}
												{okIdents.length} {!isGjenopprett && `av ${antallBestilteIdenter} `}identer
												opprettet
											</p>
										)}
									</FagsystemText>
								</div>
							</FagsystemStatus>
						)}
						{hasErrors &&
							errorStatuser.map((status, errIdx) => {
								const errEnv = getEnvironments([status])
								const errIdents = getErrorIdents(status)
								const iconKind = isWarningMessage(status.melding) ? IconTypes.avvik : IconTypes.feil
								return (
									<FagsystemStatus
										key={`${idx}-err-${errIdx}`}
										style={{ alignItems: 'flex-start' }}
									>
										<StatusIcon>
											<Icon kind={iconKind} />
										</StatusIcon>
										<div style={{ width: '96%', marginBottom: '8px' }}>
											<FagsystemText>
												<h5>{fagsystem.navn}</h5>
												{errIdents.length > 0 && (
													<p>
														{errEnv && `${errEnv} \u00B7 `}
														{errIdents.length} {errIdents.length === 1 ? 'ident' : 'identer'} feilet
													</p>
												)}
												{!errIdents.length && errEnv && <p>{errEnv}</p>}
											</FagsystemText>
											<ApiFeilmelding feilmelding={status?.melding} />
										</div>
									</FagsystemStatus>
								)
							})}
						{!hasOk && !hasErrors && (
							<FagsystemStatus style={{ alignItems: 'flex-start' }}>
								<StatusIcon>
									<Icon kind={bestilling.feil ? IconTypes.feil : IconTypes.suksess} />
								</StatusIcon>
								<div style={{ width: '96%', marginBottom: '15px' }}>
									<FagsystemText>
										<h5>{fagsystem.navn}</h5>
									</FagsystemText>
								</div>
							</FagsystemStatus>
						)}
					</React.Fragment>
				)
			})}
			{bestilling?.status?.length > 0 && <hr style={{ marginBottom: '15px' }} />}
		</div>
	)
}
