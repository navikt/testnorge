import React from 'react'
import { NavLink } from 'react-router-dom'
import Icon from '~/components/ui/icon/Icon'
import Button from '~/components/ui/button/Button'
// @ts-ignore
import logo from '~/assets/img/nav-logo-hvit.png'
// @ts-ignore
import dolly from '~/assets/favicon.ico'
import './Header.less'
import Logger from '~/logger'
import { useBrukerProfil, useBrukerProfilBilde } from '~/utils/hooks/useBruker'
import logoutBruker from '~/components/utlogging/logoutBruker'
import { Alert } from '@navikt/ds-react'

export default () => {
	const { brukerProfil } = useBrukerProfil()
	const { brukerBilde } = useBrukerProfilBilde()

	return (
		<header className="app-header">
			<NavLink to="/" end className="home-nav">
				<div className="img-logo">
					<img alt="NAV logo" src={logo} />
				</div>
				<Icon size={30} kind="dolly" className="dollysheep" />
				<h1>Dolly</h1>
			</NavLink>
			
			<Alert variant={ "warning"}>0 Dager siden siste Betsy bug i master</Alert>

			<div className="menu-links">
				<NavLink to="/gruppe">Personer</NavLink>
				<NavLink to="/organisasjoner">Organisasjoner</NavLink>
				<NavLink to="/testnorge">Test-Norge</NavLink>
				<NavLink to="/endringsmelding">Endringsmelding</NavLink>
				<a
					href="https://navikt.github.io/testnorge/applications/dolly/"
					target="_blank"
					onClick={() => Logger.log({ event: 'Trykket på dokumentasjon header' })}
				>
					Dokumentasjon
				</a>
				<a
					href={
						window.location.hostname.includes('frontend')
							? 'https://dolly-backend-dev.dev.intern.nav.no/swagger'
							: 'https://dolly-backend.dev.intern.nav.no/swagger'
					}
					target="_blank"
				>
					API-dok
				</a>
			</div>
			<div className="flexbox--all-center">
				<Button kind="logout" title="Logg ut" onClick={() => logoutBruker()} />
				<div className="profil-area flexbox--all-center">
					<NavLink to="/minside">
						<img alt="Profilbilde" src={brukerBilde || dolly} />
						<div className="profil-navn">
							<p className="min-side">MIN SIDE</p>
							<p>{brukerProfil?.visningsNavn}</p>
						</div>
					</NavLink>
				</div>
			</div>
		</header>
	)
}
