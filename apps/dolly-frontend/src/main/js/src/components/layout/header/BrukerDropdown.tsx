import React, { useContext } from 'react'
import { useNavigate } from 'react-router-dom'
import './Header.less'
import { useBrukerProfil, useBrukerProfilBilde } from '@/utils/hooks/useBruker'
import logoutBruker from '@/components/utlogging/logoutBruker'
import { getDefaultImage } from '@/pages/minSide/Profil'
import { Dropdown, DropdownContext } from '@navikt/ds-react-internal'
import Icon from '@/components/ui/icon/Icon'
import styled from 'styled-components'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'

const StyledIcon = styled(Icon)`
	&& {
		g {
			path,
			ellipse {
				stroke: #212529;
				stroke-width: 2;
			}
		}
	}
`

const DropdownToggle = () => {
	const { brukerProfil } = useBrukerProfil()
	const { brukerBilde } = useBrukerProfilBilde()

	const context = useContext(DropdownContext)
	const { isOpen } = context

	return (
		<Dropdown.Toggle className={isOpen ? 'dropdown-toggle active' : 'dropdown-toggle'}>
			<div className="profil-area">
				<div className="img-logo">
					<img
						data-testid={CypressSelector.BUTTON_PROFIL}
						alt="Profilbilde"
						src={brukerBilde || getDefaultImage()}
					/>
				</div>
				<div className="profil-navn">
					<p>{brukerProfil?.visningsNavn}</p>
				</div>
			</div>
		</Dropdown.Toggle>
	)
}

export const BrukerDropdown = () => {
	const navigate = useNavigate()
	return (
		<div style={{ margin: '0 10px' }}>
			<Dropdown>
				<DropdownToggle />
				<Dropdown.Menu placement="bottom-end">
					<Dropdown.Menu.List>
						<Dropdown.Menu.List.Item
							onClick={() => navigate('/minside')}
							style={{ color: '#212529' }}
							data-testid={CypressSelector.BUTTON_PROFIL_MINSIDE}
						>
							<StyledIcon kind="person" fontSize={'1.5rem'} />
							Min side
						</Dropdown.Menu.List.Item>
						<Dropdown.Menu.List.Item onClick={() => logoutBruker()} style={{ color: '#212529' }}>
							<Icon kind="logout" fontSize={'1.5rem'} />
							Logg ut
						</Dropdown.Menu.List.Item>
					</Dropdown.Menu.List>
				</Dropdown.Menu>
			</Dropdown>
		</div>
	)
}
