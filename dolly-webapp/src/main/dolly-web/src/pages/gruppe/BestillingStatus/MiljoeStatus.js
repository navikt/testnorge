import React, { PureComponent, Fragment } from 'react'
import './MiljoeStatus.less'
import Icon from '~/components/icon/Icon'

export default class MiljoeStatus extends PureComponent {
	_renderMiljoe = key => (
		<Fragment key={key}>
			<Icon kind="feedback-check-circle" />
			<span>T0</span>
		</Fragment>
	)

	render() {
		const miljoer = ['T0', 'Q1', 'U2', 'U3', 'U5']

		return (
			<div className="miljoe-status">
				<h3>Status i miljøene</h3>
				<hr />

				{miljoer.map((miljo, i) => {
					return this._renderMiljoe(i)
				})}
			</div>
		)
	}
}
