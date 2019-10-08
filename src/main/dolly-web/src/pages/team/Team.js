import React from 'react'
import { useMount } from 'react-use'
import useBoolean from '~/utils/hooks/useBoolean'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import Toolbar from '~/components/ui/toolbar/Toolbar'
import Knapp from 'nav-frontend-knapper'
import Loading from '~/components/ui/loading/Loading'
import LeggTilBruker from './LeggTilBruker/LeggTilBruker'
import ConfirmTooltip from '~/components/ui/confirmTooltip/ConfirmTooltip'
import RedigerTeamConnector from '~/components/RedigerTeam/RedigerTeamConnector'
import TeamMedlemmer from './teamMedlemmer/TeamMedlemmer'
import TeamGrupper from './teamGrupper/TeamGrupper'

import './Team.less'

export default function Team({
	getTeam,
	getGrupperByTeamId,
	team,
	grupper,
	isFetching,
	history,
	addMember,
	removeMember,
	deleteTeam,
	isDeletingTeam
}) {
	const [visRedigerTeamState, visRediger, skjulRediger] = useBoolean(false)
	const [visLeggTilBrukerState, visLeggTilBruker, skjulLeggTilBruker] = useBoolean(false)

	useMount(() => {
		getTeam()
		getGrupperByTeamId()
	})

	if (isFetching) return <Loading label="laster team og grupper" panel />

	if (!team) return null

	const teamMembers = team.medlemmer.map(medlem => medlem.navIdent)

	const teamActions = [
		{
			icon: 'edit',
			label: 'REDIGER',
			onClick: visRediger
		}
	]

	return (
		<div className="oversikt-container">
			<Overskrift label={team.navn} actions={teamActions}>
				{isDeletingTeam ? (
					<Loading label="Sletter team" panel />
				) : (
					<ConfirmTooltip
						label="SLETT"
						className="flexbox--align-center"
						message={
							grupper.length > 0
								? 'Å slette dette teamet vil føre til sletting av ' +
								  grupper.length +
								  ' testdatagrupper . Er du sikker på dette?'
								: 'Vil du slette dette teamet?'
						}
						onClick={deleteTeam}
					/>
				)}
			</Overskrift>
			<div style={{ width: '70%' }} className="Beskrivelse">
				{team.beskrivelse}
			</div>
			{visRedigerTeamState && <RedigerTeamConnector team={team} onCancel={skjulRediger} />}

			<Toolbar title="Medlemmer">
				<Knapp type="hoved" onClick={visLeggTilBruker}>
					Nytt medlem
				</Knapp>
			</Toolbar>

			{visLeggTilBrukerState && (
				<LeggTilBruker
					teamId={team.id}
					teamMembers={teamMembers}
					closeLeggTilBruker={skjulLeggTilBruker}
					addMember={addMember}
				/>
			)}

			<TeamMedlemmer medlemmer={team.medlemmer} removeMember={removeMember} />

			<Overskrift label="Testdatagrupper" type="h2" />
			<TeamGrupper grupper={grupper} history={history} />
		</div>
	)
}
