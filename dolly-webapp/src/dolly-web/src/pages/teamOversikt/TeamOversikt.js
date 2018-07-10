import React, { Component } from 'react'
import { ToggleGruppe, ToggleKnapp } from 'nav-frontend-skjema'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import Table from '~/components/table/Table'
import Loading from '~/components/loading/Loading'
import OpprettTeam from './OpprettTeam/OpprettTeam'
import Input from '~/components/fields/Input/Input'

import './TeamOversikt.less'

export default class TeamOversikt extends Component {
	static propTypes = {
		teams: PropTypes.object
	}

	state = {
		opprettTeam: false
	}

	componentDidMount() {
		this.props.fetchTeams()
	}

	opprettToggle = () => {
		this.setState({ opprettTeam: true })
	}

	opprettCancel = () => {
		this.setState({ opprettTeam: false })
	}

	opprettHandler = data => {
		this.props.createTeam(data)
		this.setState({ opprettTeam: false })
	}

	handleViewChange = e => {
		this.props.setTeamVisning(e.target.value)
		this.props.fetchTeams()
	}

	render() {
		const { teams, handleViewChange, history } = this.props
		const { items, fetching, visning } = teams

		return (
			<div className="team-tab">
				<div className="flexbox--space">
					<Overskrift
						label="Teams"
						actions={[{ icon: 'add-circle', onClick: this.opprettToggle }]}
					/>
					<Input name="sokefelt" className="label-offscreen" label="" placeholder="Søk" />
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
				</div>

				{this.state.opprettTeam && (
					<OpprettTeam onCancel={this.opprettCancel} opprettHandler={this.opprettHandler} />
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
							<Table.Row
								key={team.id}
								navLink={() => history.push(`team/${team.id}`)}
								editAction={() => {}}
								deleteAction={() => {}}
							>
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
