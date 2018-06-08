import React, { Component } from 'react'
import Header from '~/components/header/Header'
import Routes from '../Routes'
import './App.less'

export default class App extends Component {
	render() {
		return (
			<div id="dolly-app">
				<Header />
				<main>
					<Routes />
				</main>
			</div>
		)
	}
}
