import React from 'react'
import './Header.less'
import { useBrukerProfil, useBrukerProfilBilde } from '@/utils/hooks/useBruker'
import logoutBruker from '@/components/utlogging/logoutBruker'
import { getDefaultImage } from '@/pages/minSide/Profil'
import { ActionMenu } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { ActionMenuWrapper, DropdownStyledIcon, DropdownStyledLink } from './ActionMenuWrapper'
import { PreloadableActionMenuItem } from '@/utils/PreloadableActionMenuItem'

const myTeamsMock = [
	{
		name: 'Team Dolly',
		id: 'team-dolly',
	},
	{
		name: 'Team Black Sheep',
		id: 'team-black-sheep',
	},
]

const currentMock = {
	brukerId: '952ab92e-926f-4ac4-93d7-f2d552025caf',
	brukernavn: 'Traran, Betsy Carina',
	brukertype: 'AZURE',
	epost: 'Betsy.Carina.Traran@nav.no',
	grupper: ['2d7f1c0d-5784-4f81-8bb2-8f3a79f8f949', '9c7efec1-1599-4216-a67e-6fd53a6a951c'],
	representerer: null,
	// representerer: 'team-dolly',
}

export const BrukerDropdown = () => {
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
							backgroundColor: !currentMock?.representerer ? '#99C3FF' : null,
						}}
					>
						<DropdownStyledIcon kind="person" fontSize="1.5rem" />
						<DropdownStyledLink href="">
							{brukerProfil?.visningsNavn + (!currentMock?.representerer ? ' (valgt)' : '')}
						</DropdownStyledLink>
					</ActionMenu.Item>
					{myTeamsMock?.map((team) => (
						<ActionMenu.Item
							onClick={() => {}}
							key={team.id}
							style={{
								color: '#212529',
								backgroundColor: currentMock?.representerer === team.id ? '#99C3FF' : null,
							}}
						>
							<DropdownStyledIcon kind="group" fontSize="1.5rem" />
							<DropdownStyledLink href="">
								{team.name + (currentMock?.representerer === team.id ? ' (valgt)' : '')}
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
