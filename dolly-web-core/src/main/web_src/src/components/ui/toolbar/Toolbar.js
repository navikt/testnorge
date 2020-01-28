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
				icon: <Icon size={14} kind="man2Light" className="toggleIcon" />
			},
			{
				value: 'alle',
				label: 'Alle',
				icon: <Icon size={16} kind="groupLight" className="toggleIcon" />
			}
		]
	}

	render() {
		const { title, toggleOnChange, children, searchField } = this.props
		if (this.props.toggleCurrent === 'personer' || this.props.toggleCurrent === 'mine') {
			var arrowPosition = 'positionA'
		} else if (this.props.toggleCurrent === 'bestilling' || this.props.toggleCurrent === 'alle') {
			arrowPosition = 'positionB'
		}

		return (
			<React.Fragment>
				<div className="toolbar">
					{title && <h2>{title}</h2>}
					<div className="toolbar--actions">{children}</div>
					{toggleOnChange && this._renderToggle()}
					{searchField}
				</div>
				<div className="toggleListLine">
					<span className={arrowPosition}></span>
				</div>
			</React.Fragment>
		)
	}

	_renderToggle = () => {
		const { toggleValues, toggleOnChange, toggleCurrent } = this.props

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
