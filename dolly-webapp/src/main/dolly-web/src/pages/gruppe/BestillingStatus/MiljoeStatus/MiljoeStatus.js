import React, { PureComponent, Fragment } from 'react'
import './MiljoeStatus.less'
import Icon from '~/components/icon/Icon'
import Button from '~/components/button/Button'

export default class MiljoeStatus extends PureComponent {
	render() {
		const { id, successEnvs, failedEnvs, errorMsgs } = this.props.miljoeStatusObj
		const failed = true && successEnvs.length == 0 && errorMsgs.length == 0

		return (
			<div className="miljoe-status">
				<div className="status-header">
					<p>Bestilling #{id}</p>
					<h3>Bestillingsstatus</h3>
					<div className="remove-button-container">
						<Button kind="remove-circle" onClick={this.props.onCloseButton} />
					</div>
				</div>
				<hr />
				<div className={'miljoe-container'}>
					{failed
						? this._renderFailureMessage()
						: this._renderMiljoeStatus(successEnvs, failedEnvs)}
				</div>
				{this._renderErrorMessage(errorMsgs)}
			</div>
		)
	}

	_renderMiljoeStatus = (successEnvs, failedEnvs) => (
		<Fragment>
			{successEnvs.map((env, i) => {
				return this._renderMiljoe(env, i, 'success')
			})}

			{failedEnvs.map((env, i) => {
				return this._renderMiljoe(env, i, 'failed')
			})}
		</Fragment>
	)

	_renderErrorMessage = errorMsgs =>
		errorMsgs.map((error, i) => {
			return (
				<div className={'flexbox--all-center'} key={i}>
					<Icon size={'24px'} kind={'report-problem-triangle'} />
					<p className="error-text" key={i}>
						{error.split('%').join(' ')
						// .substring(0, error.length - 1)
						}
					</p>
				</div>
			)
		})

	_renderFailureMessage = () => (
		<Fragment>
			<Icon kind={'report-problem-circle'} />
			<p>Din bestilling ble ikke utført</p>
		</Fragment>
	)

	_renderMiljoe = (env, key, status) => {
		const iconKind = status == 'success' ? 'feedback-check-circle' : 'report-problem-triangle'
		return (
			<div className={'miljoe'} key={key}>
				<Icon size={'24px'} kind={iconKind} />
				<p>{env}</p>
			</div>
		)
	}
}
