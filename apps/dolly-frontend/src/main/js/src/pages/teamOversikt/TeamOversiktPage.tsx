import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	InformationSquareFillIcon,
	LeaveIcon,
	PencilWritingIcon,
	TrashIcon,
	XMarkOctagonFillIcon,
} from '@navikt/aksel-icons'
import { TeamVisning } from '@/pages/teamOversikt/TeamVisning'
import styled from 'styled-components'
import useBoolean from '@/utils/hooks/useBoolean'
import { OpprettRedigerTeam } from '@/pages/teamOversikt/OpprettRedigerTeam'
import React, { useState } from 'react'
import { useBrukerTeams, useCurrentBruker } from '@/utils/hooks/useBruker'
import Loading from '@/components/ui/loading/Loading'
import { ForlatTeam } from '@/pages/teamOversikt/ForlatTeam'
import { SlettTeam } from '@/pages/teamOversikt/SlettTeam'
import { BliMedITeam } from '@/pages/teamOversikt/BliMedITeam'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { runningE2ETest } from '@/service/services/Request'
import { AdminAccessDenied } from '@/pages/adminPages/AdminAccessDenied'
import { Box, Button, InfoCard, Table } from '@navikt/ds-react'

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

	const bankIdBruker = currentBruker?.brukertype === 'BANKID'

	if (bankIdBruker) {
		return <AdminAccessDenied />
	}

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
			<Box background="default" padding="space-16">
				<ErrorBoundary>
					{loading ? (
						<Loading label="Laster team ..." />
					) : error ? (
						<InfoCard data-color="danger" size="small">
							<InfoCard.Message icon={<XMarkOctagonFillIcon aria-hidden />}>
								Feil ved henting av team: {error?.message}
							</InfoCard.Message>
						</InfoCard>
					) : brukerTeams?.length < 1 ? (
						<InfoCard data-color="info" size="small">
							<InfoCard.Message icon={<InformationSquareFillIcon aria-hidden />}>
								Du er ikke medlem av noen team. Bli med i et eksisterende team eller opprett ditt
								eget.
							</InfoCard.Message>
						</InfoCard>
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
										<Table.ExpandableRow
											key={team.id}
											content={<TeamVisning team={team} />}
											data-testid={TestComponentSelectors.EXPANDABLE_TEAM}
											expandOnRowClick={runningE2ETest()}
										>
											<Table.DataCell width="65%">{team.navn}</Table.DataCell>
											<Table.DataCell width="10%" align="center">
												<Button
													onClick={() => {
														setValgtTeam(team)
														openForlatTeamModal()
													}}
													variant="tertiary"
													icon={
														<LeaveIcon
															title={
																antallMedlemmer <= 1
																	? 'Du kan ikke forlate et team hvor du er eneste medlem'
																	: 'Forlat team'
															}
														/>
													}
													size="small"
													disabled={antallMedlemmer <= 1}
												/>
											</Table.DataCell>
											<Table.DataCell width="10%" align="center">
												<Button
													onClick={() => {
														setValgtTeam(team)
														openOpprettRedigerTeamModal()
													}}
													variant="tertiary"
													icon={<PencilWritingIcon title="Rediger team" />}
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
													icon={<TrashIcon title="Slett team" />}
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
						data-testid={TestComponentSelectors.BUTTON_TEAM_OPPRETT}
					>
						Opprett team
					</Button>
					<Button variant={'primary'} onClick={openBliMedITeamModal}>
						Bli med i team
					</Button>
				</Knappegruppe>
				{bliMedITeamModalIsOpen && (
					<BliMedITeam
						brukerId={currentBruker?.brukerId}
						brukerTeams={brukerTeams}
						closeModal={() => {
							closeBliMedITeamModal()
							return mutate()
						}}
						mutate={mutate}
					/>
				)}
				{forlatTeamModalIsOpen && valgtTeam && (
					<ForlatTeam
						team={valgtTeam}
						brukerId={currentBruker?.brukerId}
						closeModal={() => {
							closeForlatTeamModal()
							return mutate()
						}}
						mutate={mutate}
					/>
				)}
				{opprettRedigerTeamModalIsOpen && (
					<OpprettRedigerTeam
						team={valgtTeam}
						closeModal={() => {
							closeOpprettRedigerTeamModal()
							return mutate()
						}}
						mutate={mutate}
					/>
				)}
				{slettTeamModalIsOpen && valgtTeam && (
					<SlettTeam
						team={valgtTeam}
						closeModal={() => {
							closeSlettTeamModal()
							return mutate()
						}}
						mutate={mutate}
					/>
				)}
			</Box>
		</>
	)
}
