import React, { Component, Suspense } from 'react'
import { Route, Switch } from 'react-router-dom'
import Header from '~/components/layout/header/Header'
import Breadcrumb from '~/components/layout/breadcrumb/BreadcrumbWithHoc'
import Loading from '~/components/ui/loading/Loading'
import Toast from '~/components/ui/toast/Toast'
import routes from '~/Routes'
import { VarslingerModal } from '~/components/varslinger/VarslingerModal'

import './App.less'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { Forbedring } from '~/components/feedback/Forbedring'

export default class App extends Component {
	state = {
		error: null
	}

	async componentDidMount() {
		await this.props.fetchConfig().catch(err => {
			this.setState({ error: err })
		})
		await this.props.getCurrentBruker()
		await this.props.getCurrentBrukerProfil()
		await this.props.getCurrentBrukerBilde()
		await this.props.getEnvironments()
		await this.props.getVarslinger()
		await this.props.getVarslingerBruker()
	}

	componentDidUpdate() {
		const { redirectTo, onRedirect, router } = this.props
		if (redirectTo && router.location.pathname !== redirectTo) return onRedirect(redirectTo)
	}

	render() {
		const {
			applicationError,
			clearAllErrors,
			configReady,
			brukerData,
			brukerProfil,
			brukerBilde,
			varslinger,
			varslingerBruker,
			isLoadingVarslinger,
			updateVarslingerBruker
		} = this.props

		if (this.state.error)
			return (
				<ErrorBoundary
					error={'Problemer med å hente dolly config. Prøv å refresh siden (ctrl + R).'}
					stackTrace={this.state.error.stack}
					style={{ margin: '25px auto' }}
				/>
			)

		if (!brukerData || !configReady) return <Loading label="laster dolly applikasjon" fullpage />
		return (
			<React.Fragment>
				<VarslingerModal
					varslinger={varslinger}
					varslingerBruker={varslingerBruker}
					isLoadingVarslinger={isLoadingVarslinger}
					updateVarslingerBruker={updateVarslingerBruker}
				/>
				<Header brukerProfil={brukerProfil} brukerBilde={brukerBilde} />
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
				<Forbedring brukerBilde={brukerBilde} />
				{applicationError && <Toast error={applicationError} clearErrors={clearAllErrors} />}
			</React.Fragment>
		)
	}
}
