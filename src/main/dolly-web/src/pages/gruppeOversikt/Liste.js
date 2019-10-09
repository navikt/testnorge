import React, { Fragment } from 'react'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import Loading from '~/components/ui/loading/Loading'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import FavoriteButtonConnector from '~/components/ui/button/FavoriteButton/FavoriteButtonConnector'

export default function Liste({ items, history, searchActive, isFetching }) {
	if (isFetching) return <Loading label="laster grupper" panel />

	if (!items || !items.length) {
		return (
			<ContentContainer>
				{searchActive ? (
					<p>Søket gav ingen resultater.</p>
				) : (
					<Fragment>
						<p>Du har ingen testdatagrupper.</p>
						<p>
							For å se alle testdatagrupper, trykk på "Alle". Her kan du søke etter en spesifikk
							testdatagruppe eller se om det er noen som er relevante for deg. Hvis du trykker på
							stjerneikonet, legger du testdatagruppen til som en favoritt. Den vil da dukke opp
							under "Mine" testdatagrupper.
						</p>
						<p>For å opprette en ny testdatagruppe, trykk på "Ny gruppe" knappen over.</p>
					</Fragment>
				)}
			</ContentContainer>
		)
	}

	const columns = [
		{
			text: 'ID',
			width: '15',
			dataField: 'id'
		},
		{
			text: 'Navn',
			width: '20',
			dataField: 'navn'
		},
		{
			text: 'Team',
			width: '15',
			dataField: 'team.navn'
		},
		{
			text: 'Hensikt',
			width: '20',
			dataField: 'hensikt'
		},
		{
			text: 'Personer',
			width: '20',
			dataField: 'antallIdenter'
		},
		{
			text: 'Favoritt',
			width: '10',
			dataField: 'id',
			formatter: (cell, row) =>
				!row.erMedlemAvTeamSomEierGruppe && (
					<FavoriteButtonConnector hideLabel={true} groupId={row.id} />
				)
		}
	]
	return (
		<DollyTable
			data={items}
			columns={columns}
			onRowClick={row => () => history.push(`gruppe/${row.id}`)}
			pagination
		/>
	)
}
