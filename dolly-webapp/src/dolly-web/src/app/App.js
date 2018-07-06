import React, { Component } from 'react'
import Routes from '~/Routes'
import Header from '~/components/header/Header'
import Breadcrumb from '~/components/breadcrumb/Breadcrumb'
import Loading from '~/components/loading/Loading'

import './App.less'

export default class App extends Component {
	componentDidMount() {
		this.props.fetchCurrentBruker()
	}

	render() {
		const { brukerData } = this.props

		if (!brukerData) return <Loading label="laster dolly applikasjon" fullpage />

		return (
			<React.Fragment>
				<Header brukerData={brukerData} />
				<Breadcrumb />
				<main>
					<Routes />
				</main>
			</React.Fragment>
		)
	}
}
