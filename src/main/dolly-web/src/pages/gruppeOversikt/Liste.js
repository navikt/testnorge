import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Table from '~/components/table/Table'
import RedigerConnector from './Rediger/RedigerConnector'

export default class Liste extends PureComponent {
	static propTypes = {
		items: PropTypes.array,
		editId: PropTypes.number,
		startRedigerGruppe: PropTypes.func,
		history: PropTypes.object
	}

	render() {
		const { items, editId, startRedigerGruppe, history } = this.props

		if (!items) return false

		return (
			<Table>
				<Table.Header>
					<Table.Column width="15" value="ID" />
					<Table.Column width="20" value="Navn" />
					<Table.Column width="15" value="Team" />
					<Table.Column width="50" value="Hensikt" />
				</Table.Header>

				{items.map(gruppe => {
					if (gruppe.id === editId) {
						return <RedigerConnector key={gruppe.id} gruppe={gruppe} />
					}

					// base row props
					const rowProps = {
						key: `row${gruppe.id}`,
						navLink: () => history.push(`gruppe/${gruppe.id}`)
					}

					// Vise redigeringsknapp eller stjerne
					if (gruppe.erMedlemAvTeamSomEierGruppe) {
						rowProps.editAction = () => startRedigerGruppe(gruppe.id)
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
							<Table.Column width="40" value={gruppe.hensikt} />
						</Table.Row>
					)
				})}
			</Table>
		)
	}
}
