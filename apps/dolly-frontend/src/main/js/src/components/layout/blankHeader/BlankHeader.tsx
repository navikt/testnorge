import React from 'react'
import Icon from '~/components/ui/icon/Icon'
// @ts-ignore
import logo from '~/assets/img/nav-logo-hvit.png'
import './BlankHeader.less'
import Logger from '~/logger'

export default () => {
	return (
		<header className="blank-header">
			<div className="home-nav">
				<div className="img-logo">
					<img alt="NAV logo" src={logo} />
				</div>
				<Icon size={30} kind="dolly" className="dollysheep" />
				<h1>Dolly</h1>
			</div>
			<div className="menu-links">
				<a href="https://navikt.github.io/testnorge/applications/dolly/" target="_blank">
					Dokumentasjon
				</a>
			</div>
		</header>
	)
}
