import React, { Component } from 'react'
import { Switch, Route } from 'react-router-dom'
import Header from '~/components/header/Header'
import Loading from '~/components/loading/Loading'
import Breadcrumb from '~/components/breadcrumb/BreadcrumbWithHoc'
import SplashscreenConnector from '~/components/splashscreen/SplashscreenConnector'
import Toast from '~/components/toast/Toast'
import routes from '~/Routes'

import './App.less'

export default class App extends Component {
	componentDidMount() {
		this.props.fetchDollyApiConfig()
		this.props.fetchCurrentBruker()
	}

	componentDidUpdate() {
		const { redirectTo, onRedirect, router } = this.props
		if (redirectTo && router.location.pathname !== redirectTo) return onRedirect(redirectTo)
	}

	render() {
		const { brukerData, applicationError, clearAllErrors } = this.props

		if (!brukerData) return <Loading label="laster dolly applikasjon" fullpage />

		return (
			<React.Fragment>
				<Header brukerData={brukerData} />
				<Breadcrumb />
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
				{applicationError && <Toast error={applicationError} clearErrors={clearAllErrors} />}
			</React.Fragment>
		)
	}
}
