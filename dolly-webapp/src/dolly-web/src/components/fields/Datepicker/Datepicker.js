import React, { Component } from 'react'
import Datovelger from 'nav-datovelger'
import 'nav-datovelger/dist/datovelger/styles/datovelger.css'

export default class Datepicker extends Component {
	state = {
		dato: null
	}

	render() {
		return (
			<Datovelger id="datofelt" dato={this.state.dato} onChange={dato => this.setState({ dato })} />
		)
	}
}
