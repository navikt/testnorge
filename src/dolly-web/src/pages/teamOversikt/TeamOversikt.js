import React, { Component } from 'react'
import PropTypes from 'prop-types'
import RedigerTeamConnector from './RedigerTeam/RedigerTeamConnector'
import MainTableContainer from '~/components/mainTableContainer/MainTableContainer'
import TeamListe from './TeamListe'

export default class TeamOversikt extends Component {
	static propTypes = {
		teams: PropTypes.object
	}

	componentDidMount() {
		this.props.fetchTeams()
	}

	handleViewChange = e => {
		this.props.setTeamVisning(e.target.value)
		this.props.fetchTeams()
	}

	render() {
		const { teams, history, startOpprettTeam, startRedigerTeam } = this.props
		const { items, fetching, visning, visOpprettTeam, editTeamId } = teams

		const listComponent = (
			<TeamListe
				items={items}
				fetching={fetching}
				history={history}
				startRedigerTeam={startRedigerTeam}
				editTeamId={editTeamId}
			/>
		)
		const createTeamComponent = <RedigerTeamConnector />

		return (
			<MainTableContainer
				headerLabel="Teams"
				startOpprettTeam={startOpprettTeam}
				visning={visning}
				visOpprettTeam={visOpprettTeam}
				byttVisning={this.handleViewChange}
				createTeamComponent={createTeamComponent}
				listComponent={listComponent}
			/>
		)
	}
}
