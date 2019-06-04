import React, { Component, Fragment } from 'react'

export default class Doedsbo extends Component {
	constructor(props) {
		super(props)
	}
	render() {
		const { items, formikProps } = this.props
		console.log('formikProps dødden:', formikProps)
		const attributtListe = this._finnAdressatSubItems(items, formikProps)
		return <div> hei</div>
	}

	_finnDefaultverdier = formikProps => {
		let defaultArray = []
		const copy = Object.assign(formikProps.values.doedsbo[0])
		Object.keys(copy).map(key => copy[key] && defaultArray.push({ key: copy[key] }))

		return defaultArray //sjekk hvilken form default skal stå på
	}

	_finnAdressatSubItems = (items, formikProps) => {
		let attributtListe = []
		console.log('formikProps død :', formikProps)
	}
}
