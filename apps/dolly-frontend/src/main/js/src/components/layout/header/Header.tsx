import React from 'react'
import { NavLink } from 'react-router-dom'
import Icon from '~/components/ui/icon/Icon'
// @ts-ignore
import logo from '~/assets/img/nav-logo-hvit.png'
import './Header.less'
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import Loading from '~/components/ui/loading/Loading'
import { BrukerDropdown } from '~/components/layout/header/BrukerDropdown'
import { DokumentasjonDropdown } from '~/components/layout/header/DokumentasjonDropdown'

export default () => {
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
				<DokumentasjonDropdown />
			</div>
			<BrukerDropdown />
		</header>
	)
}
