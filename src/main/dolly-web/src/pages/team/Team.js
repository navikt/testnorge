import React, { Component, Fragment } from 'react'
import Overskrift from '~/components/overskrift/Overskrift'
import Table from '~/components/table/Table'
import Loading from '~/components/loading/Loading'
import LeggTilBruker from './LeggTilBruker/LeggTilBruker'
import AddButton from '~/components/button/AddButton'
import ConfirmTooltip from '~/components/confirmTooltip/ConfirmTooltip'
import RedigerTeamConnector from '~/components/RedigerTeam/RedigerTeamConnector'

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
			visRedigerTeam
		} = this.props

		if (!team || !grupper) return null

		const teamMembers = team.medlemmer.map(medlem => medlem.navIdent)

		const teamActions = [
			{
				icon: 'edit',
				onClick: startRedigerTeam
			}
		]

		return (
			<div className="oversikt-container">
				<Overskrift label={team.navn} actions={teamActions}>
					{/* <ConfirmTooltip onClick={deleteTeam} /> */}
				</Overskrift>

				{visRedigerTeam && <RedigerTeamConnector team={team} />}

				<Overskrift label="Medlemmer" type="h2" />
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
						<Table>
							<Table.Header>
								<Table.Column width="30" value="Navn" />
								<Table.Column width="20" value="Rolle" />
							</Table.Header>

							{team.medlemmer.map(medlem => (
								<Table.Row
									key={medlem.navIdent}
									deleteAction={() => removeMember([medlem.navIdent])}
								>
									<Table.Column width="30" value={medlem.navIdent} />
									<Table.Column width="10" value="Utvikler" />
								</Table.Row>
							))}
						</Table>
						<AddButton title="Legg til nytt medlem" onClick={this.openLeggTilBrukerHandler} />
					</Fragment>
				)}

				<Overskrift label="Testdatagrupper" type="h2" />

				{grupperFetching ? (
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
							<Table.Row
								key={gruppe.id}
								navLink={() => history.push(`/gruppe/${gruppe.id}`)}
								deleteAction={() => {}}
							>
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
