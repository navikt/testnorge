import React, { Component } from 'react'
import Button from '~/components/ui/button/Button'

import './Beskrivelse.less'

export default class BeskrivelseFelt extends Component {
	constructor(props) {
		super(props)
		this.state = { value: props ? props.beskrivelse : '' }
	}

	render() {
		return (
			<div className="beskrivelse-felt">
				<form>
					<div>
						{this.state.value}
						<Button onClick={this.props.toggleIsEditing} className="beskrivelse-button-leggtil">
							Rediger
						</Button>
					</div>
				</form>
			</div>
		)
	}
}
