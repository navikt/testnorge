import React, { Component } from 'react'
import InputTextField from '../felles/fields/InputTextField'

class NyGruppeForm extends Component {
	constructor(props, context) {
		super(props, context)

		this.state = {
			gruppe: {}
		}
	}

	onInputChange = (statePropToChange, value) => {
		let gruppe = Object.assign({}, this.state.gruppe)
		gruppe[statePropToChange] = value

		this.setState({
			gruppe: gruppe
		})
	}

	onClickSave = () => {
		this.props.onClickSave(this.state.gruppe)
	}

	render() {
		return (
			<div className="skjemaelement">
				<br />
				<InputTextField
					label={'Navn'}
					id={'navn-nygruppe'}
					value={!this.state.gruppe['navn'] ? '' : this.state.gruppe['navn']}
					onChange={this.onInputChange}
					pattern="[a-z]+"
					patternFeilmelding="Feilmelding"
					statePropToChange={'navn'}
				/>

				<InputTextField
					label={'Beskrivelse'}
					id={'beskrivelse-nygruppe'}
					value={!this.state.gruppe['beskrivelse'] ? '' : this.state.gruppe['beskrivelse']}
					onChange={this.onInputChange}
					statePropToChange={'beskrivelse'}
				/>

				<InputTextField
					label={'Team'}
					id={'team-nygruppe'}
					value={!this.state.gruppe['team'] ? '' : this.state.gruppe['team']}
					onChange={this.onInputChange}
					statePropToChange={'team'}
				/>

				<br />
				<button onClick={this.onClickSave}>Lagre gruppe</button>
			</div>
		)
	}
}

export default NyGruppeForm
