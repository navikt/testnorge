import React from 'react'
import Table from './table/Table'
import { Pagination } from '~/components/ui/dollyTable/pagination/Pagination'
import SimplePagination from '~/components/ui/dollyTable/pagination/SimplePagination'

export const DollyTable = ({ data, pagination, gruppeDetaljer = {}, visSide, ...props }) => {
	switch (pagination) {
		case 'none':
			return <Table data={data} {...props} />
		case 'simple':
			return (
				<SimplePagination
					visSide={visSide}
					items={data}
					gruppeDetaljer={gruppeDetaljer}
					render={(items) => <Table data={items} {...props} />}
				/>
			)
		default:
			return (
				<Pagination
					visSide={visSide}
					items={data}
					gruppeDetaljer={gruppeDetaljer}
					render={(items) => <Table data={items} {...props} />}
				/>
			)
	}
}
