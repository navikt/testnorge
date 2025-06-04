import { Box, Button, Table } from '@navikt/ds-react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { LeaveIcon, PencilWritingIcon, TrashIcon } from '@navikt/aksel-icons'
// import { useBrukerProfil } from '@/utils/hooks/useBruker'
import { TeamVisning } from '@/pages/teamOversikt/TeamVisning'
import styled from 'styled-components'
import useBoolean from '@/utils/hooks/useBoolean'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import { OpprettRedigerTeam } from '@/pages/teamOversikt/OpprettRedigerTeam'
import { useState } from 'react'

const teamMock = [
	{
		id: '1',
		navn: 'Team Dolly',
		beskrivelse: 'Test av team-funksjonalitet',
		brukere: [
			'7915badd-1963-433c-8825-eb6e9bdc703e',
			'952ab92e-926f-4ac4-93d7-f2d552025caf',
			'c9ebdce0-ca6f-4964-a9fb-4ae84f4ed56c',
			'4d59f076-721c-48f3-b80e-b604826d74d0',
			'7f0223cd-062e-4636-99e2-7229f9b7dc2f',
		],
		opprettetAv: '7915badd-1963-433c-8825-eb6e9bdc703e',
	},
	{
		id: '2',
		navn: 'Team Black Sheep',
		beskrivelse: 'Bare fjas',
		brukere: ['952ab92e-926f-4ac4-93d7-f2d552025caf', '7f0223cd-062e-4636-99e2-7229f9b7dc2f'],
		opprettetAv: '952ab92e-926f-4ac4-93d7-f2d552025caf',
	},
]

const Knappegruppe = styled.div`
	margin-top: 15px;
	display: flex;
	gap: 15px;
	//align-content: baseline;
`

export default () => {
	// const { brukerProfil } = useBrukerProfil()
	// console.log('brukerProfil: ', brukerProfil) //TODO - SLETT MEG

	const [valgtTeam, setValgtTeam] = useState(null)

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
									Forlat
								</Table.HeaderCell>
								<Table.HeaderCell scope="col" align="center">
									Rediger
								</Table.HeaderCell>
								<Table.HeaderCell scope="col" align="center">
									Slett
								</Table.HeaderCell>
							</Table.Row>
						</Table.Header>
						<Table.Body>
							{teamMock.map((team) => (
								<Table.ExpandableRow key={team.id} content={<TeamVisning team={team} />}>
									{/*TODO: Bruk brukerprofil for aa sjekke om admin, vis (admin) etter team-navn*/}
									{/*TODO: Slett og rediger hvis admin, forlat hvis ikke admin*/}
									<Table.DataCell width="70%">{team.navn}</Table.DataCell>
									<Table.DataCell width="10%" align="center">
										<Button
											onClick={() => alert(`Forlat ${team.navn}`)}
											variant="tertiary"
											icon={<LeaveIcon fontSize="1.5rem" />}
											size="small"
										/>
									</Table.DataCell>
									<Table.DataCell width="10%" align="center">
										<Button
											onClick={() => {
												setValgtTeam(team)
												openOpprettRedigerTeamModal()
											}}
											variant="tertiary"
											icon={<PencilWritingIcon fontSize="1.5rem" />}
											size="small"
										/>
									</Table.DataCell>
									<Table.DataCell width="10%" align="center">
										<Button
											onClick={() => alert(`Slett ${team.navn}`)}
											variant="tertiary"
											icon={<TrashIcon fontSize="1.5rem" />}
											size="small"
										/>
									</Table.DataCell>
								</Table.ExpandableRow>
							))}
						</Table.Body>
					</Table>
				</ErrorBoundary>
				<Knappegruppe>
					<Button
						variant={'primary'}
						onClick={() => {
							setValgtTeam(null)
							openOpprettRedigerTeamModal()
						}}
					>
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
					<OpprettRedigerTeam team={valgtTeam} closeModal={closeOpprettRedigerTeamModal} />
				</DollyModal>
			</Box>
		</>
	)
}
