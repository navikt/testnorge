import React from 'react'
import TableRow from './TableRow'
import './table.less'

const Table = ({ id, tableObjects }) => {
	const oby = [
		{
			Navn: 'dust',
			Team: 'FREG',
			Eier: 'Holene, Axel',
			Hensikt: 'Hensikten med dette er å hh',
			Personer: '30',
			Miljø: 't5, t6, t7',
			id: 0
		},
		{
			Navn: 'bra',
			Team: 'FO',
			Eier: 'Fløgstad, Peter',
			Hensikt: 'Hensikten loooooool',
			Personer: '60',
			Miljø: 't5, t6, t7',
			id: 1
		}
	]

	tableObjects = oby

	const headers = (
		<thead>
			<tr>{Object.keys(tableObjects[0]).map((objKey, idx) => <th key={idx}>{objKey}</th>)}</tr>
		</thead>
	)

	return (
		<table className="dolly-table">
			{headers}

			{addRow}

			<tbody>{tableObjects.map(obj => <TableRow rowObject={obj} key={obj.id} />)}</tbody>
		</table>
	)
}

export default Table
