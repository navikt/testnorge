import React, { Component } from 'react'
import InputTextField from '../felles/fields/InputTextField'

export default class TeamForm extends Component {
	constructor(props, context) {
		super(props, context)
		this.state = {
			team: {}
		}
	}

	onInputChange = (statePropToChange, value) => {
		let team = Object.assign({}, this.state.team)
		team[statePropToChange] = value

		this.setState({
			team: team
		})
	}

	onClickSave = () => {
		this.props.onClickSave(this.state.team)
	}

	render() {
		return (
			<div className="skjemaelement">
				<br />
				<InputTextField
					label={'Navn'}
					id={'navn-team'}
					value={!this.state.team['navn'] ? '' : this.state.team['navn']}
					onChange={this.onInputChange}
					pattern="[a-z]+"
					patternFeilmelding="Feilmelding"
					statePropToChange={'navn'}
				/>

				<InputTextField
					label={'Beskrivelse'}
					id={'beskrivelse-team'}
					value={!this.state.team['beskrivelse'] ? '' : this.state.team['beskrivelse']}
					onChange={this.onInputChange}
					statePropToChange={'beskrivelse'}
				/>

				<br />
				<button onClick={this.onClickSave}>Lagre gruppe</button>
			</div>
		)
	}
}
