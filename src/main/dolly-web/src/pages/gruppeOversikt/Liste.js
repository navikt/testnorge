import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Table from '~/components/table/Table'
import ContentContainer from '~/components/contentContainer/ContentContainer'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'

export default class Liste extends PureComponent {
	static propTypes = {
		items: PropTypes.array,
		editId: PropTypes.number,
		editGroup: PropTypes.func,
		history: PropTypes.object
	}

	render() {
		const { items, editId, editGroup, history, deleteGruppe } = this.props

		if (!items) {
			return (
				<ContentContainer>
					<p>Du har ingen testdatagrupper.</p>
					<p>
						For å se alle testdatagrupper, trykk på "Alle". Her kan du søke etter en spesifikk
						testdatagruppe eller se om det er noen som er relevante for deg. Hvis du trykker på
						stjerneikonet, legger du testdatagruppen til som en favoritt. Den vil da dukke opp under
						"Mine" testdatagrupper.
					</p>
					<p>For å opprette en ny testdatagruppe, trykk på legg til-knappen nedenfor.</p>
				</ContentContainer>
			)
		}

		return (
			<Table>
				<Table.Header>
					<Table.Column width="15" value="ID" />
					<Table.Column width="20" value="Navn" />
					<Table.Column width="15" value="Team" />
					<Table.Column width="20" value="Hensikt" />
					<Table.Column width="20" value="Personer" />
				</Table.Header>

				{items.map(gruppe => {
					if (gruppe.id === editId) {
						return <RedigerGruppeConnector key={gruppe.id} gruppe={gruppe} />
					}

					// base row props
					const rowProps = {
						key: `row${gruppe.id}`,
						navLink: () => history.push(`gruppe/${gruppe.id}`)
					}

					// Vise redigeringsknapp eller stjerne
					if (gruppe.erMedlemAvTeamSomEierGruppe) {
						rowProps.editAction = () => editGroup(gruppe.id)
						rowProps.deleteAction = () => deleteGruppe(gruppe.id)
					} else {
						rowProps.favoriteAction = () => {
							alert('favorite this - not implemented')
						}
					}

					return (
						<Table.Row {...rowProps}>
							<Table.Column width="15" value={gruppe.id.toString()} />
							<Table.Column width="20" value={gruppe.navn} />
							<Table.Column width="15" value={gruppe.team.navn} />
							<Table.Column width="20" value={gruppe.hensikt} />
							<Table.Column width="20" value={gruppe.testidenter.length.toString()} />
						</Table.Row>
					)
				})}
			</Table>
		)
	}
}
