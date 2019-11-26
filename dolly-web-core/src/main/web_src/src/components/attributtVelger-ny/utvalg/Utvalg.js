import React from 'react'
import { getAttributterForUtvalgListe } from '~/service/attributter/Attributter'
import LinkButton from '~/components/ui/button/LinkButton/LinkButton'

import './Utvalg.less'

export const Utvalg = ({ valgteAttributter, checkAttributter }) => {
	const utvalgListe = getAttributterForUtvalgListe(valgteAttributter)

	const attrsToFalse = Object.keys(valgteAttributter)
		.filter(key => valgteAttributter[key])
		.reduce((acc, curr) => ({ ...acc, [curr]: false }), {})

	const renderUtvalg = () => {
		if (!utvalgListe.length) return renderEmptyResult()

		return (
			<React.Fragment>
				{utvalgListe.map(renderPanel)}
				<LinkButton text="Fjern alle" onClick={() => checkAttributter(attrsToFalse)} />
			</React.Fragment>
		)
	}

	const renderPanel = ({ panel, values }) => (
		<ul key={panel.id}>
			<li>
				<span>{panel.label}</span>
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
