import Icon from '@/components/ui/icon/Icon'
import { Status } from '@/components/bestilling/sammendrag/miljoeStatus/MiljoeStatus'
import Spinner from '@/components/ui/loading/Spinner'
import * as React from 'react'
import ApiFeilmelding from '@/components/ui/apiFeilmelding/ApiFeilmelding'
import styled from 'styled-components'

const FagsystemStatus = styled.div`
	display: flex;
	align-items: center;
`

const StatusIcon = styled.div`
	min-width: 24px;
	margin-right: 7px;
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
		oppretter: 'loading-spinner',
		suksess: 'feedback-check-circle',
		avvik: 'report-problem-circle',
		feil: 'report-problem-triangle',
	}

	const iconType = (statuser: Status[], feil: string) => {
		if (feil) {
			return IconTypes.feil
		}
		// Alle statuser er OK
		if (statuser.every((status) => status.melding === 'OK')) {
			return IconTypes.suksess
		}
		// Denne statusmeldingen gir kun avvik
		else if (
			statuser.some(
				(status) =>
					status?.melding?.toLowerCase()?.includes('tidsavbrudd') ||
					status?.melding?.toLowerCase()?.includes('avvik'),
			)
		) {
			return IconTypes.avvik
		}
		// Avvik eller error
		return statuser.some((status) => status?.melding === 'OK') ? IconTypes.avvik : IconTypes.feil
	}

	return (
		<div style={{ marginTop: '15px' }}>
			{bestilling?.status?.map((fagsystem, idx) => {
				const oppretter = fagsystem?.statuser?.some((status) => status?.melding?.includes('Info'))

				const infoString = ['Info', 'INFO', 'info']
				const infoListe = fagsystem?.statuser?.filter((s) =>
					infoString.some((i) => s?.melding?.includes(i)),
				)

				const advarselString = ['Advarsel', 'ADVARSEL', 'advarsel']
				const advarselListe = fagsystem?.statuser?.filter((s) =>
					advarselString.some((i) => s?.melding?.includes(i)),
				)

				const feilString = ['Feil', 'FEIL', 'feil']
				const feilListe = fagsystem?.statuser?.filter((s) =>
					feilString.some((i) => s?.melding?.includes(i)),
				)

				const getMelding = () => {
					if (fagsystem?.statuser?.every((s) => s?.melding === 'OK')) {
						return null
					} else {
						return infoListe.concat(advarselListe, feilListe)
					}
				}

				// @ts-ignore
				const marginBottom = getMelding()?.length > 0 ? '8px' : '15px'

				const antallBestilteIdenter = erOrganisasjon ? 1 : bestilling?.antallIdenter

				const getOkIdenter = () => {
					const miljouavhengig = fagsystem?.statuser?.find((s) => s?.melding === 'OK')?.identer
					const miljoavhengig = fagsystem?.statuser?.find((s) => s?.melding === 'OK')?.detaljert
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

				return (
					<FagsystemStatus key={idx} style={{ alignItems: 'flex-start' }}>
						<StatusIcon>
							{oppretter ? (
								<Spinner size={23} margin="0px" />
							) : (
								<Icon kind={iconType(fagsystem.statuser, bestilling.feil)} />
							)}
						</StatusIcon>
						<div style={{ width: '96%', marginBottom: marginBottom }}>
							<FagsystemText>
								<h5>{fagsystem.navn}</h5>
								{fagsystem.id !== 'ANNEN_FEIL' && !erOrganisasjon && (
									<p>
										{getOkIdenter()?.length}{' '}
										{!bestilling.opprettetFraGruppeId && `av ${antallBestilteIdenter} `}identer
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
