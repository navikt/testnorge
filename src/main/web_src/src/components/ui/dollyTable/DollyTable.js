import React from 'react'
import Pagination from './pagination/Pagination'
import Table from './table/Table'

export default function DollyTable({ data, pagination, visSide, ...props }) {
	if (!pagination) return <Table data={data} {...props} />

	return (
		<Pagination
			items={data}
			visSide={visSide}
			render={items => <Table data={items} {...props} />}
		/>
	)
}
