import React, { Component } from 'react'
import Table from '~/components/table/Table'
import Knapp from 'nav-frontend-knapper'
import Api from '~/service/Api'
import OpprettGruppe from './OpprettGruppe/OpprettGruppe'
import './GruppeOversikt.less'

export default class GruppeOversikt extends Component {
	state = {
		grupper: null,
		visOpprettGruppe: false
	}

	componentDidMount() {
		this.hentGrupper()
	}

	hentGrupper = async () => {
		const { data } = await Api.getGrupper()
		this.setState({ grupper: data })
	}

	onOpprettGruppeSuccess = () => this.setState({ visOpprettGruppe: false }, this.hentGrupper)

	toggleVisOpprettGruppe = () => this.setState({ visOpprettGruppe: !this.state.visOpprettGruppe })

	render() {
		const { visOpprettGruppe, grupper } = this.state
		const opprettGruppeText = visOpprettGruppe ? 'Lukk opprett gruppe' : 'Opprett ny gruppe'

		return (
			<div id="gruppeoversikt-container">
				<div className="gruppeoversikt-header">
					<h1>Mine testdatagrupper</h1>
					<Knapp type="standard" onClick={this.toggleVisOpprettGruppe}>
						{opprettGruppeText}
					</Knapp>
				</div>

				{visOpprettGruppe && <OpprettGruppe onSuccess={this.onOpprettGruppeSuccess} />}

				{grupper && <Table data={grupper} />}
			</div>
		)
	}
}
