import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { ToggleGruppe, ToggleKnapp } from 'nav-frontend-skjema'

import './Toolbar.less'

export default class Toolbar extends PureComponent {
	static propTypes = {
		toggleOnChange: PropTypes.func,
		toggleCurrent: PropTypes.string,
		searchField: PropTypes.func,
		title: PropTypes.string
	}

	renderToggle = () => (
		<ToggleGruppe onChange={this.props.toggleOnChange} name="toggler">
			<ToggleKnapp value="mine" checked={this.props.toggleCurrent === 'mine'}>
				Mine
			</ToggleKnapp>
			<ToggleKnapp value="alle" checked={this.props.toggleCurrent === 'alle'}>
				Alle
			</ToggleKnapp>
		</ToggleGruppe>
	)

	render() {
		const { title, toggleOnChange, children } = this.props
		const SearchField = this.props.searchField
		return (
			<div className="toolbar">
				{title && <h2>{title}</h2>}
				{toggleOnChange && this.renderToggle()}
				{SearchField && <SearchField />}
				<div className="toolbar--actions">{children}</div>
			</div>
		)
	}
}
