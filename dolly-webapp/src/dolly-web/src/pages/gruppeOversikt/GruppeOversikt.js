import React, { Component } from 'react'
import Table from '~/components/table/Table'
import Knapp from 'nav-frontend-knapper'
import OpprettGruppe from './OpprettGruppe/OpprettGruppe'
import './GruppeOversikt.less'

export default class GruppeOversikt extends Component {
	state = {
		visOpprettGruppe: false
	}

	componentDidMount() {
		this.props.getGrupper()
	}

	onOpprettGruppeSuccess = () => this.setState({ visOpprettGruppe: false }, this.props.getGrupper)

	toggleVisOpprettGruppe = () => this.setState({ visOpprettGruppe: !this.state.visOpprettGruppe })

	render() {
		const { visOpprettGruppe } = this.state
		const { grupper } = this.props
		const opprettGruppeText = visOpprettGruppe ? 'Lukk opprett gruppe' : 'Opprett ny gruppe'

		return (
			<div id="gruppeoversikt-container">
				<div className="content-header">
					<h1>Mine testdatagrupper</h1>
					<Knapp type="standard" onClick={this.toggleVisOpprettGruppe}>
						{opprettGruppeText}
					</Knapp>
				</div>

				{visOpprettGruppe && <OpprettGruppe onSuccess={this.onOpprettGruppeSuccess} />}

				{grupper && <Table data={grupper} link />}
			</div>
		)
	}
}
