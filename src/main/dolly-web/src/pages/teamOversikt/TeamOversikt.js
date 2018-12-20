import React, { Component, Fragment } from 'react'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import RedigerTeamConnector from '~/components/RedigerTeam/RedigerTeamConnector'
import Loading from '~/components/loading/Loading'
import Toolbar from '~/components/toolbar/Toolbar'
import Knapp from 'nav-frontend-knapper'
import TeamListe from './TeamListe'
import SearchFieldConnector from '~/components/searchField/SearchFieldConnector'
import ContentTooltip from '~/components/contentTooltip/ContentTooltip'
import PaginationConnector from '~/components/pagination/PaginationConnector'

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
		const {
			teamListe,
			teams,
			history,
			startOpprettTeam,
			startRedigerTeam,
			deleteTeam,
			searchActive,
			isFetching
		} = this.props
		const { visning, visOpprettTeam, editTeamId } = teams

		return (
			<div className="oversikt-container">
				<div className="page-header flexbox--align-center--justify-start">
					<Overskrift label="Teams" />
					<ContentTooltip>Med teams kan du og kolleger dele testdatagrupper.</ContentTooltip>
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
				{isFetching ? (
					<Loading label="laster teams" panel />
				) : (
					<PaginationConnector
						items={teamListe}
						render={items => (
							<TeamListe
								items={items}
								history={history}
								startRedigerTeam={startRedigerTeam}
								editTeamId={editTeamId}
								deleteTeam={deleteTeam}
								searchActive={searchActive}
							/>
						)}
					/>
				)}
			</div>
		)
	}
}
