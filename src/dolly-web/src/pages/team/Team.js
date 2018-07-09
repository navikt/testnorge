import React, { Component } from 'react'
import Overskrift from '~/components/overskrift/Overskrift'
import Table from '~/components/table/Table'

class Team extends Component {
	componentDidMount() {
		this.props.fetchTeams()
		this.props.getGrupper({ teamId: this.props.currentTeamId })
	}

	render() {
		const { team } = this.props
		if (!team) return null

		return (
			<div>
				<Overskrift label={team.navn} />

				<Table>
					<Table.Header>
						<Table.Column width="30" value="Navn" />
						<Table.Column width="20" value="Rolle" />
					</Table.Header>

					{team.medlemmer.map(medlem => (
						<Table.Row key={medlem.navIdent} deleteAction={() => {}}>
							<Table.Column width="30" value={medlem.navIdent} />
							<Table.Column width="10" value="Utvikler" />
						</Table.Row>
					))}
				</Table>
			</div>
		)
	}
}

export default Team
