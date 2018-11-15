import React, { PureComponent, Fragment } from 'react'
import './MiljoeStatus.less'
import Icon from '~/components/icon/Icon'
import Button from '~/components/button/Button'

export default class MiljoeStatus extends PureComponent {
	_renderMiljoe = (env, key) => (
		<div className={'miljoe'} key={key}>
			<Icon size={'24px'} kind="feedback-check-circle" />
			<p>{env}</p>
		</div>
	)

	render() {
		const bestillingsData = this.props.bestillingsData
		const miljoer = ['T0', 'Q1', 'U2', 'U3', 'U5']
		console.log(this.props.bestillingsData, 'data')

		return (
			<div className="miljoe-status">
				<div className="status-header">
					<p>Bestilling #{bestillingsData.id}</p>
					<h3>Status i miljøene</h3>
					<div className="remove-button-container">
						<Button kind="remove-circle" />
					</div>
				</div>
				<hr />
				<div className={'miljoe-container'}>
					{bestillingsData.environments.map((env, i) => {
						return this._renderMiljoe(env, i)
					})}
				</div>
			</div>
		)
	}
}
