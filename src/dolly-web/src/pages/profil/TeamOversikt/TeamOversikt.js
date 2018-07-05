import React, { Component, Fragment } from 'react'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import Table from '~/components/table/Table'

export default class TeamOversikt extends Component {
	static propTypes = {
		teams: PropTypes.array
	}

	render() {
		const { teams } = this.props
		return (
			<div className="team-tab">
				<Overskrift type="h2" label="Teams" actions={[{ icon: 'add-circle', onClick: () => {} }]} />

				<Table>
					<Table.Header>
						<Table.Column width="30" value="Navn" />
						<Table.Column width="30" value="Eier" />
						<Table.Column width="20" value="Personer" />
					</Table.Header>

					<Table.Row editAction={() => {}} deleteAction={() => {}}>
						{teams.map(team => (
							<Fragment>
								<Table.Column width="30" value={team.navn} />
								<Table.Column width="30" value={team.eierNavIdent} />
								<Table.Column width="20" value={team.medlemmer.length} />
							</Fragment>
						))}
					</Table.Row>
				</Table>
			</div>
		)
	}
}
