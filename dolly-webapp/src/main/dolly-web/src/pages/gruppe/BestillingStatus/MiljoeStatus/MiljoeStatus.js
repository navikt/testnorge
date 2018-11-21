import React, { PureComponent, Fragment } from 'react'
import './MiljoeStatus.less'
import Icon from '~/components/icon/Icon'
import Button from '~/components/button/Button'

export default class MiljoeStatus extends PureComponent {
	_renderMiljoe = (env, key, status) => {
		const iconKind = status == 'success' ? 'feedback-check-circle' : 'report-problem-triangle'
		return (
			<div className={'miljoe'} key={key}>
				<Icon size={'24px'} kind={iconKind} />
				<p>{env}</p>
			</div>
		)
	}

	render() {
		const { envs, failedEnvs } = this.props.miljoeStatusObj
		console.log(envs, failedEnvs, 'bro')
		// console.log(this.props.bestillingsData, 'data')
		// let envs = bestillingsData.environments.slice(0) // Clone array for å unngå mutering
		// let failedEnvs = []

		// console.log(envs, 'all envs original')

		// // bestillingsData.personStatus.forEach(person => {
		// // 	envs.forEach(env => {
		// // 		if (!person.tpsfSuccessEnv) {
		// // 			// TODO: Bestilling failed 100% fra Tpsf. Implement retry senere når maler er støttet
		// // 			failedEnvs = envs
		// // 			envs = []
		// // 		} else if (!person.tpsfSuccessEnv.includes(env)) {
		// // 			failedEnvs.push(env) && envs.splice(envs.indexOf(env), 1)
		// // 		}
		// // 	})
		// // })

		// console.log(envs, 'all envs after splice')
		// console.log(failedEnvs, 'failed envs')

		return (
			<div className="miljoe-status">
				<div className="status-header">
					{/* <p>Bestilling #{bestillingsData.id}</p> */}
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
