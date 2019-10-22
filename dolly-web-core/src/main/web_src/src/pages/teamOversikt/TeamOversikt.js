import React, { useState, useEffect } from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Knapp from 'nav-frontend-knapper'
import HjelpeTekst from 'nav-frontend-hjelpetekst'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import Toolbar from '~/components/ui/toolbar/Toolbar'
import RedigerTeamConnector from '~/components/RedigerTeam/RedigerTeamConnector'
import TeamListe from './TeamListe'
import SearchFieldConnector from '~/components/searchField/SearchFieldConnector'

export default function TeamOversikt({
	fetchAllTeams,
	fetchTeamsForUser,
	teamListe,
	history,
	searchActive,
	isFetching
}) {
	const [visning, setVisning] = useState('mine')
	const [opprettTeamState, visOpprettTeam, skjulOpprettTeam] = useBoolean(false)

	useEffect(() => {
		visning === 'mine' ? fetchTeamsForUser() : fetchAllTeams()
	}, [visning])

	const byttVisning = event => setVisning(event.target.value)

	return (
		<div className="oversikt-container">
			<div className="page-header flexbox--align-center--justify-start">
				<Overskrift label="Teams" />
				<HjelpeTekst>Med teams kan du og kolleger dele testdatagrupper.</HjelpeTekst>
			</div>
			<Toolbar
				toggleOnChange={byttVisning}
				toggleCurrent={visning}
				searchField={<SearchFieldConnector />}
			>
				<Knapp type="hoved" onClick={visOpprettTeam}>
					Nytt team
				</Knapp>
			</Toolbar>

			{opprettTeamState && <RedigerTeamConnector onCancel={skjulOpprettTeam} />}

			<TeamListe
				isFetching={isFetching}
				teams={teamListe}
				history={history}
				searchActive={searchActive}
			/>
		</div>
	)
}
