import React, { Fragment } from 'react'
import Formatters from '~/utils/DataFormatter'
import ApiFeilmelding from '~/components/ui/apiFeilmelding/ApiFeilmelding'

export default function Fagsystemfeil({ fagsystemFeil }) {
	if (fagsystemFeil.length <= 0) return false

	return (
		<Fragment>
			<div className="feilmelding_tabell-header">
				<h2>Feilmelding</h2>
				<h2>Miljø</h2>
				<h2>Ident</h2>
			</div>
			{fagsystemFeil.map((status, idx) => (
				<div className="feilmelding_fagsystem" key={idx}>
					<h5>{status.navn}</h5>
					<div className="feilmelding_fagsystem-container">
						<div>
							<ApiFeilmelding feilmelding={status.melding} />
						</div>
						<div>
							{status.identer.map(({ miljo, identer }, idx) => (
								<div className="flexbox" key={idx}>
									<div>{miljo || <i>N/A</i>}</div>
									<div>{Formatters.arrayToString(identer)}</div>
								</div>
							))}
						</div>
					</div>
				</div>
			))}
		</Fragment>
	)
}
