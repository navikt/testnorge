import React, { PureComponent } from 'react'

import './SoekMiniNorge.less'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'

const infoTekst =
	'Syntetiske testdata er tilgjengelig i Dolly gjennom tekniske APIer og Mini-Norge. Mini-Norge er NAVs syntetiske basispopulasjon med egenskaper tilsvarende normalen i Norge. Antall innbyggere er pr mars 2020 ca 200 000. Befolkningen er dynamisk og det gjøres løpende endringer – nye personer fødes, folk skifter jobb osv. Mini-Norge kan brukes av alle og er godt egnet til basistester, volumtester m.m. der det ikke er behov for spesialtilfeller.\n\n' +
	'Mini-Norge er kun tilgjengelig i Q2 som er et helsyntetisk testmiljø.\n\n' +
	'Beta-versjon for søk i Mini-Norge jobbes med fortløpende.'

export default class SoekMiniNorge extends PureComponent {
	render() {
		return (
			<div className="soek-container">
				<h1 className="page-header">Søk i Mini-Norge</h1>
				<h3 className="page-header">(beta)</h3>
				<AlertStripeInfo>{infoTekst}</AlertStripeInfo>
			</div>
		)
	}
}
