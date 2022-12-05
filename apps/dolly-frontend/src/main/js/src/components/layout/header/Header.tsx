import React from 'react'
import { NavLink } from 'react-router-dom'
import Icon from '~/components/ui/icon/Icon'
import Button from '~/components/ui/button/Button'
// @ts-ignore
import logo from '~/assets/img/nav-logo-hvit.png'
// @ts-ignore
import dolly from '~/favicon.ico'
import './Header.less'
import { useBrukerProfil, useBrukerProfilBilde, useCurrentBruker } from '~/utils/hooks/useBruker'
import logoutBruker from '~/components/utlogging/logoutBruker'
import { getDefaultImage } from '~/pages/minSide/Profil'
import Loading from '~/components/ui/loading/Loading'

export default () => {
	const { brukerProfil } = useBrukerProfil()
	const { brukerBilde } = useBrukerProfilBilde()
	const { currentBruker, loading } = useCurrentBruker()

	if (loading) {
		return <Loading label="Laster bruker" panel />
	}

	const bankidBruker = currentBruker?.brukertype === 'BANKID'
	return (
		<header className="app-header">
			<NavLink to="/" end className="home-nav">
				<div className="img-logo">
					<img alt="NAV logo" src={logo} />
				</div>
				<Icon size={30} kind="dolly" className="dollysheep" />
				<h1>Dolly</h1>
			</NavLink>

			<div className="menu-links">
				<NavLink to="/gruppe">Personer</NavLink>
				<NavLink to="/organisasjoner">Organisasjoner</NavLink>
				<NavLink to="/testnorge">Test-Norge</NavLink>
				{!bankidBruker && <NavLink to="/endringsmelding">Endringsmelding</NavLink>}
				<NavLink to="/dokumentasjon">Dokumentasjon</NavLink>
			</div>
			<div className="flexbox--all-center">
				<Button kind="logout" title="Logg ut" onClick={() => logoutBruker()} />
				<div className="profil-area flexbox--all-center">
					<NavLink to="/minside" key={'naviger-minside'}>
						<img alt="Profilbilde" src={brukerBilde || getDefaultImage()} />
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
