import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { NavLink } from 'react-router-dom'

export default class Breadcrumb extends PureComponent {
	static propTypes = {}

	render() {
		return <div className="breadcrumb">Gruppeoversikt / gruppenavn / legg til personer</div>
	}
}
