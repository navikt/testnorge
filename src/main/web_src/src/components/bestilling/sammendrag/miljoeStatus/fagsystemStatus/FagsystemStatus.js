import React from 'react'
import ApiFeilmelding from '~/components/ui/apiFeilmelding/ApiFeilmelding'
import Icon from '~/components/ui/icon/Icon'

import './FagsystemStatus.less'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export default function FagsystemStatus({ statusrapport }) {
	if (statusrapport.length <= 0) return false

	// Feilmeldinger som skal ha gul problem-circle legges inn her
	const problemCircleFeil = ['InnvandringOpprettingsmelding: STATUS: TIDSAVBRUDD']

	const getIconType = melding =>
		melding && !melding.includes('OK')
			? problemCircleFeil.includes(melding)
				? 'report-problem-circle'
				: 'report-problem-triangle'
			: 'feedback-check-circle'

	if (statusrapport.organisasjonsnummer) {
		const melding = statusrapport.melding
			? statusrapport.melding
			: 'NA:Status inneholdt ingen melding'
		return (
			<ErrorBoundary>
				<table className="fagsystemstatus">
					<thead>
						<tr>
							<td>Status</td>
							<td>Miljø</td>
						</tr>
					</thead>
					<tbody>
						{melding.split(',').map((fullStatus, idx) => {
							const [miljo, statuskode] = fullStatus.split(':')
							return (
								<tr key={idx}>
									<td>
										<div className="flexbox">
											<Icon size={16} kind={getIconType(statuskode)} />
											<div>
												<h5>Organisasjon Forvalter</h5>
												<ApiFeilmelding feilmelding={statuskode} />
											</div>
										</div>
									</td>
									<td>{miljo.toUpperCase() || <i>Ikke relevant</i>}</td>
								</tr>
							)
						})}
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
					{statusrapport.map((status, idx) => (
						<tr key={idx}>
							<td>
								<div className="flexbox">
									<Icon size={16} kind={getIconType(status.melding)} />
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
