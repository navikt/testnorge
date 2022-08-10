import React from 'react'
// @ts-ignore
import ApplicationService from '@/services/ApplicationService'

import '@navikt/ds-css'

import { BrowserRouter, Route, Routes } from 'react-router-dom'
import MagicTokenPage from '@/pages/MagicTokenPage'
import AccessTokenPage from '@/pages/AccessTokenPage'
import { LoadableComponent } from '@navikt/dolly-komponenter'
import { ScopeAccessTokenPage } from '@/pages/ScopeAccessTokenPage'
import LoginPage from '@/pages/LoginPage'
import { UserPage } from '@/pages/UserPage'

export default () => {
	return (
		<BrowserRouter>
			<Routes>
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
			</Routes>
		</BrowserRouter>
	)
}
