import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import Table from '~/components/table/Table'

export default class TeamOversikt extends Component {
	static propTypes = {}

	render() {
		return (
			<div className="team-tab">
				<Overskrift
					type="h2"
					label="Teams"
					actions={[{ icon: 'plus-circle', onClick: () => {} }]}
				/>

				<Table>
					<Table.Header>
						<Table.Column width="10" value="ID" />
						<Table.Column width="15" value="Navn" />
						<Table.Column width="30" value="Eier" />
						<Table.Column width="15" value="Alder" />
						<Table.Column width="20" value="Kjønn" />
					</Table.Header>

					<Table.Row actionWidth="10" editAction={() => {}} deleteAction={() => {}}>
						<Table.Column width="10" value="a" />
						<Table.Column width="15" value="a" />
						<Table.Column width="30" value="a" />
						<Table.Column width="15" value="a" />
						<Table.Column width="20" value="a" />
					</Table.Row>
				</Table>
			</div>
		)
	}
}
