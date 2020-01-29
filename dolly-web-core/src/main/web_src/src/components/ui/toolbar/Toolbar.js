import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { ToggleGruppe, ToggleKnapp } from 'nav-frontend-skjema'
import Icon from '~/components/ui/icon/Icon'

import './Toolbar.less'

export default class Toolbar extends PureComponent {
	static propTypes = {
		toggleOnChange: PropTypes.func,
		toggleCurrent: PropTypes.string,
		searchField: PropTypes.node,
		title: PropTypes.string
	}
	static defaultProps = {
		toggleValues: [
			{
				value: 'mine',
				label: 'Mine',
				icon: 'man2'
			},
			{
				value: 'alle',
				label: 'Alle',
				icon: 'group'
			}
		]
	}

	render() {
		const { title, toggleOnChange, children, searchField } = this.props
		return (
			<div className="toolbar">
				{title && <h2>{title}</h2>}
				<div className="toolbar--actions">{children}</div>
				{toggleOnChange && this._renderToggle()}
				{searchField}
			</div>
		)
	}

	_renderToggle = () => {
		const { toggleValues, toggleOnChange, toggleCurrent } = this.props
		if (toggleCurrent === 'mine') {
			toggleValues[0].icon = <Icon size={14} kind="man2Light" className="toggleIcon" />
			toggleValues[1].icon = <Icon size={16} kind="groupDark" className="toggleIcon" />
		} else if (toggleCurrent === 'alle') {
			toggleValues[0].icon = <Icon size={14} kind="man2" className="toggleIcon" />
			toggleValues[1].icon = <Icon size={16} kind="groupLight" className="toggleIcon" />
		}
		return (
			<ToggleGruppe onChange={toggleOnChange} name="toggler">
				{toggleValues.map(val => (
					<ToggleKnapp key={val.value} value={val.value} checked={toggleCurrent === val.value}>
						{val.icon}
						{val.label}
					</ToggleKnapp>
				))}
			</ToggleGruppe>
		)
	}
}
