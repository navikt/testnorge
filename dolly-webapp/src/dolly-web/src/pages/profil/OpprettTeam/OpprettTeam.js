import React, { Component } from 'react'
import { Input } from 'nav-frontend-skjema'
import Knapp from 'nav-frontend-knapper'

import './OpprettTeam.less'

class OpprettTeam extends Component {
	state = {
		navn: '',
		beskrivelse: ''
	}

	navnHandler = e => {
		this.setState({
			navn: e.target.value
		})
	}

	beskrivelseHandler = e => {
		this.setState({
			beskrivelse: e.target.value
		})
	}

	onClickOpprett = () => {
		const { navn, beskrivelse } = this.state
		const data = {
			navn,
			beskrivelse
		}
		this.props.opprettHandler(data)
	}

	render() {
		const { onCancel } = this.props
		return (
			<div className="opprett-team-container">
				<Input label="NAVN" name="navn" value={this.state.navn} onChange={this.navnHandler} />
				<Input
					label="BESKRIVELSE"
					name="beskrivelse"
					value={this.state.beskrivelse}
					onChange={this.beskrivelseHandler}
				/>
				<Knapp type="hoved" onClick={this.onClickOpprett}>
					Opprett
				</Knapp>
				<Knapp type="standard" onClick={onCancel}>
					Avbryt
				</Knapp>
			</div>
		)
	}
}

export default OpprettTeam
