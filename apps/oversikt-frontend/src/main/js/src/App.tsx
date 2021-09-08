import React from 'react'
import './App.less'
// @ts-ignore
import ApplicationService from '@/services/ApplicationService'

import { BrowserRouter as Router, Route, Switch } from 'react-router-dom'
import MagicTokenPage from '@/pages/MagicTokenPage'
import AccessTokenPage from '@/pages/AccessTokenPage'
import {
	Header,
	HeaderLink,
	HeaderLinkGroup,
	LoadableComponent,
	ProfilLoader,
} from '@navikt/dolly-komponenter'

import ProfilService from './services/ProfilService'
import styled from 'styled-components'
import { ScopeAccessTokenPage } from '@/pages/ScopeAccessTokenPage'
import index from '@/pages/LoginPage'
import LoginPage from '@/pages/LoginPage'

const Body = styled.div`
	border-bottom: solid 1px #c6c2bf;
`

export default () => {
	return (
		<Router>
			<Header title="Generer token">
				<HeaderLinkGroup>
					<HeaderLink
						href="/magic-token"
						isActive={() =>
							window.location.pathname === '/' || window.location.pathname.includes('/magic-token')
						}
					>
						Magic Token
					</HeaderLink>
					<HeaderLink
						href="/access-token/dev-fss.dolly.dolly-backend"
						isActive={() => window.location.pathname.includes('/access-token')}
					>
						Access Token
					</HeaderLink>
				</HeaderLinkGroup>
			</Header>
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
									navigations={items.map((item: string) => ({
										href: '/access-token/' + item,
										label: item,
									}))}
								/>
							)}
						/>
					</Route>
					<Route path="/">
						<MagicTokenPage />
					</Route>
				</Switch>
			</Body>
		</Router>
	)
}
