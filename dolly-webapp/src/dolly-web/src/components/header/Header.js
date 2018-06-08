import React from 'react'
import './Header.less'
import { NavLink } from 'react-router-dom'
import logo from '~/assets/img/nav-logo-hvit.png'

export default () => {
	return (
		<div id="dolly-header">
			<div
				id="header-icon-area"
				className="logo-wrapper"
				data-flex
				data-layout="row"
				data-layout-align="center center"
			>
				<a rel="home">
					<img alt="NAV logo" src={logo} />
				</a>
			</div>
			<div id="header-title" data-flex data-layout="row" data-layout-align="start center">
				<NavLink exact to="/">
					<h1 id="page-title">Dolly</h1>
				</NavLink>
			</div>

			<div id="header-navigation" data-flex data-layout="row" data-layout-align="start center">
				<div className="header-nav-clickable">
					<h3>Testdata</h3>
				</div>

				<div className="header-nav-clickable">
					<h3>Kømanager</h3>
				</div>

				<div className="header-nav-clickable">
					<h3>API-Dok</h3>
				</div>
			</div>

			<div id="header-user-area" data-flex data-layout="row" data-layout-align="start center">
				<div id="header-user-name">Sergio Fløgstad</div>
				<div id="header-user-icon">
					<i className="fa fa-user-circle"> </i>
				</div>
			</div>
		</div>
	)
}
