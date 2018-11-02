import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'
import StaticValue from '~/components/fields/StaticValue/StaticValue'

import './personInfoBlock.less'

export default class PersonInfoBlock extends PureComponent {
	static propTypes = {
		data: PropTypes.array
	}

	renderPersonInfoBlock = (data, idx, bottomBorder) => {
		const cssClassContent = cn('person-info-block_content', {
			'bottom-border': bottomBorder
		})

		return (
			<div key={idx} className="person-info-block">
				{/* {header && <h3>{header}</h3>} */}

				<div className={cssClassContent}>
					{data.map((v, k) => {
						const optionalClassName = cn('static-value', [
							{ 'static-value_xlarge': v.width === 'xlarge' },
							{ 'static-value_medium': v.width === 'medium' }
						])
						return (
							v.value && (
								<StaticValue
									optionalClassName={optionalClassName}
									key={k}
									header={v.label}
									headerType="h4"
									value={v.value || ''}
								/>
							)
						)
					})}
				</div>
			</div>
		)
	}

	render() {
		const { header, data, multiple } = this.props
		if (multiple) {
			return (
				<Fragment>
					{data.map((subBlock, idx) =>
						this.renderPersonInfoBlock(subBlock.value, idx, idx !== data.length - 1)
					)}
				</Fragment>
			)
		}

		return this.renderPersonInfoBlock(data)
	}
}
