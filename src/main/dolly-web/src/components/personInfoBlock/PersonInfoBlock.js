import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import StaticValue from '~/components/fields/StaticValue/StaticValue'

import './personInfoBlock.less'

export default class PersonInfoBlock extends PureComponent {
	static propTypes = {
		header: PropTypes.string,
		data: PropTypes.array
	}

	renderPersonInfoBlock = (data, header, idx) => {
		return (
			<div key={idx} className="person-info-block">
				{header && <h4>{header}</h4>}

				<div className="person-info-block_content">
					{data.map((v, k) => {
						return v.value && <StaticValue key={k} header={v.label} value={v.value || ''} />
					})}
				</div>
			</div>
		)
	}

	render() {
		const { header, data, multiple } = this.props
		if (multiple) {
			return data.map((subBlock, idx) =>
				this.renderPersonInfoBlock(subBlock.value, subBlock.label, idx)
			)
		}

		return this.renderPersonInfoBlock(data)
	}
}
