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

// Setter rowKey til en verdi dersom datasett har et unikt felt
// Fallback til row index
const getRowKey = (row, columns) => {
	const hasUnique = columns.find(c => c.unique)
	return hasUnique && _get(row, `${hasUnique.dataField}`).toString()
}

export default function Table({ data, columns, onRowClick, onExpand }) {
	return (
		<div className="dot">
			<div className="dot-header">
				{columns.map((cell, idx) => (
					<Column key={idx} width={cell.width} value={cell.text} />
				))}
				{onExpand && <Column />}
			</div>
			{data.map((row, rowIdx) => {
				console.log('row :', row)
				const navLink = onRowClick ? onRowClick(row) : null
				const expandComponent = onExpand ? onExpand(row) : null
				const ident = row[0]
				const rowKey = getRowKey(row, columns) || rowIdx
				return (
					<Row key={rowKey} navLink={navLink} expandComponent={expandComponent} ident={ident}>
						{columns.map((columnCell, idx) => (
							<Column key={idx} width={columnCell.width} value={getColumnValue(row, columnCell)} />
						))}
					</Row>
				)
			})}
		</div>
	)
}
