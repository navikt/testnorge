import React, { useContext } from 'react'
import { useNavigate } from 'react-router-dom'
import './Header.less'
import { useBrukerProfil, useBrukerProfilBilde } from '~/utils/hooks/useBruker'
import logoutBruker from '~/components/utlogging/logoutBruker'
import { getDefaultImage } from '~/pages/minSide/Profil'
import { Dropdown, DropdownContext } from '@navikt/ds-react-internal'
import Icon from '~/components/ui/icon/Icon'

const DropdownToggle = () => {
	const { brukerProfil } = useBrukerProfil()
	const { brukerBilde } = useBrukerProfilBilde()

	const context = useContext(DropdownContext)
	const { isOpen } = context

	return (
		<Dropdown.Toggle className={isOpen ? 'profil-toggle active' : 'profil-toggle'}>
			<div className="profil-area flexbox--all-center">
				<div className="img-logo">
					<img alt="Profilbilde" src={brukerBilde || getDefaultImage()} />
				</div>
				<div className="profil-navn">
					<p>{brukerProfil?.visningsNavn}</p>
				</div>
			</div>
		</Dropdown.Toggle>
	)
}

export const DropdownMenu = () => {
	const navigate = useNavigate()
	return (
		<header className="app-header">
			<Dropdown>
				<DropdownToggle />
				<Dropdown.Menu style={{ position: 'absolute', right: '0', top: '61px', width: '150px' }}>
					<Dropdown.Menu.List>
						<Dropdown.Menu.List.Item onClick={() => navigate('/minside')}>
							<Icon kind="person" size={16} />
							Min side
						</Dropdown.Menu.List.Item>
						<Dropdown.Menu.List.Item onClick={() => logoutBruker()}>
							<Icon kind="logout" size={16} />
							Logg ut
						</Dropdown.Menu.List.Item>
					</Dropdown.Menu.List>
				</Dropdown.Menu>
			</Dropdown>
		</header>
	)
}
