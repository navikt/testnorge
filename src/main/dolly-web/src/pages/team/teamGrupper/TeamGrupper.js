import React from 'react'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import Loading from '~/components/ui/loading/Loading'

export default function TeamMedlemmer({ isFetching, grupper, history }) {
	if (isFetching) return <Loading label="laster grupper" panel />
	if (!grupper) return false

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
			width: '40',
			dataField: 'hensikt'
		}
	]

	return (
		<DollyTable
			data={grupper}
			columns={columns}
			onRowClick={row => () => history.push(`/gruppe/${row.id}`)}
			pagination
		/>
	)
}
