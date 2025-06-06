import React from 'react'
import './Header.less'
import {
	useBrukerProfil,
	useBrukerProfilBilde,
	useBrukerTeams,
	useCurrentBruker,
} from '@/utils/hooks/useBruker'
import logoutBruker from '@/components/utlogging/logoutBruker'
import { getDefaultImage } from '@/pages/minSide/Profil'
import { ActionMenu } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { ActionMenuWrapper, DropdownStyledIcon, DropdownStyledLink } from './ActionMenuWrapper'
import { PreloadableActionMenuItem } from '@/utils/PreloadableActionMenuItem'

export const BrukerDropdown = () => {
	const { brukerProfil } = useBrukerProfil()
	const { brukerBilde } = useBrukerProfilBilde()

	const { currentBruker } = useCurrentBruker()
	const { brukerTeams, loading, error, mutate } = useBrukerTeams()

	console.log('brukerTeams: ', brukerTeams) //TODO - SLETT MEG

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
							<p>{brukerProfil?.visningsNavn}</p>
						</div>
					</ActionMenu.Trigger>
				}
			>
				{/*TODO: Blir det dobbelt opp med group naar ActionMenuWrapper wrapper alt i en group?*/}
				<ActionMenu.Group label="Representasjon">
					<ActionMenu.Item
						onClick={() => {}}
						style={{
							color: '#212529',
							backgroundColor: !currentBruker?.gjeldendeTeam ? '#99C3FF' : null,
						}}
					>
						<DropdownStyledIcon kind="person" fontSize="1.5rem" />
						<DropdownStyledLink href="">
							{brukerProfil?.visningsNavn + (!currentBruker?.gjeldendeTeam ? ' (valgt)' : '')}
						</DropdownStyledLink>
					</ActionMenu.Item>
					{brukerTeams?.map((team) => (
						<ActionMenu.Item
							onClick={() => {}}
							key={team.id}
							style={{
								color: '#212529',
								backgroundColor: currentBruker?.gjeldendeTeam === team.id ? '#99C3FF' : null,
							}}
						>
							<DropdownStyledIcon kind="group" fontSize="1.5rem" />
							<DropdownStyledLink href="">
								{team.navn + (currentBruker?.gjeldendeTeam === team.id ? ' (valgt)' : '')}
							</DropdownStyledLink>
						</ActionMenu.Item>
					))}
				</ActionMenu.Group>
				<ActionMenu.Divider />
				<ActionMenu.Group label="Administrasjon">
					<PreloadableActionMenuItem
						route="/minside"
						style={{ color: '#212529' }}
						dataTestId={TestComponentSelectors.BUTTON_PROFIL_MINSIDE}
					>
						<DropdownStyledIcon kind="person" fontSize="1.5rem" />
						<DropdownStyledLink href="/minside">Min side</DropdownStyledLink>
					</PreloadableActionMenuItem>
					<PreloadableActionMenuItem route="/team" style={{ color: '#212529' }}>
						<DropdownStyledIcon kind="group" fontSize="1.5rem" />
						<DropdownStyledLink href="/team">Team-oversikt</DropdownStyledLink>
					</PreloadableActionMenuItem>
				</ActionMenu.Group>
				<ActionMenu.Divider />
				<ActionMenu.Item onClick={() => logoutBruker()} style={{ color: '#212529' }}>
					<DropdownStyledIcon kind="logout" fontSize="1.5rem" />
					<DropdownStyledLink href="#">Logg ut</DropdownStyledLink>
				</ActionMenu.Item>
			</ActionMenuWrapper>
		</span>
	)
}
