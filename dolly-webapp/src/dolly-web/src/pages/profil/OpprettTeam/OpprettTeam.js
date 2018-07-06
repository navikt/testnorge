import React, { Component } from 'react'
import { Input } from 'nav-frontend-skjema'
import Knapp from 'nav-frontend-knapper'

import './OpprettTeam.less'

class OpprettTeam extends Component {
	state = {
		nameInput: '',
		beskrivelseInput: ''
	}

	nameInputHandler = e => {
		this.setState({
			nameInput: e.target.value
		})
	}

	beskrivelseInputHandler = e => {
		this.setState({
			beskrivelseInput: e.target.value
		})
	}

	render() {
		const { onCancel, onSuccess } = this.props
		return (
			<div className="opprett-team-container">
				<Input label="NAVN" name="navn" value={this.state.nameInput} onChange={this.inputHandler} />
				<Input
					label="BESKRIVELSE"
					name="beskrivelse"
					value={this.state.beskrivelseInput}
					onChange={this.beskrivelseInputHandler}
				/>
				<Knapp type="hoved" onClick={onSuccess}>
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
