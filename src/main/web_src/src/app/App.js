import React, { Component, Suspense } from 'react'
import { Route, Switch } from 'react-router-dom'
import Header from '~/components/layout/header/Header'
import Breadcrumb from '~/components/layout/breadcrumb/BreadcrumbWithHoc'
import Loading from '~/components/ui/loading/Loading'
import Toast from '~/components/ui/toast/Toast'
import routes from '~/Routes'

import './App.less'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export default class App extends Component {
	state = {
		error: null
	}

	async componentDidMount() {
		await this.props.fetchConfig().catch(err => {
			this.setState({ error: err })
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

		if (this.state.error)
			return (
				<ErrorBoundary
					stackTrace={this.state.error.message}
					error={'Problemer med å hente dolly config. Prøv å refresh siden (ctrl + R).'}
					style={{ margin: '25px auto' }}
				/>
			)

		if (!brukerData || !configReady) return <Loading label="laster dolly applikasjon" fullpage />
		return (
			<React.Fragment>
				<Header brukerData={brukerData} />
				<Breadcrumb />
				<main>
					<Suspense fallback={<Loading label="Laster inn" />}>
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
					</Suspense>
				</main>
				{applicationError && <Toast error={applicationError} clearErrors={clearAllErrors} />}
			</React.Fragment>
		)
	}
}
