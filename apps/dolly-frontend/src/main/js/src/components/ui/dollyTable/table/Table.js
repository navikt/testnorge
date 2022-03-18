import React from 'react'
import cn from 'classnames'
import Row from './TableRow'
import Column from './TableColumn'
import _get from 'lodash/get'
import _isFunction from 'lodash/isFunction'
import _isNil from 'lodash/isNil'
import { LaastGruppeIconItem } from '~/components/ui/icon/IconItem'

import './Table.less'

const getColumnValue = (row, column) => {
	let value = _get(row, `${column.dataField}`, '')
	value = _isNil(value) ? '' : value.toString()
	return column.formatter ? column.formatter(value, row) : value
}

const getColumnHeader = (cell, data) => {
	if (cell.headerFormatter) {
		return cell.headerFormatter(cell.text, data)
	} else {
		return cell.text
	}
}
// Setter rowKey til en verdi dersom datasett har et unikt felt
// Fallback til row index
const getRowKey = (row, columns) => {
	const hasUnique = columns.find((c) => c.unique)
	return hasUnique && _get(row, `${hasUnique.dataField}`).toString()
}

const getIconType = (iconItem, row) => {
	if (!iconItem) return null
	if (row.erLaast) {
		iconItem = <LaastGruppeIconItem />
	}
	return _isFunction(iconItem) ? iconItem(row) : iconItem
}

export default function Table({
	data,
	iconItem,
	columns,
	onRowClick,
	header = true,
	visPerson,
	visBestilling,
	onExpand,
}) {
	const headerClass = cn('dot-header', {
		'has-icon': Boolean(iconItem),
	})

	return (
		<div className="dot">
			{header && (
				<div className={headerClass}>
					{columns.map((cell, idx) => (
						<Column key={idx} width={cell.width} value={getColumnHeader(cell, data)} />
					))}
					{onExpand && <Column />}
				</div>
			)}
			{data.map((row, rowIdx) => {
				if (!row) return null
				const navLink = onRowClick ? onRowClick(row) : null
				const expandComponent = onExpand ? onExpand(row) : null
				const iconType = getIconType(iconItem, row)
				const rowKey = getRowKey(row, columns) || rowIdx
				const expandPerson =
					expandComponent &&
					visPerson &&
					_get(expandComponent, 'props.personId') === visPerson.toString()
				const expandBestilling =
					expandComponent &&
					visBestilling &&
					_get(expandComponent, 'props.bestilling.id')?.toString() === visBestilling.toString()

				return (
					<Row
						key={rowKey}
						icon={iconType}
						navLink={navLink}
						expandComponent={expandComponent}
						expandPerson={expandPerson}
						expandBestilling={expandBestilling}
					>
						{columns.map((columnCell, idx) => (
							<Column
								key={idx}
								width={columnCell.width}
								value={getColumnValue(row, columnCell)}
								centerItem={columnCell.centerItem}
							/>
						))}
					</Row>
				)
			})}
		</div>
	)
}
