import React, { Component, Fragment } from 'react'
import PropTypes from 'prop-types'
import Knapp from 'nav-frontend-knapper'
import HjelpeTekst from 'nav-frontend-hjelpetekst'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import RedigerTeamConnector from '~/components/RedigerTeam/RedigerTeamConnector'
import Loading from '~/components/ui/loading/Loading'
import Toolbar from '~/components/ui/toolbar/Toolbar'
import TeamListe from './TeamListe'
import SearchFieldConnector from '~/components/searchField/SearchFieldConnector'

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
		const { teamListe, teams, history, startOpprettTeam, searchActive, isFetching } = this.props
		const { visning, visOpprettTeam } = teams

		return (
			<div className="oversikt-container">
				<div className="page-header flexbox--align-center--justify-start">
					<Overskrift label="Teams" />
					<HjelpeTekst>Med teams kan du og kolleger dele testdatagrupper.</HjelpeTekst>
				</div>
				<Toolbar
					toggleOnChange={this.handleViewChange}
					toggleCurrent={visning}
					searchField={<SearchFieldConnector />}
				>
					<Knapp type="hoved" onClick={startOpprettTeam}>
						Nytt team
					</Knapp>
				</Toolbar>
				{visOpprettTeam && <RedigerTeamConnector />}

				<TeamListe
					isFetching={isFetching}
					teams={teamListe}
					history={history}
					searchActive={searchActive}
				/>
			</div>
		)
	}
}
