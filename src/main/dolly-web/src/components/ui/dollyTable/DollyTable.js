import React from 'react'
import Pagination from './pagination/Pagination'
import Table from './table/Table'

export default function DollyTable({ data, columns, onRowClick, onExpand, pagination }) {
	if (!pagination)
		return <Table data={data} columns={columns} onRowClick={onRowClick} onExpand={onExpand} />

	return (
		<Pagination
			items={data}
			render={items => (
				<Table data={items} columns={columns} onRowClick={onRowClick} onExpand={onExpand} />
			)}
		/>
	)
}
