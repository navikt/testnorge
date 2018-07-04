import React, { Component } from 'react'
import Routes from '~/Routes'
import Header from '~/components/header/Header'
import Breadcrumb from '~/components/breadcrumb/Breadcrumb'

import './App.less'

export default class App extends Component {
	componentDidMount() {
		this.props.fetchCurrentBruker()
	}

	render() {
		const { brukerData } = this.props
		if (!brukerData) return null
		return (
			<div id="dolly-app">
				<Header brukerData={brukerData} />
				<main>
					<Breadcrumb />
					<Routes />
				</main>
			</div>
		)
	}
}
