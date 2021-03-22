import React from 'react'
import Pagination from './pagination/Pagination'
import Table from './table/Table'

export default function DollyTable({
	data,
	pagination,
	gruppeDetaljer,
	setSidetall,
	setSideStoerrelse,
	visSide,
	...props
}) {
	if (!pagination) return <Table data={data} {...props} />

	return (
		<Pagination
			items={data}
			visSide={visSide}
			gruppeDetaljer={gruppeDetaljer}
			setSidetall={setSidetall}
			setSideStoerrelse={setSideStoerrelse}
			render={items => <Table data={items} {...props} />}
		/>
	)
}
