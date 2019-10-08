import React from 'react'
import cn from 'classnames'
import Row from './TableRow'
import Column from './TableColumn'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'

import './Table.less'

const getColumnValue = (row, column) => {
	let value = _get(row, `${column.dataField}`, '')
	value = _isNil(value) ? '' : value.toString()
	return column.formatter ? column.formatter(value, row) : value
}

export default function Table({ data, columns, onRowClick, onExpand }) {
	return (
		<div className="dot">
			<div className="dot-header">
				{columns.map((cell, idx) => <Column key={idx} width={cell.width} value={cell.text} />)}
				{onExpand && <Column />}
			</div>
			{data.map((row, rowIdx) => {
				const navLink = onRowClick ? onRowClick(row) : null
				const expandComponent = onExpand ? onExpand(row) : null
				return (
					<Row key={rowIdx} navLink={navLink} expandComponent={expandComponent}>
						{columns.map((columnCell, idx) => (
							<Column key={idx} width={columnCell.width} value={getColumnValue(row, columnCell)} />
						))}
					</Row>
				)
			})}
		</div>
	)
}
