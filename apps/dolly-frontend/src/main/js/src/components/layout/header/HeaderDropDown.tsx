import React from 'react'
// @ts-ignore
import logo from '~/assets/img/nav-logo-hvit.png'
// @ts-ignore
import dolly from '~/assets/favicon.ico'
import { useNavigate } from 'react-router-dom'
import './Header.less'
import { useBrukerProfil, useBrukerProfilBilde } from '~/utils/hooks/useBruker'
import logoutBruker from '~/components/utlogging/logoutBruker'
import { getDefaultImage } from '~/pages/minSide/Profil'
import { Dropdown } from '@navikt/ds-react-internal'

export default () => {
	const { brukerProfil } = useBrukerProfil()
	const { brukerBilde } = useBrukerProfilBilde()

	const navigate = useNavigate()
	return (
		<header className="app-header">
			<Dropdown>
				<Dropdown.Toggle className="profil-toggle">
					<div className="profil-area flexbox--all-center">
						<div className="img-logo">
							<img alt="Profilbilde" src={brukerBilde || getDefaultImage()} />
						</div>
						<div className="profil-navn">
							<p>{brukerProfil?.visningsNavn}</p>
						</div>
					</div>
				</Dropdown.Toggle>
				<Dropdown.Menu>
					<Dropdown.Menu.List>
						<Dropdown.Menu.List.Item onClick={() => navigate('/minside')}>
							Min side
						</Dropdown.Menu.List.Item>
						<Dropdown.Menu.List.Item onClick={() => logoutBruker()}>
							Logg ut
						</Dropdown.Menu.List.Item>
					</Dropdown.Menu.List>
				</Dropdown.Menu>
			</Dropdown>
		</header>
	)
}
