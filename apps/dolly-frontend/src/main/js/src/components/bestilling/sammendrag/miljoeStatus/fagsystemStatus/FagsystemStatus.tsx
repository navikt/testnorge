import React from 'react'
import ApiFeilmelding from '~/components/ui/apiFeilmelding/ApiFeilmelding'
import Icon from '~/components/ui/icon/Icon'

import './FagsystemStatus.less'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { Status } from '~/components/bestilling/sammendrag/miljoeStatus/MiljoeStatus'

export default function FagsystemStatus({ statusrapport }: { statusrapport: Status[] }) {
	if (statusrapport.length <= 0) return null

	// Feilmeldinger som skal ha gul problem-circle legges inn her
	const problemCircleFeil = ['InnvandringOpprettingsmelding: STATUS: TIDSAVBRUDD']

	const getIconType = (status: Status) => {
		const melding = status.melding
		if (melding && !melding.includes('OK')) {
			return problemCircleFeil.includes(melding) || (status.orgnummer && status.orgnummer !== 'NA')
				? 'report-problem-circle'
				: 'report-problem-triangle'
		} else {
			return 'feedback-check-circle'
		}
	}

	if (statusrapport && statusrapport.some((status) => status.id === 'ORGANISASJON_FORVALTER')) {
		return (
			<ErrorBoundary>
				<table className="fagsystemstatus">
					<thead>
						<tr>
							<td>Status</td>
							<td>Miljø</td>
							<td>Orgnr.</td>
						</tr>
					</thead>
					<tbody>
						{statusrapport.map((status, index) => (
							<tr key={index}>
								<td>
									<div className="flexbox">
										{status.melding !== 'Pågående' && status.melding !== 'Deployer' && (
											<Icon size={16} kind={getIconType(status)} />
										)}
										<div>
											<h5>{status.navn}</h5>
											<ApiFeilmelding feilmelding={status.melding} />
										</div>
									</div>
								</td>
								<td>{status.miljo || <i>Ikke relevant</i>}</td>
								<td>{status.orgnummer}</td>
							</tr>
						))}
					</tbody>
				</table>
			</ErrorBoundary>
		)
	}

	return (
		<ErrorBoundary>
			<table className="fagsystemstatus">
				<thead>
					<tr>
						<td>System</td>
						<td>Miljø</td>
						<td>Ident</td>
					</tr>
				</thead>
				<tbody>
					{statusrapport.map((status, index) => (
						<tr key={index}>
							<td>
								<div className="flexbox">
									<Icon size={16} kind={getIconType(status)} />
									<div>
										<h5>{status.navn}</h5>
										<ApiFeilmelding feilmelding={status.melding} />
									</div>
								</div>
							</td>
							<td>{status.miljo || <i>Ikke relevant</i>}</td>
							<td>
								<ul>
									{status.identer.map((ident, idx) => (
										<li key={idx}>{ident}</li>
									))}
								</ul>
							</td>
						</tr>
					))}
				</tbody>
			</table>
		</ErrorBoundary>
	)
}
