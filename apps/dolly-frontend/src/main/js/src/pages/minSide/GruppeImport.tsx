import React from 'react'
import ImporterGrupperConnector from '../gruppeOversikt/ImporterGrupperConnector'

export default function GruppeImport() {
	return (
		<div className="gruppeimport">
			<hr />
			<div className="flexbox--space">
				<div>
					<h2>Import av testdatagrupper</h2>
					<p>
						Savner du testdatagruppene fra dine tidligere Z-identer? Da kan du importere dem her.
					</p>
				</div>
				<ImporterGrupperConnector />
			</div>
		</div>
	)
}
