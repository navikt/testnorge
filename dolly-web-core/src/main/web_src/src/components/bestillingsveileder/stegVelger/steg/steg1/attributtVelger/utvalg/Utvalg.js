import React from 'react'
import './Utvalg.less'

export const Utvalg = ({ checked }) => {
	const renderPanel = ({ label, values }, idx) => (
		<ul key={idx}>
			<li>
				<span>{label}</span>
				<ul>{values.map(renderItem)}</ul>
			</li>
		</ul>
	)

	const renderItem = (item, idx) => (
		<li key={idx}>
			<span>{item}</span>
		</li>
	)

	const renderEmptyResult = () => <span className="utvalg--empty-result">Ingenting er valgt</span>

	return (
		<div className="utvalg-container">
			<div className="utvalg">
				<h2>Du har lagt til følgende egenskaper:</h2>
				{checked.length ? checked.map(renderPanel) : renderEmptyResult()}
			</div>
		</div>
	)
}
