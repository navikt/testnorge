import React, { Component } from 'react'
import Header from '~/components/header/Header'
import Routes from '../Routes'
import Breadcrumb from '~/components/breadcrumb/Breadcrumb'

import './App.less'

export default class App extends Component {
	render() {
		return (
			<div id="dolly-app">
				<Header />
				<main>
					<Breadcrumb />
					<Routes />
				</main>
			</div>
		)
	}
}
