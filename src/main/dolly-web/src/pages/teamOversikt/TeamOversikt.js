import React, { Component, Fragment } from 'react'
import PropTypes from 'prop-types'
import { ToggleGruppe, ToggleKnapp } from 'nav-frontend-skjema'
import Overskrift from '~/components/overskrift/Overskrift'
import Input from '~/components/fields/Input/Input'
import RedigerTeamConnector from '~/components/RedigerTeam/RedigerTeamConnector'
import Loading from '~/components/loading/Loading'
import TeamListe from './TeamListe'
import AddButton from '~/components/button/AddButton'
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
		const { teamListe, teams, history, startOpprettTeam, startRedigerTeam, deleteTeam } = this.props
		const { fetching, visning, visOpprettTeam, editTeamId } = teams

		return (
			<div className="oversikt-container">
				<div className="page-header flexbox--space">
					<Overskrift label="Teams" />
				</div>

				<div className="flexbox--space">
					<ToggleGruppe onChange={this.handleViewChange} name="toggleGruppe">
						<ToggleKnapp value="mine" checked={visning === 'mine'} key="1">
							Mine
						</ToggleKnapp>
						<ToggleKnapp value="alle" checked={visning === 'alle'} key="2">
							Alle
						</ToggleKnapp>
					</ToggleGruppe>
					<SearchFieldConnector />
				</div>
				{visOpprettTeam && <RedigerTeamConnector />}
				{fetching ? (
					<Loading label="laster teams" panel />
				) : (
					<TeamListe
						items={teamListe}
						fetching={fetching}
						history={history}
						startRedigerTeam={startRedigerTeam}
						editTeamId={editTeamId}
						deleteTeam={deleteTeam}
					/>
				)}

				<AddButton title="Opprett nytt team" onClick={startOpprettTeam} />
			</div>
		)
	}
}
