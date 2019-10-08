import React from 'react'
import DollyTable from '~/components/ui/dollyTable/DollyTable'

export default function TeamMedlemmer({ grupper, history }) {
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
