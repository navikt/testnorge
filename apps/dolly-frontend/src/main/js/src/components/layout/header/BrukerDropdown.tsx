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
import { DollyApi } from '@/service/Api'
import dollyTeam from '@/assets/img/dollyTeam.png'
import { teamVarslingLocalStorageKey } from '@/components/layout/header/TeamVarsel'

//TODO: Fiks Playwright-tester som feiler

//TODO: Test litt mer, rydd opp, push til dev

export const BrukerDropdown = () => {
	const { brukerProfil } = useBrukerProfil()
	const { brukerBilde } = useBrukerProfilBilde()

	const { currentBruker } = useCurrentBruker()
	const { brukerTeams, mutate } = useBrukerTeams()
	const gjeldendeTeam = currentBruker?.gjeldendeTeam

	const handleTeamChange = (teamId: string) => {
		DollyApi.setGjeldendeTeam(teamId)
			.then(() => {
				mutate()
			})
			.catch((error) => {
				console.error('Feil ved valg av gjeldende team: ', error)
			})
	}

	const handleFjernGjeldendeTeam = () => {
		localStorage.removeItem(teamVarslingLocalStorageKey)
		DollyApi.fjernGjeldendeTeam()
			.then(() => {
				mutate()
			})
			.catch((error) => {
				console.error('Feil ved valg av gjeldende bruker: ', error)
			})
	}

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
									src={gjeldendeTeam ? dollyTeam : brukerBilde || getDefaultImage()}
								/>
							</div>
							<p>{gjeldendeTeam ? gjeldendeTeam.navn : brukerProfil?.visningsNavn}</p>
						</div>
					</ActionMenu.Trigger>
				}
			>
				{/*TODO: Blir det dobbelt opp med group naar ActionMenuWrapper wrapper alt i en group?*/}
				<ActionMenu.Group label="Bruker-/team-valg">
					<ActionMenu.Item
						onClick={handleFjernGjeldendeTeam}
						style={{
							color: '#212529',
							backgroundColor: !gjeldendeTeam ? '#99C3FF' : null,
						}}
					>
						<DropdownStyledIcon kind="person" fontSize="1.5rem" />
						<DropdownStyledLink href="">
							{brukerProfil?.visningsNavn + (!gjeldendeTeam ? ' (valgt)' : '')}
						</DropdownStyledLink>
					</ActionMenu.Item>
					{brukerTeams?.map((team) => (
						<ActionMenu.Item
							onClick={() => handleTeamChange(team.id)}
							key={team.id}
							style={{
								color: '#212529',
								backgroundColor: gjeldendeTeam?.id === team.id ? '#99C3FF' : null,
							}}
						>
							<DropdownStyledIcon kind="group" fontSize="1.5rem" />
							<DropdownStyledLink href="">
								{team.navn + (gjeldendeTeam?.id === team.id ? ' (valgt)' : '')}
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
