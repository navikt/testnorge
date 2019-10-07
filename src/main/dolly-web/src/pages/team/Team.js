import React, { Component } from 'react'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import Toolbar from '~/components/ui/toolbar/Toolbar'
import Knapp from 'nav-frontend-knapper'
import Loading from '~/components/ui/loading/Loading'
import LeggTilBruker from './LeggTilBruker/LeggTilBruker'
import ConfirmTooltip from '~/components/ui/confirmTooltip/ConfirmTooltip'
import RedigerTeamConnector from '~/components/RedigerTeam/RedigerTeamConnector'
import TeamMedlemmer from './teamMedlemmer/TeamMedlemmer'
import TeamGrupper from './teamGrupper/TeamGrupper'

import './Team.less'
export default class Team extends Component {
	state = {
		leggTilBruker: false
	}

	componentDidMount() {
		this.props.getTeam()
		this.props.listGrupper()
	}

	openLeggTilBrukerHandler = () => {
		this.setState({ leggTilBruker: true })
	}

	closeLeggTilBruker = () => {
		this.setState({ leggTilBruker: false })
	}

	render() {
		const {
			team,
			grupper,
			teamFetching,
			grupperFetching,
			history,
			addMember,
			removeMember,
			deleteTeam,
			startRedigerTeam,
			visRedigerTeam
		} = this.props

		if (!team || !grupper) return null

		const teamMembers = team.medlemmer.map(medlem => medlem.navIdent)

		const teamActions = [
			{
				icon: 'edit',
				label: 'REDIGER',
				onClick: startRedigerTeam
			}
		]

		return (
			<div className="oversikt-container">
				<Overskrift label={team.navn} actions={teamActions}>
					{this.props.isDeletingTeam ? (
						<Loading label="Sletter team" panel />
					) : (
						<ConfirmTooltip
							label="SLETT"
							className="flexbox--align-center"
							message={
								grupper.length > 0
									? 'Å slette dette teamet vil føre til sletting av ' +
									  grupper.length +
									  ' testdatagrupper . Er du sikker på dette?'
									: 'Vil du slette dette teamet?'
							}
							onClick={deleteTeam}
						/>
					)}
				</Overskrift>
				<div style={{ width: '70%' }} className="Beskrivelse">
					{team.beskrivelse}
				</div>
				{visRedigerTeam && <RedigerTeamConnector team={team} />}

				<Toolbar title="Medlemmer">
					<Knapp type="hoved" onClick={this.openLeggTilBrukerHandler}>
						Nytt medlem
					</Knapp>
				</Toolbar>

				{this.state.leggTilBruker && (
					<LeggTilBruker
						teamId={team.id}
						teamMembers={teamMembers}
						closeLeggTilBruker={this.closeLeggTilBruker}
						addMember={addMember}
					/>
				)}

				<TeamMedlemmer
					medlemmer={team.medlemmer}
					isFetching={teamFetching}
					removeMember={removeMember}
				/>

				<Overskrift label="Testdatagrupper" type="h2" />
				<TeamGrupper isFetching={grupperFetching} grupper={grupper} history={history} />
			</div>
		)
	}
}
