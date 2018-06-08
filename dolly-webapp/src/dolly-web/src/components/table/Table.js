import React from 'react'
import TableRow from './TableRow'
import './table.less'

const Table = ({ id, data }) => {
	const headers = (
		<thead>
			<tr>{Object.keys(data[0]).map((objKey, idx) => <th key={idx}>{objKey}</th>)}</tr>
		</thead>
	)

	return (
		<table className="dolly-table">
			{headers}

			<tbody>{data.map(obj => <TableRow rowObject={obj} key={obj.id} />)}</tbody>
		</table>
	)
}

export default Table
