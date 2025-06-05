import { Box, Button, Table, Tag } from '@navikt/ds-react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { LeaveIcon, PencilWritingIcon, TrashIcon } from '@navikt/aksel-icons'
import { TeamVisning } from '@/pages/teamOversikt/TeamVisning'
import styled from 'styled-components'
import useBoolean from '@/utils/hooks/useBoolean'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import { OpprettRedigerTeam } from '@/pages/teamOversikt/OpprettRedigerTeam'
import React, { useState } from 'react'
import { useBrukerTeams, useCurrentBruker } from '@/utils/hooks/useBruker'
import Loading from '@/components/ui/loading/Loading'

const Knappegruppe = styled.div`
	margin-top: 20px;
	display: flex;
	gap: 15px;
`

export default () => {
	const { currentBruker } = useCurrentBruker()

	const { brukerTeams, loading, error, mutate } = useBrukerTeams()

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
					{loading ? (
						<Loading label="Laster team ..." />
					) : (
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
								{brukerTeams?.map((team) => {
									const erAdmin = team.opprettetAv?.brukerId === currentBruker?.brukerId
									return (
										<Table.ExpandableRow key={team.id} content={<TeamVisning team={team} />}>
											<Table.DataCell width="65%">
												{team.navn}
												{erAdmin && (
													<Tag variant="info-moderate" size="small" style={{ marginLeft: '10px' }}>
														Admin
													</Tag>
												)}
											</Table.DataCell>
											<Table.DataCell width="10%" align="center">
												<Button
													onClick={() => alert(`Forlat ${team.navn}`)}
													variant="tertiary"
													icon={<LeaveIcon fontSize="1.5rem" />}
													size="small"
													disabled={erAdmin}
													title={erAdmin ? 'Du kan ikke forlate et team du er admin for' : ''}
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
													disabled={!erAdmin}
													title={
														!erAdmin ? 'Du kan ikke redigere et team du ikke er admin for' : ''
													}
												/>
											</Table.DataCell>
											<Table.DataCell width="10%" align="center">
												<Button
													onClick={() => alert(`Slett ${team.navn}`)}
													variant="tertiary"
													icon={<TrashIcon fontSize="1.5rem" />}
													size="small"
													disabled={!erAdmin}
													title={!erAdmin ? 'Du kan ikke slette et team du ikke er admin for' : ''}
												/>
											</Table.DataCell>
										</Table.ExpandableRow>
									)
								})}
							</Table.Body>
						</Table>
					)}
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
					<OpprettRedigerTeam
						team={valgtTeam}
						closeModal={() => {
							closeOpprettRedigerTeamModal()
							return mutate()
						}}
						mutate={mutate}
					/>
				</DollyModal>
			</Box>
		</>
	)
}
