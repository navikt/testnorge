import { Alert, Box, Button, Table } from '@navikt/ds-react'
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
import { ForlatTeam } from '@/pages/teamOversikt/ForlatTeam'
import { SlettTeam } from '@/pages/teamOversikt/SlettTeam'
import { BliMedITeam } from '@/pages/teamOversikt/BliMedITeam'

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
	const [bliMedITeamModalIsOpen, openBliMedITeamModal, closeBliMedITeamModal] = useBoolean(false)
	const [forlatTeamModalIsOpen, openForlatTeamModal, closeForlatTeamModal] = useBoolean(false)
	const [slettTeamModalIsOpen, openSlettTeamModal, closeSlettTeamModal] = useBoolean(false)

	return (
		<>
			<h1>Team-oversikt</h1>
			<p>
				Her finner du en oversikt over alle teamene du er med i. Et team er en gruppe Dolly-brukere
				som samarbeider om de samme dataene. Du kan også opprette nye team eller velge eksisterende
				team du vil bli med i.
			</p>
			<p style={{ marginBottom: '20px' }}>
				I brukermenyen øverst til høyre kan du når som helst velge hvilket team du ønsker å
				representere. Dette vil si at når du har valgt et team der, vil alle grupper, personer,
				maler, osv. du oppretter tilhøre valgt team, og ikke deg selv.
			</p>
			<Box background="surface-default" padding="4">
				<ErrorBoundary>
					{loading ? (
						<Loading label="Laster team ..." />
					) : brukerTeams?.length < 1 ? (
						<Alert variant="info" size="small">
							Du er ikke medlem av noen team. Bli med i et eksisterende team eller opprett ditt
							eget.
						</Alert>
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
									const antallMedlemmer = team.brukere?.length || 0
									return (
										<Table.ExpandableRow key={team.id} content={<TeamVisning team={team} />}>
											<Table.DataCell width="65%">{team.navn}</Table.DataCell>
											<Table.DataCell width="10%" align="center">
												<Button
													onClick={() => {
														setValgtTeam(team)
														openForlatTeamModal()
													}}
													variant="tertiary"
													icon={<LeaveIcon fontSize="1.5rem" />}
													size="small"
													disabled={antallMedlemmer <= 1}
													title={
														antallMedlemmer <= 1
															? 'Du kan ikke forlate et team hvor du er eneste medlem'
															: ''
													}
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
													onClick={() => {
														setValgtTeam(team)
														openSlettTeamModal()
													}}
													variant="tertiary"
													icon={<TrashIcon fontSize="1.5rem" />}
													size="small"
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
					<Button variant={'primary'} onClick={openBliMedITeamModal}>
						Bli med i team
					</Button>
				</Knappegruppe>
				<DollyModal isOpen={bliMedITeamModalIsOpen} closeModal={closeBliMedITeamModal} width="60%">
					<BliMedITeam
						brukerId={currentBruker?.brukerId}
						brukerTeams={brukerTeams}
						closeModal={() => {
							closeBliMedITeamModal()
							return mutate()
						}}
						mutate={mutate}
					/>
				</DollyModal>
				<DollyModal isOpen={forlatTeamModalIsOpen} closeModal={closeForlatTeamModal} width="40%">
					<ForlatTeam
						team={valgtTeam}
						brukerId={currentBruker?.brukerId}
						closeModal={() => {
							closeForlatTeamModal()
							return mutate()
						}}
						mutate={mutate}
					/>
				</DollyModal>
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
				<DollyModal isOpen={slettTeamModalIsOpen} closeModal={closeSlettTeamModal} width="40%">
					<SlettTeam
						team={valgtTeam}
						closeModal={() => {
							closeSlettTeamModal()
							return mutate()
						}}
						mutate={mutate}
					/>
				</DollyModal>
			</Box>
		</>
	)
}
