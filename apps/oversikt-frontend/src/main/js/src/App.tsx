import React from 'react'
// @ts-ignore
import ApplicationService from '@/services/ApplicationService'

import { BrowserRouter, Route, Routes } from 'react-router-dom'
import MagicTokenPage from '@/pages/MagicTokenPage'
import AccessTokenPage from '@/pages/AccessTokenPage'
import { LoadableComponent } from '@navikt/dolly-komponenter'
import { ScopeAccessTokenPage } from '@/pages/ScopeAccessTokenPage'
import LoginPage from '@/pages/LoginPage'
import { UserPage } from '@/pages/UserPage'

export default () => (
	<BrowserRouter>
		<Routes>
			<Route path="/*" element={<MagicTokenPage />} />
			<Route path="/login" element={<LoginPage />} />
			<Route path="/access-token/scope/:scope" element={<ScopeAccessTokenPage />} />
			<Route
				path="/access-token/:name"
				element={
					<LoadableComponent
						onFetch={ApplicationService.fetchApplications}
						render={(items) => {
							return (
								<AccessTokenPage
									navigations={items.map((application) => {
										const cluster = application.cluster?.replace(
											'unknown',
											application?.name?.includes('proxy') ? 'dev-fss' : 'dev-gcp'
										)
										return {
											href:
												'/access-token/' +
												cluster +
												'.' +
												application.namespace +
												'.' +
												application.name,
											label: application.name,
											content: application,
										}
									})}
								/>
							)
						}}
					/>
				}
			/>
			<Route path="/user" element={<UserPage />} />
		</Routes>
	</BrowserRouter>
)
