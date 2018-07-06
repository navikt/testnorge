import React, { Component } from 'react'
import { ToggleGruppe, ToggleKnapp } from 'nav-frontend-skjema'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import Table from '~/components/table/Table'
import Loading from '~/components/loading/Loading'
import OpprettTeamConnector from '../OpprettTeam/OpprettTeamConnector'

export default class TeamOversikt extends Component {
	static propTypes = {
		teams: PropTypes.object
	}

	state = {
		opprettTeam: true
	}

	opprettToggle = () => {
		this.setState({ opprettTeam: true })
	}

	opprettCancel = () => {
		this.setState({ opprettTeam: false })
	}

	opprettSuccess = () => {
		//send opprett call til API
		this.setState({ opprettTeam: false })
	}

	render() {
		const { teams, handleViewChange } = this.props
		const { items, fetching, visning } = teams

		return (
			<div className="team-tab">
				<Overskrift
					type="h2"
					label="Teams"
					actions={[{ icon: 'add-circle', onClick: this.opprettToggle }]}
				/>

				<div className="flexbox--space">
					<ToggleGruppe onChange={handleViewChange} name="toggleGruppe">
						<ToggleKnapp value="mine" checked={visning === 'mine'} key="1">
							Mine
						</ToggleKnapp>
						<ToggleKnapp value="alle" checked={visning === 'alle'} key="2">
							Alle
						</ToggleKnapp>
					</ToggleGruppe>
				</div>

				{this.state.opprettTeam && (
					<OpprettTeamConnector onCancel={this.opprettCancel} onSuccess={this.opprettSuccess} />
				)}

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

						{items.map(team => (
							<Table.Row key={team.id} editAction={() => {}} deleteAction={() => {}}>
								<Table.Column width="20" value={team.navn} />
								<Table.Column width="30" value={team.beskrivelse} />
								<Table.Column width="20" value={team.eierNavIdent} />
								<Table.Column width="10" value={team.medlemmer.length.toString()} />
							</Table.Row>
						))}
					</Table>
				)}
			</div>
		)
	}
}
