import React from 'react'
import Table from './table/Table'
import { Pagination } from '~/components/ui/dollyTable/pagination/Pagination'

export const DollyTable = ({ data, pagination, gruppeDetaljer = {}, visSide, ...props }) => {
	if (!pagination) return <Table data={data} {...props} />

	return (
		<Pagination
			items={data}
			visSide={visSide}
			gruppeDetaljer={gruppeDetaljer}
			render={(items) => <Table data={items} {...props} />}
		/>
	)
}
