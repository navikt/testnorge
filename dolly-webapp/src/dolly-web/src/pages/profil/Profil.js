import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Tabs from 'nav-frontend-tabs'
import Overskrift from '~/components/overskrift/Overskrift'

export default class ProfilPage extends Component {
	static propTypes = {}

	render() {
		const { teams, createTeam, history } = this.props

		return (
			<div>
				<Overskrift label="Min profil" />
			</div>
		)
	}
}
