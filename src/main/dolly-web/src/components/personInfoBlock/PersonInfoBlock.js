import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import StaticValue from '~/components/fields/StaticValue/StaticValue'

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

				<div className="person-info-block_content">
					{data.map((v, k) => <StaticValue key={k} header={v.label} value={v.value} />)}
				</div>
			</div>
		)
	}
}
