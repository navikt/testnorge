import React from 'react'
import { NavLink } from 'react-router-dom'
import Icon from '~/components/ui/icon/Icon'
import logo from '~/assets/img/nav-logo-hvit.png'

import './Header.less'

export default ({ brukerData }) => {
	const isGruppePathActive = (match, location) => {
		return location.pathname === '/' || location.pathname.includes('/gruppe')
	}

	return (
		<header className="app-header">
			<NavLink to="/" className="home-nav">
				<div className="img-logo">
					<img alt="NAV logo" src={logo} />
				</div>
				<Icon size={30} kind="dolly" className="dollysheep" />
				<h1>Dolly</h1>
			</NavLink>

			<div className="menu-links">
				<NavLink isActive={isGruppePathActive} to="/">
					Testdatagrupper
				</NavLink>
				<NavLink to="/tpsendring">Endringsmelding</NavLink>
				<a href="/swagger-ui.html" target="_blank">
					API-dok
				</a>
			</div>

			<div className="profil-area flexbox--all-center">
				<NavLink to="/minside">
					<Icon kind="user" size="25" />
					{brukerData.brukerId}
				</NavLink>
			</div>
		</header>
	)
}
