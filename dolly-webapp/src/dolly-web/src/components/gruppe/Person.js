import React, { Component } from 'react'
import InputTextField from '../felles/fields/InputTextField'

export default class Person extends Component {
	constructor(props, context) {
		super(props, context)

		this.state = {
			person: this.props.person,
			expanded: false
		}
	}

	toggle = () => {
		this.setState(prevState => {
			return { expanded: !prevState.expanded }
		})
	}

	test = () => console.log(this.state.person)

	onInputChange = (statePropToChange, value) => {
		let tempPerson = Object.assign({}, this.state.person)
		tempPerson[statePropToChange] = value

		let isFormValid = document.getElementById('myform').checkValidity()

		this.setState({ person: tempPerson })
	}

	render() {
		if (this.state.expanded) {
			return (
				<div className="person-input-row" data-flex data-layout="row">
					<form id="myform" name="formname" noValidate>
						<InputTextField
							label="Fornavn"
							id="navn-id"
							value={this.state.person.fornavn}
							onChange={this.onInputChange}
							pattern="[a-z]+"
							patternFeilmelding="Bare små bokstaver"
							statePropToChange="fornavn"
						/>

						<InputTextField
							label="Etternavn"
							id="etternavn-id"
							statePropToChange="etternavn"
							disabled
							value={this.state.person.etternavn}
							onChange={this.onInputChange}
						/>
					</form>

					<button onClick={this.test}>Se verdi</button>
				</div>
			)
		} else {
			return <div onClick={this.toggle}>--> {this.state.person.fornavn}</div>
		}
	}
}
