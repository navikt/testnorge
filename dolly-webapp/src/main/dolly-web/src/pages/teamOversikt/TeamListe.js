import React from 'react'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import Loading from '~/components/ui/loading/Loading'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'

export default function TeamListe({ teams, isFetching, history, searchActive }) {
	if (isFetching) return <Loading label="laster teams" panel />

	if (!teams || !teams.length) {
		return (
			<ContentContainer>
				{searchActive
					? 'Søket gav ingen resultater.'
					: 'Du har ingen teams. Trykke på opprett knappen for å opprette ett nytt team.'}
			</ContentContainer>
		)
	}

	const columns = [
		{
			text: 'Navn',
			width: '20',
			dataField: 'navn'
		},
		{
			text: 'Beskrivelse',
			width: '30',
			dataField: 'beskrivelse'
		},
		{
			text: 'Eier',
			width: '20',
			dataField: 'eierNavIdent'
		},
		{
			text: 'Personer',
			width: '20',
			dataField: 'antallMedlemmer'
		}
	]

	return (
		<DollyTable
			data={teams}
			columns={columns}
			onRowClick={row => () => history.push(`team/${row.id}`)}
			pagination
		/>
	)
}
