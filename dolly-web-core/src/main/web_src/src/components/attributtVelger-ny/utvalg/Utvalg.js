import React from 'react'
import { getAttributterForUtvalgListe } from '~/service/Attributter'
import LinkButton from '~/components/ui/button/LinkButton/LinkButton'

import './Utvalg.less'

export const Utvalg = ({ valgteAttributter, checkAttributter }) => {
	const utvalgListe = getAttributterForUtvalgListe(valgteAttributter)
	const attrValues = Object.keys(valgteAttributter).filter(key => valgteAttributter[key])

	const renderUtvalg = () => {
		if (!utvalgListe.length) return renderEmptyResult()

		return (
			<React.Fragment>
				{utvalgListe.map(panel => renderPanel(panel))}
				<LinkButton text="Fjern alle" onClick={() => checkAttributter(attrValues, false)} />
			</React.Fragment>
		)
	}

	const renderPanel = ({ panel, values }) => (
		<ul key={panel}>
			<li>
				<span>{panel}</span>
				<ul>{values.map(item => renderItem(item))}</ul>
			</li>
		</ul>
	)

	const renderItem = item => (
		<li key={item.name}>
			<span>{item.label}</span>
		</li>
	)

	const renderEmptyResult = () => <span className="utvalg--empty-result">Ingenting er valgt</span>

	return (
		<div className="utvalg-container">
			<div className="utvalg">
				<h2>Du har lagt til følgende egenskaper:</h2>
				{renderUtvalg()}
			</div>
		</div>
	)
}
