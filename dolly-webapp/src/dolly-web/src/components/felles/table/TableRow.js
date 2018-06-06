import React from 'react'

const TableRow = ({ id, rowObject }) => {
	const s = Object.keys(rowObject).map((key, idx) => {
		return <td key={idx}>{rowObject[key]}</td>
	})

	return <tr>{s}</tr>
}

export default TableRow
