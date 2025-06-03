import { Box, Button, Table } from '@navikt/ds-react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { LeaveIcon, PencilWritingIcon } from '@navikt/aksel-icons'
import { useBrukerProfil } from '@/utils/hooks/useBruker'
import { TeamVisning } from '@/pages/teamOversikt/TeamVisning'
import styled from 'styled-components'
import useBoolean from '@/utils/hooks/useBoolean'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import { OpprettRedigerTeam } from '@/pages/teamOversikt/OpprettRedigerTeam'

const teamMock = [
	{
		id: '1',
		navn: 'Team Dolly',
		hensikt: 'Test av team-funksjonalitet',
		medlemmer: [
			'Marstrander, Anders',
			'Traran, Betsy Carina',
			'Olsen, Cato',
			'Hærum, Kristen',
			'Gustavsson, Stian',
		],
		admin: 'Marstrander, Anders',
	},
	{
		id: '2',
		navn: 'Team Black Sheep',
		hensikt: 'Bare fjas',
		medlemmer: ['Traran, Betsy Carina', 'Gustavsson, Stian'],
		admin: 'Traran, Betsy Carina',
	},
]

const Knappegruppe = styled.div`
	margin-top: 15px;
	display: flex;
	gap: 15px;
	//align-content: baseline;
`

export default () => {
	const { brukerProfil } = useBrukerProfil()
	console.log('brukerProfil: ', brukerProfil) //TODO - SLETT MEG

	const [opprettRedigerTeamModalIsOpen, openOpprettRedigerTeamModal, closeOpprettRedigerTeamModal] =
		useBoolean(false)

	return (
		<>
			<h1>Team-oversikt</h1>
			<p>
				Her finner du en oversikt over alle teamene du er med i og/eller er administrator for. Du
				kan også opprette nye team eller velge eksisterende team du vil bli med i.
			</p>
			<Box background="surface-default" padding="4">
				<ErrorBoundary>
					<Table>
						<Table.Header>
							<Table.Row>
								<Table.HeaderCell />
								<Table.HeaderCell scope="col">Mine team</Table.HeaderCell>
								<Table.HeaderCell scope="col" align="center">
									Rediger
								</Table.HeaderCell>
								<Table.HeaderCell scope="col" align="center">
									Forlat
								</Table.HeaderCell>
								{/*	TODO: Slett team?*/}
							</Table.Row>
						</Table.Header>
						<Table.Body>
							{teamMock.map((team) => (
								<Table.ExpandableRow key={team.id} content={<TeamVisning team={team} />}>
									{/*TODO: Bruk brukerprofil for aa sjekke om admin, vis (admin) etter team-navn*/}
									<Table.DataCell width="75%">{team.navn}</Table.DataCell>
									<Table.DataCell width="15%" align="center">
										<Button
											onClick={() => alert(`Rediger ${team.navn}`)}
											variant="tertiary"
											icon={<PencilWritingIcon fontSize="1.5rem" />}
											size="small"
										/>
									</Table.DataCell>
									<Table.DataCell width="15%" align="center">
										<Button
											onClick={() => alert(`Forlat ${team.navn}`)}
											variant="tertiary"
											icon={<LeaveIcon fontSize="1.5rem" />}
											size="small"
										/>
									</Table.DataCell>
								</Table.ExpandableRow>
							))}
						</Table.Body>
					</Table>
				</ErrorBoundary>
				<Knappegruppe>
					<Button variant={'primary'} onClick={openOpprettRedigerTeamModal}>
						Opprett team
					</Button>
					<Button variant={'primary'} onClick={() => alert('Bli med i team')}>
						Bli med i team
					</Button>
				</Knappegruppe>
				<DollyModal
					isOpen={opprettRedigerTeamModalIsOpen}
					closeModal={closeOpprettRedigerTeamModal}
					width="60%"
				>
					<OpprettRedigerTeam closeModal={closeOpprettRedigerTeamModal} />
				</DollyModal>
			</Box>
		</>
	)
}
