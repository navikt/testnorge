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
		const { data, multiple } = this.props
		if (multiple) {
			// *Multiple: Eksempel: flere inntekter.
			return (
				<Fragment>
					{data.map((subBlock, idx) => {
						let subItems = []

						subBlock.value = subBlock.value.filter(v => {
							v.subItem && subItems.push(v)
							return !v.subItem
						})
						return (
							<Fragment key={idx}>
								{this.renderPersonInfoBlock(
									subBlock.value,
									subBlock.label,
									idx,
									subItems.length > 0 ? false : idx !== data.length - 1
								)}
								{subItems &&
									subItems.length > 0 &&
									subItems.map((subItem, jdx) => {
										return this.renderSubItems(
											subBlock.parent,
											subItem.id,
											subItem.label,
											subItem.value,
											jdx,
											jdx === subItems.length - 1 && idx !== data.length - 1
										)
									})}
							</Fragment>
						)
					})}
				</Fragment>
			)
		}

		return this.renderPersonInfoBlock(data)
	}

	renderSubItems = (parent, subItemId, header, data, idx, bottomBorder) => {
		// Tar inn alle permisjoner eller utenlandsopphold
		const cssClass = cn('person-info-subItems', {
			'bottom-border': bottomBorder
		})
		const attributt = this.props.attributtManager.getAttributtById(parent)
		return (
			<div key={idx} className={cssClass}>
				<h4>{header}</h4>
				<Fragment>
					{data.map((subItem, kdx) => {
						return (
							<div key={kdx} className="subitems">
								{subItem.map((v, k) => {
									// map gjennom attributt
									let apiKodeverkId
									attributt &&
										attributt.items.map(item => {
											item.path.includes(subItemId) &&
												item.subItems.map(subsubItem => {
													subsubItem.id === v.id &&
														subsubItem.apiKodeverkId &&
														(apiKodeverkId = subsubItem.apiKodeverkId)
												})
										})
									const staticValueProps = {
										key: k,
										size: v.width && v.width,
										header: v.label,
										headerType: 'h4',
										value: v.value
									}
									{
										if (apiKodeverkId && v.value) {
											return (
												<KodeverkValueConnector
													apiKodeverkId={apiKodeverkId}
													{...staticValueProps}
												/>
											)
										} else {
											return v.value && <StaticValue {...staticValueProps} />
										}
									}
								})}
							</div>
						)
					})}
				</Fragment>
			</div>
		)
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
							size: v && v.width && v.width,
							header: v && v.label,
							headerType: 'h4',
							value: v && v.value
						}
						// Spesiell tilfeller for gtVerdi og tknr
						const apiKodeverkId = v && v.apiKodeverkId ? v.apiKodeverkId : null
						const tknr = v && v.tknr ? v.tknr : null

						// finn tilhørende attributt
						const attributt = this.props.attributtManager.getAttributtById(v && v.id)

						if (attributt && attributt.apiKodeverkId && v.value) {
							return (
								<KodeverkValueConnector
									apiKodeverkId={attributt.apiKodeverkId}
									{...staticValueProps}
								/>
							)
						} else if (apiKodeverkId && v.value) {
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
						return v && v.value && <StaticValue {...staticValueProps} />
					})}
				</div>
			</div>
		)
	}
}
