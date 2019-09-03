import React, { Component, Fragment } from 'react'
import { TransitionGroup, CSSTransition } from 'react-transition-group'
import Overskrift from '~/components/overskrift/Overskrift'
import Table from '~/components/table/Table'
import Toolbar from '~/components/toolbar/Toolbar'
import Knapp from 'nav-frontend-knapper'
import Loading from '~/components/loading/Loading'
import LeggTilBruker from './LeggTilBruker/LeggTilBruker'
import ConfirmTooltip from '~/components/confirmTooltip/ConfirmTooltip'
import RedigerTeamConnector from '~/components/RedigerTeam/RedigerTeamConnector'
import PaginationConnector from '~/components/pagination/PaginationConnector'

import './Team.less'
class Team extends Component {
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
			visRedigerTeam,
			isCreateDelete
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

				{teamFetching ? (
					<Loading label="laster medlemmer" panel />
				) : (
					<Fragment>
						{this.state.leggTilBruker && (
							<LeggTilBruker
								teamId={team.id}
								teamMembers={teamMembers}
								closeLeggTilBruker={this.closeLeggTilBruker}
								addMember={addMember}
							/>
						)}
						<PaginationConnector
							items={team.medlemmer}
							render={items => (
								<Table>
									<Table.Header>
										<Table.Column width="30" value="Navn" />
										<Table.Column width="20" value="Rolle" />
										<Table.Column width="50" value="Slett" />
									</Table.Header>
									<TransitionGroup component={null}>
										{items.map(medlem => (
											<CSSTransition
												key={medlem.navIdent}
												timeout={isCreateDelete ? 500 : 1}
												classNames="fade"
											>
												<Table.Row
													key={medlem.navIdent}
													deleteAction={() => removeMember(medlem.navIdent)}
													deleteMessage={'Vil du slette ' + medlem.navIdent + ' fra dette teamet?'}
												>
													<Table.Column width="30" value={medlem.navIdent} />
													<Table.Column width="10" value="Utvikler" />
												</Table.Row>
											</CSSTransition>
										))}
									</TransitionGroup>
								</Table>
							)}
						/>
					</Fragment>
				)}

				<Overskrift label="Testdatagrupper" type="h2" />

				{grupperFetching ? (
					<Loading label="laster grupper" panel />
				) : (
					<PaginationConnector
						items={grupper}
						render={items => (
							<Table>
								<Table.Header>
									<Table.Column width="15" value="ID" />
									<Table.Column width="20" value="Navn" />
									<Table.Column width="15" value="Team" />
									<Table.Column width="40" value="Hensikt" />
								</Table.Header>

								{items.map(gruppe => (
									<Table.Row
										key={gruppe.id}
										navLink={() => history.push(`/gruppe/${gruppe.id}`)}
										// deleteAction={() => {}}
									>
										<Table.Column width="15" value={gruppe.id.toString()} />
										<Table.Column width="20" value={gruppe.navn} />
										<Table.Column width="15" value={gruppe.team.navn} />
										<Table.Column width="40" value={gruppe.hensikt} />
									</Table.Row>
								))}
							</Table>
						)}
					/>
				)}
			</div>
		)
	}
}

export default Team
