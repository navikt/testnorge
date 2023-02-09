import Icon from '@/components/ui/icon/Icon'

import '../BestillingResultat/FagsystemStatus/FagsystemStatus.less'
import { Miljostatus, Status } from '@/components/bestilling/sammendrag/miljoeStatus/MiljoeStatus'
import Spinner from '@/components/ui/loading/Spinner'
import * as React from 'react'

const statusTest = [
	{
		id: 'PDL_FORVALTER',
		navn: 'Persondataløsningen (PDL)',
		statuser: [
			{
				melding: 'OK',
				identer: ['03498146707'],
			},
		],
	},
	{
		id: 'PDL_PERSONSTATUS',
		navn: 'PDL-Synkronisering',
		statuser: [
			{
				melding: 'OK',
				identer: ['03498146707'],
			},
		],
	},
	{
		id: 'AAREG',
		navn: 'Arbeidsregister (AAREG)',
		statuser: [
			{
				melding: 'OK',
				detaljert: [
					{
						miljo: 'q1',
						identer: ['03498146707'],
					},
				],
			},
		],
	},
	{
		id: 'ARENA',
		navn: 'Arena fagsystem',
		statuser: [
			{
				melding: 'Info: Oppretting startet mot Arena ...',
				detaljert: [
					{
						miljo: 'q1',
						identer: ['03498146707'],
					},
				],
			},
		],
	},
	{
		id: 'UDISTUB',
		navn: 'Utlendingsdirektoratet (UDI)',
		statuser: [
			{
				melding: 'Info: Oppretting startet mot UdiStub ...',
				identer: ['03498146707'],
			},
		],
	},
	{
		id: 'INNTK',
		navn: 'Inntektskomponenten (INNTK)',
		statuser: [
			{
				melding: 'OK',
				identer: ['03498146707'],
			},
		],
	},
	{
		id: 'PEN_INNTEKT',
		navn: 'Pensjonsopptjening (POPP)',
		statuser: [
			{
				melding: 'OK',
				detaljert: [
					{
						miljo: 'q1',
						identer: ['03498146707'],
					},
				],
			},
		],
	},
	{
		id: 'TP_FORVALTER',
		navn: 'Tjenestepensjon (TP)',
		statuser: [
			{
				melding: 'OK',
				detaljert: [
					{
						miljo: 'q1',
						identer: ['03498146707'],
					},
				],
			},
		],
	},
	{
		id: 'PEN_FORVALTER',
		navn: 'Pensjon (PEN)',
		statuser: [
			{
				melding: 'OK',
				detaljert: [
					{
						miljo: 'q1',
						identer: ['03498146707'],
					},
					{
						miljo: 'q2',
						identer: ['03498146707'],
					},
				],
			},
		],
	},
	{
		id: 'PEN_AP',
		navn: 'Alderspensjon (AP)',
		statuser: [
			{
				melding: 'Feil:',
				detaljert: [
					{
						miljo: 'q1',
						identer: ['03498146707'],
					},
				],
			},
		],
	},
]
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
				// const oppretter = statusTest.some((status) => status?.melding?.includes('Info:'))
				return (
					<div className="fagsystem-status_kind" key={idx}>
						{oppretter ? (
							<Spinner size={19} />
						) : (
							<Icon kind={iconType(fagsystem.statuser, bestilling.feil)} />
							// <Icon kind={iconType(statusTest, bestilling.feil)} />
						)}
						<p>
							<b>{fagsystem.navn}</b>
						</p>
						<p> - {fagsystem.statuser[0].melding}</p>
					</div>
				)
			})}
		</div>
	)
}
