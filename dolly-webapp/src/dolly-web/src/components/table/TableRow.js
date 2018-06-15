import React, { Component } from 'react'
import cn from 'classnames'
import Checkbox from '~/components/fields/Checkbox/Checkbox'
import TableRowDetail from './TableRowDetail'

export default class TableRow extends Component {
	state = {
		detailOpen: false
	}

	onClickRow = (event, id) => {
		const { link, history } = this.props
		// click should redirect to next page
		if (link) return history.push(`gruppe/${id}`)

		return false
	}

	toggleOpenDetail = e => this.setState({ detailOpen: !this.state.detailOpen })

	render() {
		const { detailOpen } = this.state
		const { rowObject, selectable, expandable } = this.props

		const rowColumns = Object.keys(rowObject).map((key, idx) => {
			return <td key={idx}>{rowObject[key]}</td>
		})

		const toggleClass = cn('fa', {
			'fa-chevron-down': !this.state.detailOpen,
			'fa-chevron-up': this.state.detailOpen
		})

		return (
			<React.Fragment>
				<tr onClick={e => this.onClickRow(e, rowObject.id)}>
					{selectable && (
						<td>
							<Checkbox className="table-checkbox" label="" />
						</td>
					)}
					{rowColumns}
					{expandable && (
						<td>
							<button aria-label="Expand row" onClick={this.toggleOpenDetail}>
								<i className={toggleClass} />
							</button>
						</td>
					)}
				</tr>
				{detailOpen && <TableRowDetail />}
			</React.Fragment>
		)
	}
}
