import React, { Component, Fragment } from 'react'
import PropTypes from 'prop-types'
import Panel from '~/components/panel/Panel'
import Input from '~/components/fields/Input/Input'
import Utvalg from './Utvalg/Utvalg'
import Checkbox from '~/components/fields/Checkbox/Checkbox'
import { AttributtManager } from '~/service/Kodeverk'

import './AttributtVelger.less'

export default class AttributtVelger extends Component {
	static propTypes = {
		onToggle: PropTypes.func.isRequired,
		selectedIds: PropTypes.arrayOf(PropTypes.string)
	}

	constructor(props) {
		super(props)
		this.AttributtManager = new AttributtManager()
	}

	state = {
		search: ''
	}

	searchOnChange = e => this.setState({ search: e.target.value })

	renderPanels = () => {
		const list = this.AttributtManager.listSelectableAttributes(this.state.search)
		if (list.length === 0) return this.renderEmptyResult()
		return list.map(hovedKategori => this.renderHovedKategori(hovedKategori))
	}

	renderHovedKategori = ({ hovedKategori, items }) => {
		const name = hovedKategori.navn
		return (
			<Panel key={name} heading={<h2>{name}</h2>} startOpen>
				<fieldset name={name}>
					<div className="attributt-velger_panelcontent">
						{items.map((subKategori, idx) => this.renderSubKategori(subKategori, idx))}
					</div>
				</fieldset>
			</Panel>
		)
	}

	renderSubKategori = ({ subKategori, items }, idx) => {
		return (
			<Fragment key={idx}>
				{subKategori && <h3>{subKategori.navn}</h3>}
				<fieldset name={subKategori.navn}>
					<div className="attributt-velger_panelsubcontent">
						{items.map(item => this.renderItem(item))}
					</div>
				</fieldset>
			</Fragment>
		)
	}

	renderItem = item => (
		<Checkbox
			key={item.id}
			label={item.label}
			id={item.id}
			checked={this.props.selectedIds.includes(item.id)}
			onChange={e => this.props.onToggle(e.target.id)}
		/>
	)

	renderEmptyResult = () => <p>Søket ga ingen treff</p>

	render() {
		return (
			<div className="attributt-velger">
				<Input
					label="Søk attributter"
					labelOffscreen
					placeholder="Søk attributter"
					className="attributt-velger_search"
					onChange={this.searchOnChange}
				/>

				<div className="flexbox">
					<div className="attributt-velger_panels">{this.renderPanels()}</div>

					<Utvalg selectedIds={this.props.selectedIds} />
				</div>
			</div>
		)
	}
}
