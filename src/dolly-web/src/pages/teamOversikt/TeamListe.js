import React, { Component, Fragment } from 'react'
import Table from '~/components/table/Table'
import Loading from '~/components/loading/Loading'
import RedigerTeamConnector from './RedigerTeam/RedigerTeamConnector'

class TeamListe extends Component {
	render() {
		const { items, fetching, history, startRedigerTeam, editTeamId, deleteTeam } = this.props
		return (
			<Fragment>
				{fetching ? (
					<Loading label="laster teams" panel />
				) : (
					<Table>
						<Table.Header>
							<Table.Column width="20" value="Navn" />
							<Table.Column width="30" value="Beskrivelse" />
							<Table.Column width="20" value="Eier" />
							<Table.Column width="20" value="Personer" />
						</Table.Header>

						{items.map(team => {
							if (editTeamId === team.id) {
								return <RedigerTeamConnector key={team.id} team={team} />
							}

							return (
								<Table.Row
									key={team.id}
									navLink={() => history.push(`team/${team.id}`)}
									editAction={() => startRedigerTeam(team.id)}
									deleteAction={() => deleteTeam(team.id)}
								>
									<Table.Column width="20" value={team.navn} />
									<Table.Column width="30" value={team.beskrivelse} />
									<Table.Column width="20" value={team.eierNavIdent} />
									<Table.Column width="10" value={team.medlemmer.length.toString()} />
								</Table.Row>
							)
						})}
					</Table>
				)}
			</Fragment>
		)
	}
}

export default TeamListe
