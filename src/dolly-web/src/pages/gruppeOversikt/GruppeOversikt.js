import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { ToggleGruppe, ToggleKnapp } from 'nav-frontend-skjema'
import Overskrift from '~/components/overskrift/Overskrift'
import Input from '~/components/fields/Input/Input'
import RedigerConnector from './Rediger/RedigerConnector'
import Liste from './Liste'
import Loading from '~/components/loading/Loading'

import './GruppeOversikt.less'

export default class GruppeOversikt extends PureComponent {
	static propTypes = {
		grupper: PropTypes.object,
		history: PropTypes.object,
		startRedigerGruppe: PropTypes.func,
		startOpprettGruppe: PropTypes.func,
		getGrupper: PropTypes.func,
		settVisning: PropTypes.func
	}

	componentDidMount() {
		this.hentGrupper()
	}

	hentGrupper = () => this.props.getGrupper()
	byttVisning = e => {
		this.props.settVisning(e.target.value)
		this.props.getGrupper(e.target.value)
	}

	render() {
		const { grupper, history, startRedigerGruppe, startOpprettGruppe } = this.props

		return (
			<div className="gruppeoversikt-container">
				<div className="flexbox--space">
					<Overskrift
						label="Testdatagrupper"
						actions={[{ icon: 'add-circle', onClick: startOpprettGruppe }]}
					/>
					<Input name="sokefelt" className="label-offscreen" label="" placeholder="Søk" />
				</div>

				<div className="flexbox--space">
					<ToggleGruppe onChange={this.byttVisning} name="toggleGruppe">
						<ToggleKnapp value="mine" defaultChecked={true} key="1">
							Mine
						</ToggleKnapp>
						<ToggleKnapp value="alle" key="2">
							Alle
						</ToggleKnapp>
					</ToggleGruppe>
				</div>

				{grupper.visOpprettGruppe && <RedigerConnector />}

				{grupper.fetching ? (
					<Loading label="laster grupper" panel />
				) : (
					<Liste
						items={grupper.items}
						editId={grupper.editId}
						startRedigerGruppe={startRedigerGruppe}
						history={history}
					/>
				)}
			</div>
		)
	}
}
