import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { ToggleGruppe, ToggleKnapp } from 'nav-frontend-skjema'

import './Toolbar.less'

export default class Toolbar extends PureComponent {
	static propTypes = {
		toggleOnChange: PropTypes.func,
		toggleCurrent: PropTypes.string,
		searchField: PropTypes.node,
		title: PropTypes.string
	}
	static defaultProps = {
		toggleValues: [{ value: 'mine', label: 'Mine' }, { value: 'alle', label: 'Alle' }]
	}

	render() {
		const { title, toggleOnChange, children, searchField } = this.props
		return (
			<div className="toolbar">
				{title && <h2>{title}</h2>}
				{toggleOnChange && this._renderToggle()}
				{searchField}
				<div className="toolbar--actions">{children}</div>
			</div>
		)
	}

	_renderToggle = () => {
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
}
