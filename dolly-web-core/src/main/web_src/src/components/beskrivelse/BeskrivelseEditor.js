import React, { Component } from 'react'
import Button from '~/components/ui/button/Button'

import './Beskrivelse.less'

export default class BeskrivelseEditor extends Component {
	constructor(props) {
		super(props);
		this.state = {value: props ? props.beskrivelse : ''};
		this.handleChange = this.handleChange.bind(this);
		this.handleSubmit = this.handleSubmit.bind(this);
	  }
	
	  handleChange(event) {
		this.setState({value: event.target.value});
	  }
	
	  handleSubmit() {
		console.log('this.props :', this.props);
		this.props.updateBeskrivelse(this.state.value);
		this.props.toggleIsEditing()
	  }
	
	  render() {
		return (
			<div className="beskrivelse-editor">
				<form>
					<textarea
					className="beskrivelse-editor-textarea"
					type="text" 
					value={this.state.value}
					placeholder="Skriv inn en beskrivelse"
					onChange={this.handleChange} />
					<br/>

					<Button
					onClick={this.handleSubmit}
					className="beskrivelse-button-leggtil">
					Legg til
					</Button>
				</form>
		  	</div>
		);
	  }
}