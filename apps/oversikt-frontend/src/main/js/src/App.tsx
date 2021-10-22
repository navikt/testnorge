import React from 'react'
import './App.less'
// @ts-ignore
import ApplicationService from '@/services/ApplicationService'

import { BrowserRouter as Router, Route, Switch } from 'react-router-dom'
import MagicTokenPage from '@/pages/MagicTokenPage'
import AccessTokenPage from '@/pages/AccessTokenPage'
import { LoadableComponent } from '@navikt/dolly-komponenter'
import styled from 'styled-components'
import { ScopeAccessTokenPage } from '@/pages/ScopeAccessTokenPage'
import LoginPage from '@/pages/LoginPage'
import { UserPage } from '@/pages/UserPage'

const Body = styled.div`
	border-bottom: solid 1px #c6c2bf;
`

export default () => {
	return (
		<Router>
			<Body>
				<Switch>
					<Route path="/login">
						<LoginPage />
					</Route>
					<Route path="/access-token/scope/:scope">
						<ScopeAccessTokenPage />
					</Route>

					<Route path="/access-token/:name">
						<LoadableComponent
							onFetch={ApplicationService.fetchApplications}
							render={(items) => (
								<AccessTokenPage
									navigations={items.map((application) => ({
										href:
											'/access-token/' +
											application.cluster +
											'.' +
											application.namespace +
											'.' +
											application.name,
										label: application.name,
										content: application,
									}))}
								/>
							)}
						/>
					</Route>
					<Route path="/user">
						<UserPage />
					</Route>
					<Route path="/">
						<MagicTokenPage />
					</Route>
				</Switch>
			</Body>
		</Router>
	)
}
