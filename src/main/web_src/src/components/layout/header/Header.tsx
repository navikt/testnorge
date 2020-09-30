import React from 'react'
import { NavLink } from 'react-router-dom'
import Icon from '~/components/ui/icon/Icon'
// @ts-ignore
import logo from '~/assets/img/nav-logo-hvit.png'
import './Header.less'
import Logger from '~/logger'
import { useLocation } from 'react-use'

type Props = {
	brukerData: {
		visningsNavn: string
		brukernavn: string
	}
	brukerBilde: any
}

export default ({ brukerData, brukerBilde }: Props) => {
	const location = useLocation()

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
				<NavLink
					isActive={() => location.pathname === '/' || location.pathname.includes('/gruppe')}
					to="/"
				>
					Testdatagrupper
				</NavLink>
				<NavLink to="/tpsendring">Endringsmelding</NavLink>
				<NavLink to="/soek">Søk i Mini-Norge</NavLink>
				<a
					href="https://navikt.github.io/dolly-frontend/"
					target="_blank"
					onClick={() => Logger.log({ event: 'Trykket på dokumentasjon header' })}
				>
					Dokumentasjon
				</a>
				<a href="/swagger-ui.html" target="_blank">
					API-dok
				</a>
			</div>

			<div className="profil-area flexbox--all-center">
				<NavLink to="/minside">
					<img alt="Profilbilde" src={brukerBilde && brukerBilde.url} />
					{brukerData.visningsNavn}
				</NavLink>
			</div>
		</header>
	)
}
