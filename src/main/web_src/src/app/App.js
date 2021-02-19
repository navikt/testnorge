import React, { Component, Suspense } from 'react'
import { Route, Switch } from 'react-router-dom'
import Header from '~/components/layout/header/Header'
import Breadcrumb from '~/components/layout/breadcrumb/BreadcrumbWithHoc'
import Loading from '~/components/ui/loading/Loading'
import Toast from '~/components/ui/toast/Toast'
import routes from '~/Routes'
import { VarslingerModal } from '~/components/varslinger/VarslingerModal'

import './App.less'
import { Forbedring } from '~/components/feedback/Forbedring'
import Utlogging from '~/components/utlogging'
import { CriticalError } from '~/components/ui/appError/CriticalError'

export default class App extends Component {
	state = {
		criticalError: null,
		apiError: null
	}

	async componentDidMount() {
		await this.props.fetchConfig().catch(err => this.setState({ criticalError: err }))
		await this.props.getEnvironments().catch(err => this.setState({ criticalError: err }))
		await this.props.getCurrentBruker().catch(err => this.setState({ criticalError: err }))
		await this.props.getCurrentBrukerProfil().catch(err => this.setState({ apiError: err }))
		await this.props.getCurrentBrukerBilde().catch(err => this.setState({ apiError: err }))
		await this.props.getVarslinger().catch(err => this.setState({ apiError: err }))
		await this.props.getVarslingerBruker().catch(err => this.setState({ apiError: err }))
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

		if (this.state.criticalError) return <CriticalError error={this.state.criticalError.stack} />

		if (!brukerData || !configReady) return <Loading label="laster dolly applikasjon" fullpage />
		return (
			<React.Fragment>
				<Utlogging />
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
				{this.state.apiError && <Toast error={this.state.apiError} clearErrors={clearAllErrors} />}
			</React.Fragment>
		)
	}
}
