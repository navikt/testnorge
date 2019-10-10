import React from 'react'
import DollyTable from '~/components/ui/dollyTable/DollyTable'

import ConfirmTooltip from '~/components/ui/confirmTooltip/ConfirmTooltip'

export default function TeamMedlemmer({ medlemmer, removeMember }) {
	if (!medlemmer) return false

	const columns = [
		{
			text: 'Navn',
			width: '30',
			dataField: 'navIdent',
			unique: true
		},
		{
			text: 'Rolle',
			width: '20',
			dataField: 'navIdent',
			formatter: (cell, row) => 'Utvikler' // statisk rolle satt til "utvikler"
		},
		{
			text: 'Slett',
			width: '50',
			dataField: 'team.navn',
			formatter: (cell, row) => (
				<ConfirmTooltip
					className="flexbox--align-center"
					onClick={() => removeMember(row.navIdent)}
					message={`Vil du slette ${row.navIdent} fra dette teamet?`}
				/>
			)
		}
	]

	return <DollyTable data={medlemmer} columns={columns} pagination />
}
