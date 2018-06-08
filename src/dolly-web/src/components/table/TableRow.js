import React, { Component } from 'react'
import TableRowDetail from './TableRowDetail'

export default class TableRow extends Component {
	state = {
		detailOpen: false
	}

	toggleOpenDetail = e => this.setState({ detailOpen: !this.state.detailOpen })

	render() {
		const { detailOpen } = this.state
		const { id, rowObject } = this.props

		const rowColumns = Object.keys(rowObject).map((key, idx) => {
			return <td key={idx}>{rowObject[key]}</td>
		})

		return (
			<React.Fragment>
				<tr onClick={this.toggleOpenDetail}>{rowColumns}</tr>
				{detailOpen && <TableRowDetail />}
			</React.Fragment>
		)
	}
}
