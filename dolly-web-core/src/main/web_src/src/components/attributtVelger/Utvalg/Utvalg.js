import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { AttributtManager } from '~/service/Kodeverk'
import LinkButton from '~/components/ui/button/LinkButton/LinkButton'

import './Utvalg.less'

export default class Utvalg extends PureComponent {
	static propTypes = {
		selectedIds: PropTypes.arrayOf(PropTypes.string)
	}

	constructor(props) {
		super(props)
		this.AttributtManager = new AttributtManager()
	}

	renderUtvalg = () => {
		const list = this.AttributtManager.listUtvalg(this.props.selectedIds)

		if (!list.length) return this.renderEmptyResult()

		return (
			<React.Fragment>
				{list.map(hovedKategori => this.renderHovedKategori(hovedKategori))}
				<LinkButton text="Fjern alle" onClick={this.props.uncheckAllAttributes} />
			</React.Fragment>
		)
	}

	renderHovedKategori = ({ hovedKategori, items }) => (
		<ul key={hovedKategori.navn}>
			<li>
				<span>{hovedKategori.navn}</span>
				<ul>{items.map(item => this.renderItem(item))}</ul>
			</li>
		</ul>
	)

	renderItem = item => (
		<li key={item.id}>
			<span>{item.label}</span>
		</li>
	)

	renderEmptyResult = () => <span className="utvalg--empty-result">Ingenting er valgt</span>

	render() {
		return (
			<div className="utvalg">
				<h2>Du har lagt til følgende egenskaper:</h2>
				{this.renderUtvalg()}
			</div>
		)
	}
}
