import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import './personInfoBlock.less'

export default class PersonInfoBlock extends PureComponent {
	static propTypes = {
		header: PropTypes.string,
		data: PropTypes.array
	}

	render() {
		const { header, data } = this.props

		return (
			<div className="person-info-block">
				{header && <h4>{header}</h4>}

				<div className="person-info-block-row">
					{data.map((v, k) => {
						// TODO: use tables for this?
						return (
							<div className="person-info-block-cell" key={k}>
								<h5>{v.label}</h5>
								<span>{v.value}</span>
							</div>
						)
					})}
				</div>
			</div>
		)
	}
}
