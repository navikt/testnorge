import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Tabs from 'nav-frontend-tabs'
import TeamOversikt from './TeamOversikt/TeamOversikt'
import Overskrift from '~/components/overskrift/Overskrift'

import './Profil.less'

export default class ProfilPage extends Component {
	static propTypes = {}

	componentDidMount() {
		this.props.fetchTeams()
	}

	handleTabsChange = (e, idx) => this.props.setActivePage(idx)

	handleViewChange = e => {
		this.props.setTeamVisning(e.target.value)
		this.props.fetchTeams()
	}

	render() {
		const { teams, createTeam, history } = this.props

		return (
			<div>
				<Overskrift label="Min profil" />

				<Tabs tabs={[{ label: 'Teams' }]} onChange={this.handleTabsChange} />

				{teams.activePage === 0 && (
					<TeamOversikt
						teams={teams}
						handleViewChange={this.handleViewChange}
						createTeam={createTeam}
						history={history}
					/>
				)}
			</div>
		)
	}
}
