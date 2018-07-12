import React, { Component } from 'react'
import { ToggleGruppe, ToggleKnapp } from 'nav-frontend-skjema'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import Table from '~/components/table/Table'
import Loading from '~/components/loading/Loading'
import Input from '~/components/fields/Input/Input'

import './MainTableContainer.less'

export default class MainTableContainer extends Component {
	static propTypes = {
		headerLabel: PropTypes.string,
		startOpprettTeam: PropTypes.func,
		visning: PropTypes.string,
		byttVisning: PropTypes.func,
		listComponent: PropTypes.node,
		editComponent: PropTypes.node
	}

	render() {
		const {
			headerLabel,
			startOpprettTeam,
			visning,
			byttVisning,
			listComponent,
			createTeamComponent,
			visOpprettTeam
		} = this.props

		return (
			<div className="maintable">
				<div className="flexbox--space">
					<Overskrift
						label={headerLabel}
						actions={[{ icon: 'add-circle', onClick: startOpprettTeam }]}
					/>
					<Input name="sokefelt" className="label-offscreen" label="" placeholder="Søk" />
				</div>

				<div className="flexbox--space">
					<ToggleGruppe onChange={byttVisning} name="toggleGruppe">
						<ToggleKnapp value="mine" checked={visning === 'mine'} key="1">
							Mine
						</ToggleKnapp>
						<ToggleKnapp value="alle" checked={visning === 'alle'} key="2">
							Alle
						</ToggleKnapp>
					</ToggleGruppe>
				</div>
				{visOpprettTeam && createTeamComponent}
				{listComponent}
			</div>
		)
	}
}
