import React, { Component } from 'react'
import Knapp from 'nav-frontend-knapper'
import { ToggleGruppe, ToggleKnapp } from 'nav-frontend-skjema'
import Table from '~/components/table/Table'
import Input from '~/components/fields/Input/Input'
import IconButton from '~/components/fields/IconButton/IconButton'
import RedigerGruppe from './RedigerGruppe/RedigerGruppe'
import './GruppeOversikt.less'

export default class GruppeOversikt extends Component {
	state = {
		visOpprettGruppe: false,
		gruppeEier: 'mine'
	}

	componentDidMount() {
		this.props.getGrupper()
	}

	onOpprettGruppeSuccess = () => this.setState({ visOpprettGruppe: false }, this.props.getGrupper)

	toggleVisOpprettGruppe = () => this.setState({ visOpprettGruppe: !this.state.visOpprettGruppe })

	toggleGruppeOwner = e => this.setState({ gruppeEier: e.target.value })

	render() {
		const { visOpprettGruppe } = this.state
		const { grupper } = this.props
		const opprettGruppeText = visOpprettGruppe ? 'Lukk opprett gruppe' : 'Ny gruppe'

		return (
			<div id="gruppeoversikt-container">
				<div className="content-header">
					<h1>
						Testdatagrupper{' '}
						<IconButton onClick={this.toggleVisOpprettGruppe} iconName="plus-circle" />
					</h1>
					<Input name="sokefelt" className="label-offscreen" label="" placeholder="Søk" />
				</div>

				<div className="content-header">
					<ToggleGruppe onChange={this.toggleGruppeOwner} name="toggleGruppe">
						<ToggleKnapp value="mine" defaultChecked={true} key="1">
							Mine
						</ToggleKnapp>
						<ToggleKnapp value="alle" key="2">
							Alle
						</ToggleKnapp>
					</ToggleGruppe>
				</div>

				{visOpprettGruppe && (
					<RedigerGruppe
						onSuccess={this.onOpprettGruppeSuccess}
						onCancel={this.toggleVisOpprettGruppe}
					/>
				)}

				{grupper && <Table data={grupper} link />}
			</div>
		)
	}
}
