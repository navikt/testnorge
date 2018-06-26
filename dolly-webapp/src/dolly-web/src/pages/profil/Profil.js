import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Tabs from 'nav-frontend-tabs'
import TeamOversikt from './TeamOversikt/TeamOversikt'
import Overskrift from '~/components/overskrift/Overskrift'

import './Profil.less'

export default class ProfilPage extends Component {
	static propTypes = {}

	state = {
		activePage: 0
	}

	handleTabsChange = (e, idx) => this.setState({ activePage: idx })

	render() {
		const { activePage } = this.state

		return (
			<div>
				<Overskrift label="Min profil" />

				<Tabs tabs={[{ label: 'Teams' }]} onChange={this.handleTabsChange} />

				{activePage === 0 && <TeamOversikt />}
			</div>
		)
	}
}
