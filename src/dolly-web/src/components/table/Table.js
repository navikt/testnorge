import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import TableRow from './TableRow'
import Checkbox from '~/components/fields/Checkbox/Checkbox'
import { withRouter } from 'react-router-dom'
import './table.less'

class Table extends PureComponent {
	static propTypes = {
		data: PropTypes.array,
		link: PropTypes.bool,
		selectable: PropTypes.bool,
		history: PropTypes.object
	}

	render() {
		const { data, link, history, selectable, expandable } = this.props
		return (
			<React.Fragment>
				{selectable && (
					<div className="dolly-table-header">
						<Checkbox id="select-all" label="Velg alle" />

						<span>Valgt: 0</span>
					</div>
				)}

				<table className="dolly-table">
					<thead>
						<tr>
							{selectable && <th>[_]</th>}
							{Object.keys(data[0]).map((objKey, idx) => <th key={idx}>{objKey}</th>)}
							{expandable && <th />}
						</tr>
					</thead>

					<tbody>
						{data.map(obj => (
							<TableRow
								rowObject={obj}
								key={obj.id}
								link={link}
								history={history}
								selectable={selectable}
								expandable={expandable}
							/>
						))}
					</tbody>
				</table>
			</React.Fragment>
		)
	}
}

export default withRouter(Table)
