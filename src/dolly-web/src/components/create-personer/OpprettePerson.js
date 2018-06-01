import React, { Component } from 'react'

export default class CreatePerson extends Component {
	constructor(props, context) {
		super(props, context)

		this.state = {}
	}

	//TODO Skal hente personer basert på testidenter i gruppen etter hvert.
	componentDidMount() {}

	render() {
		return <div id="create-person-container" />
	}
}
