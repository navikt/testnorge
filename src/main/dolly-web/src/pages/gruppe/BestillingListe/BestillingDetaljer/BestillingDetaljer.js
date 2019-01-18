import React, { PureComponent, Fragment } from 'react'
import Icon from '~/components/icon/Icon'
import Button from '~/components/button/Button'
import './BestillingDetaljer.less'

export default class BestillingDetaljer extends PureComponent {
	render() {
		const { id, successEnvs, failedEnvs, errorMsgs } = this.props.miljoeStatusObj

		return (
			<div className="bestilling-detaljer">
				<h3>Bestillingsdetaljer</h3>
				{this._renderMiljoeStatus(successEnvs, failedEnvs)}
				{errorMsgs.length > 0 && this._renderErrorMessage(errorMsgs)}
				<div className="flexbox--align-center--justify-end">
					<Button className="flexbox--align-center" kind="edit">
						GJENOPPRETT I TPS
					</Button>
				</div>
			</div>
		)
	}

	_renderErrorMessage = errorMsgs => (
		<Fragment>
			<h3>Feilmeldinger</h3>
			{errorMsgs.map((error, i) => {
				return (
					<div className={'flexbox--all-center'} key={i}>
						<Icon size={'24px'} kind={'report-problem-triangle'} />
						<p className="" key={i}>
							{error.split('%').join(' ')
							// .substring(0, error.length - 1)
							}
						</p>
					</div>
				)
			})}
		</Fragment>
	)

	_renderMiljoeStatus = (successEnvs, failedEnvs) => (
		<Fragment>
			<h3>Miljøstatus</h3>
			<div className={'flexbox--align-center--justify-start'}>
				{successEnvs.map((env, i) => {
					return this._renderMiljoe(env, i, 'success')
				})}

				{failedEnvs.map((env, i) => {
					return this._renderMiljoe(env, i, 'failed')
				})}
			</div>
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

	// _renderErrorMessage = errorMsgs =>
	// 	errorMsgs.map((error, i) => {
	// 		return (
	// 			<div className={'flexbox--all-center'} key={i}>
	// 				<Icon size={'24px'} kind={'report-problem-triangle'} />
	// 				<p className="error-text" key={i}>
	// 					{error.split('%').join(' ')
	// 					// .substring(0, error.length - 1)
	// 					}
	// 				</p>
	// 			</div>
	// 		)
	// 	})

	// _renderFailureMessage = () => (
	// 	<Fragment>
	// 		<Icon kind={'report-problem-circle'} />
	// 		<p>Din bestilling ble ikke utført</p>
	// 	</Fragment>
	// )

	// _renderMiljoe = (env, key, status) => {
	// 	const iconKind = status == 'success' ? 'feedback-check-circle' : 'report-problem-triangle'
	// 	return (
	// 		<div className={'miljoe'} key={key}>
	// 			<Icon size={'24px'} kind={iconKind} />
	// 			<p>{env}</p>
	// 		</div>
	// 	)
	// }
}
