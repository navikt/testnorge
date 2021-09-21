import React from 'react'
import Icon from '~/components/ui/icon/Icon'
// @ts-ignore
import logo from '~/assets/img/nav-logo-hvit.png'
import './BlankHeader.less'
import { NavLink } from 'react-router-dom'

export default () => {
	return (
		<header className="blank-header">
			<NavLink to="/login" className="home-nav">
				<div className="img-logo">
					<img alt="NAV logo" src={logo} />
				</div>
				<Icon size={30} kind="dolly" className="dollysheep" />
				<h1>Dolly</h1>
			</NavLink>
		</header>
	)
}
