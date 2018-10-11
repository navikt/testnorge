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
	static defaultProps = {
		toggleValues: [{ value: 'mine', label: 'Mine' }, { value: 'alle', label: 'Alle' }]
	}

	renderToggle = () => {
		const { toggleValues, toggleOnChange, toggleCurrent } = this.props

		return (
			<ToggleGruppe onChange={toggleOnChange} name="toggler">
				{toggleValues.map(val => (
					<ToggleKnapp key={val.value} value={val.value} checked={toggleCurrent === val.value}>
						{val.label}
					</ToggleKnapp>
				))}
			</ToggleGruppe>
		)
	}

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
