import React from 'react'
import Table from './table/Table'
import { Pagination } from '~/components/ui/dollyTable/pagination/Pagination'

export const DollyTable = ({ data, pagination, gruppeDetaljer = {}, visSide = 0, ...props }) => {
	if (!pagination) return <Table data={data} {...props} />

	return (
		<Pagination
			visSide={visSide}
			items={data}
			gruppeDetaljer={gruppeDetaljer}
			render={(items) => <Table data={items} {...props} />}
		/>
	)
}
