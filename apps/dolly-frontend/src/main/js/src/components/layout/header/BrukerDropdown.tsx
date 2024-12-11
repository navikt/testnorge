import React from 'react'
import { useNavigate } from 'react-router-dom'
import './Header.less'
import { useBrukerProfil, useBrukerProfilBilde } from '@/utils/hooks/useBruker'
import logoutBruker from '@/components/utlogging/logoutBruker'
import { getDefaultImage } from '@/pages/minSide/Profil'
import { ActionMenu } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { ActionMenuWrapper, DropdownStyledIcon, DropdownStyledLink } from './ActionMenuWrapper'

export const BrukerDropdown = () => {
	const navigate = useNavigate()
	const { brukerProfil } = useBrukerProfil()
	const { brukerBilde } = useBrukerProfilBilde()

	return (
		<span className={'dropdown-toggle'}>
			<ActionMenuWrapper
				title="Bruker"
				trigger={
					<ActionMenu.Trigger style={{ hover: 'blue' }}>
						<div className="profil-area">
							<div className="img-logo">
								<img
									data-testid={TestComponentSelectors.BUTTON_PROFIL}
									alt="Profilbilde"
									src={brukerBilde || getDefaultImage()}
								/>
							</div>
							<div className="profil-navn">
								<p>{brukerProfil?.visningsNavn}</p>
							</div>
						</div>
					</ActionMenu.Trigger>
				}
			>
				<ActionMenu.Item
					onClick={() => navigate('/minside')}
					style={{ color: '#212529' }}
					data-testid={TestComponentSelectors.BUTTON_PROFIL_MINSIDE}
				>
					<DropdownStyledIcon kind="person" fontSize="1.5rem" />
					<DropdownStyledLink href="/minside">Min side</DropdownStyledLink>
				</ActionMenu.Item>
				<ActionMenu.Item onClick={() => logoutBruker()} style={{ color: '#212529' }}>
					<DropdownStyledIcon kind="logout" fontSize="1.5rem" />
					<DropdownStyledLink href="#">Logg ut</DropdownStyledLink>
				</ActionMenu.Item>
			</ActionMenuWrapper>
		</span>
	)
}
