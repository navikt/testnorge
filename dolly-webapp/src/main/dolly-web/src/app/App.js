import React, { Component } from 'react'
import { Switch, Route } from 'react-router-dom'
import Header from '~/components/header/Header'
import Loading from '~/components/loading/Loading'
import Breadcrumbs from '~/components/breadcrumb/Breadcrumb'
import routes from '~/Routes'

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
				<Breadcrumbs />
				<main>
					<Switch>
						{routes.map((route, idx) => {
							return route.component ? (
								<Route
									key={idx}
									path={route.path}
									exact={route.exact}
									render={props => <route.component {...props} />}
								/>
							) : null
						})}
					</Switch>
				</main>
			</React.Fragment>
		)
	}
}
