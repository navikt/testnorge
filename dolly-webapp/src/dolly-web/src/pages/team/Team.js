import React, { Component } from 'react'
import Overskrift from '~/components/overskrift/Overskrift'
import Table from '~/components/table/Table'
import Loading from '~/components/loading/Loading'

class Team extends Component {
	componentDidMount() {
		this.props.fetchTeams()
		this.props.getGrupper({ teamId: this.props.currentTeamId })
	}

	render() {
		const { team, grupper, teamFetching, gruppeFetching } = this.props

		if (!team || !grupper) return null

		return (
			<div>
				<Overskrift label={team.navn} />

				<Overskrift label="Medlemmer" type="h2" />
				{teamFetching ? (
					<Loading label="laster medlemmer" panel />
				) : (
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
				)}

				<Overskrift label="Testdatagrupper" type="h2" />

				{gruppeFetching ? (
					<Loading label="laster grupper" panel />
				) : (
					<Table>
						<Table.Header>
							<Table.Column width="15" value="ID" />
							<Table.Column width="20" value="Navn" />
							<Table.Column width="15" value="Team" />
							<Table.Column width="50" value="Hensikt" />
						</Table.Header>

						{grupper.map(gruppe => (
							<Table.Row key={gruppe.id} deleteAction={() => {}}>
								<Table.Column width="15" value={gruppe.id.toString()} />
								<Table.Column width="20" value={gruppe.navn} />
								<Table.Column width="15" value={gruppe.team.navn} />
								<Table.Column width="40" value={gruppe.hensikt} />
							</Table.Row>
						))}
					</Table>
				)}
			</div>
		)
	}
}

export default Team
