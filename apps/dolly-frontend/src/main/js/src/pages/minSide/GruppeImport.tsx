import React from 'react'
import ImporterGrupperConnector from '../gruppeOversikt/ImporterGrupperConnector'

export default function GruppeImport() {
	return (
		<div className="gruppeimport">
			<hr />
			<div className="flexbox--space">
				<div>
					<h2>Import av grupper</h2>
					<p>Savner du gruppene fra dine tidligere Z-identer? Da kan du importere dem her.</p>
				</div>
				<ImporterGrupperConnector />
			</div>
		</div>
	)
}
