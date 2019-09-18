import React, { Component, Fragment } from 'react'
import Table from '~/components/ui/table/Table'
import RedigerTeamConnector from '~/components/RedigerTeam/RedigerTeamConnector'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'

class TeamListe extends Component {
	render() {
		const { items, history, startRedigerTeam, editTeamId, deleteTeam, searchActive } = this.props
		if (!items || !items.length) {
			return (
				<ContentContainer>
					{searchActive
						? 'Søket gav ingen resultater.'
						: 'Du har ingen teams. Trykke på opprett knappen for å opprette ett nytt team.'}
				</ContentContainer>
			)
		}

		return (
			<Table>
				<Table.Header>
					<Table.Column width="20" value="Navn" />
					<Table.Column width="30" value="Beskrivelse" />
					<Table.Column width="20" value="Eier" />
					<Table.Column width="20" value="Personer" />
					<Table.Column width="10" value="" />
				</Table.Header>

				{items.map(team => {
					if (editTeamId === team.id) {
						return <RedigerTeamConnector key={team.id} team={team} />
					}

					return (
						<Table.Row key={team.id} navLink={() => history.push(`team/${team.id}`)}>
							<Table.Column width="20" value={team.navn} />
							<Table.Column width="30" value={team.beskrivelse} />
							<Table.Column width="20" value={team.eierNavIdent} />
							<Table.Column width="10" value={team.antallMedlemmer.toString()} />
						</Table.Row>
					)
				})}
			</Table>
		)
	}
}

export default TeamListe
