import React, { Component } from 'react'
import { Switch, Route } from 'react-router-dom'
import Header from '~/components/layout/header/Header'
import Breadcrumb from '~/components/layout/breadcrumb/BreadcrumbWithHoc'
import Loading from '~/components/ui/loading/Loading'
import { AppError } from '~/components/ui/appError/AppError'
import Toast from '~/components/ui/toast/Toast'
import routes from '~/Routes'

import './App.less'

export default class App extends Component {
	state = {
		bootError: false
	}

	async componentDidMount() {
		await this.props.fetchConfig().catch(err => {
			this.setState({ bootError: true })
		})
		await this.props.getCurrentBruker()
		await this.props.getEnvironments()
	}

	componentDidUpdate() {
		const { redirectTo, onRedirect, router } = this.props
		if (redirectTo && router.location.pathname !== redirectTo) return onRedirect(redirectTo)
	}

	render() {
		const { brukerData, applicationError, clearAllErrors, configReady } = this.props

		if (this.state.bootError)
			return (
				<AppError error="Problemer med å hente dolly config. Prøv å refresh siden (ctrl + R)." />
			)

		if (!brukerData || !configReady) return <Loading label="laster dolly applikasjon" fullpage />
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
