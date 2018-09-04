import React from 'react'
import { NavLink } from 'react-router-dom'
import Icon from '~/components/icon/Icon'
import logo from '~/assets/img/nav-logo-hvit.png'

import './Header.less'

export default ({ brukerData }) => {
	return (
		<header className="app-header">
			<NavLink to="/" className="home-nav app-header--third">
				<div className="img-logo">
					<img alt="NAV logo" src={logo} />
				</div>
				<h1>Dolly</h1>
			</NavLink>

			<div className="menu-links app-header--third">
				<NavLink to="/">Testdatagrupper</NavLink>
				<NavLink to="/kømanager">Kømanager</NavLink>
				<NavLink to="/swagger-ui.html">API-dok</NavLink>
			</div>

			<div className="app-header--third">
				<NavLink to="/profil" className="header-user-name">
					<Icon kind="user" size="20" /> {brukerData.navIdent}
				</NavLink>
			</div>
		</header>
	)
}
