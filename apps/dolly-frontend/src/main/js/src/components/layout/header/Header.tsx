import React from 'react'
import { NavLink } from 'react-router-dom'
import Icon from '~/components/ui/icon/Icon'
// @ts-ignore
import logo from '~/assets/img/nav-logo-hvit.png'
// @ts-ignore
import dolly from '~/assets/favicon.ico'
import './Header.less'
import Logger from '~/logger'
import { useLocation } from 'react-use'

type Props = {
	brukerProfil: {
		visningsNavn: string
		brukernavn: string
	}
	brukerBilde: Response
}

export default ({ brukerProfil, brukerBilde }: Props) => {
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
					Testpersoner
				</NavLink>
				<NavLink to="/organisasjoner">Testorganisasjoner</NavLink>
				<a
					href="https://endringsmelding.dev.intern.nav.no"
					onClick={() => Logger.log({ event: 'Trykket på dokumentasjon header' })}
				>
					Endringsmelding
				</a>
				<NavLink to="/soek">Søk</NavLink>
				<a
					href="https://navikt.github.io/dolly-frontend/"
					target="_blank"
					onClick={() => Logger.log({ event: 'Trykket på dokumentasjon header' })}
				>
					Dokumentasjon
				</a>
				<a
					href="https://dolly-backend.dev.adeo.no/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config"
					target="_blank"
				>
					API-dok
				</a>
			</div>

			<div className="profil-area flexbox--all-center">
				<NavLink to="/minside">
					<img alt="Profilbilde" src={brukerBilde ? brukerBilde.url : dolly} />
					<div className="profil-navn">
						<p className="min-side">MIN SIDE</p>
						<p>{brukerProfil && brukerProfil.visningsNavn}</p>
					</div>
				</NavLink>
			</div>
		</header>
	)
}
