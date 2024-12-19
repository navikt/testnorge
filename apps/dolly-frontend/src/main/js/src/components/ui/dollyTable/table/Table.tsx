import cn from 'classnames'
import Row from './TableRow'
import Column from './TableColumn'
import * as _ from 'lodash-es'
import { LaastGruppeIconItem } from '@/components/ui/icon/IconItem'

import './Table.less'

const getColumnValue = (row, column) => {
	let value = _.get(row, `${column.dataField}`, '')
	value = _.isNil(value) ? '' : value.toString()
	return column.formatter ? column.formatter(value, row) : value
}

const getColumnHeader = (cell, data) => {
	if (cell.headerFormatter) {
		return cell.headerFormatter(cell.text, data)
	} else {
		return cell.text
	}
}

const getColumnClass = (cell) => cell.headerCssClass

// Setter rowKey til en verdi dersom datasett har et unikt felt
// Fallback til row index
const getRowKey = (row, columns) => {
	const hasUnique = columns.find((c) => c.unique)
	return hasUnique && _.get(row, `${hasUnique.dataField}`)?.toString()
}

const getIconType = (iconItem, row) => {
	if (!iconItem) {
		return null
	}
	if (row.erLaast) {
		iconItem = <LaastGruppeIconItem />
	}
	return _.isFunction(iconItem) ? iconItem(row) : iconItem
}

export default function Table({
	data,
	iconItem,
	columns,
	onRowClick,
	header = true,
	visPerson,
	hovedperson,
	visBestilling,
	onExpand,
	onHeaderClick,
}) {
	const headerClass = cn('dot-header', {
		'has-icon': Boolean(iconItem),
	})

	const tableColumnClick = (value) => {
		onHeaderClick && onHeaderClick(value)
	}

	return (
		<div className="dot">
			{header && (
				<div className={headerClass}>
					{columns?.map((cell, idx) => (
						<Column
							key={idx}
							width={cell.width}
							value={getColumnHeader(cell, data)}
							style={cell.style}
							className={getColumnClass(cell)}
							onClick={tableColumnClick}
						/>
					))}
					{onExpand && <Column />}
				</div>
			)}
			{data?.map((row, rowIdx) => {
				if (!row) {
					return null
				}
				const navLink = onRowClick ? onRowClick(row) : null
				const expandComponent = onExpand ? onExpand(row) : null
				const iconType = getIconType(iconItem, row)
				const rowKey = getRowKey(row, columns) || rowIdx
				const expandPerson =
					expandComponent &&
					visPerson &&
					(_.get(expandComponent, 'props.children.props.personId') === visPerson.toString() ||
						_.get(expandComponent, 'props.children.props.personId') === hovedperson.toString())
				const expandBestilling =
					expandComponent &&
					visBestilling &&
					_.get(expandComponent, 'props.bestilling.id')?.toString() === visBestilling.toString()

				return (
					<Row
						key={rowKey}
						icon={iconType}
						navLink={navLink}
						expandComponent={expandComponent}
						expandPerson={expandPerson}
						expandBestilling={expandBestilling}
					>
						{columns?.map((columnCell, idx) => (
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
