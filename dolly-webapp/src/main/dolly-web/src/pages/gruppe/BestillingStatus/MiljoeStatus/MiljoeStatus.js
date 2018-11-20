import React, { PureComponent, Fragment } from 'react'
import './MiljoeStatus.less'
import Icon from '~/components/icon/Icon'
import Button from '~/components/button/Button'

export default class MiljoeStatus extends PureComponent {
	_renderMiljoe = (env, key, status) => {
		const iconKind = status == 'success' ? 'feedback-check-circle' : 'report-problem-circle'
		return (
			<div className={'miljoe'} key={key}>
				<Icon size={'24px'} kind={iconKind} />
				<p>{env}</p>
			</div>
		)
	}

	render() {
		const bestillingsData = this.props.bestillingsData
		console.log(this.props.bestillingsData, 'data')
		const envs = bestillingsData.environments
		const failedEnvs = ['t0']

		// bestillingsData.personStatus.foreach(person => {
		// 	envs.forEach(env => {
		// 		if (!person.tpsfSuccessEnv.includes(env)) {
		// 			failedEnvs.push(env) && envs.splice(envs.indexOf(env), 1)
		// 		}
		// 	})
		// })

		console.log(envs, 'all envs')
		console.log(failedEnvs, 'failed envs')

		return (
			<div className="miljoe-status">
				<div className="status-header">
					<p>Bestilling #{bestillingsData.id}</p>
					<h3>Status i miljøene</h3>
					<div className="remove-button-container">
						<Button kind="remove-circle" onClick={this.props.onCloseButton} />
					</div>
				</div>
				<hr />
				<div className={'miljoe-container'}>
					{envs.map((env, i) => {
						return this._renderMiljoe(env, i, 'success')
					})}
					{failedEnvs.map((env, i) => {
						return this._renderMiljoe(env, i, 'failed')
					})}
				</div>
			</div>
		)
	}
}
