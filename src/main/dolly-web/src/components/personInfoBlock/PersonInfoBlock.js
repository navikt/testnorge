import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import KodeverkValueConnector from '~/components/fields/KodeverkValue/KodeverkValueConnector'

import './personInfoBlock.less'
import TknrValueConnector from '../fields/TknrValue/TknrValueConnector'

export default class PersonInfoBlock extends PureComponent {
	static propTypes = {
		data: PropTypes.array
	}

	render() {
		const { header, data, multiple } = this.props
		if (multiple) {
			// Multiple: Eksempel: flere inntekter.
			return (
				<Fragment>
					{data.map((subBlock, idx) =>
						this.renderPersonInfoBlock(subBlock.value, subBlock.label, idx, idx !== data.length - 1)
					)}
				</Fragment>
			)
		}

		return this.renderPersonInfoBlock(data)
	}

	renderPersonInfoBlock = (data, header, idx, bottomBorder) => {
		const cssClassContent = cn('person-info-block_content', {
			'bottom-border': bottomBorder
		})

		return (
			<div key={idx} className="person-info-block">
				{header && <h4 className="person-info-block_multiple">{header}</h4>}

				<div className={cssClassContent}>
					{data.map((v, k) => {
						const staticValueProps = {
							key: k,
							size: v.width && v.width,
							header: v.label,
							headerType: 'h4',
							value: v.value
						}

						// Spesiell tilfeller for gtVerdi og tknr
						const apiKodeverkId = v.apiKodeverkId ? v.apiKodeverkId : null
						const tknr = v.tknr ? v.tknr : null
						// console.log(tknr)
						// finn tilhørende attributt
						const attributt = this.props.attributtManager.getAttributtById(v.id)

						if (attributt && attributt.apiKodeverkId && v.value) {
							return (
								<KodeverkValueConnector
									apiKodeverkId={attributt.apiKodeverkId}
									{...staticValueProps}
								/>
							)
						} else if (apiKodeverkId) {
							return (
								<KodeverkValueConnector
									apiKodeverkId={apiKodeverkId}
									{...staticValueProps}
									extraLabel={v.extraLabel && v.extraLabel}
								/>
							)
						} else if (tknr) {
							return <TknrValueConnector tknr={tknr} {...staticValueProps} />
						}
						return v.value && <StaticValue {...staticValueProps} />
					})}
				</div>
			</div>
		)
	}
}
