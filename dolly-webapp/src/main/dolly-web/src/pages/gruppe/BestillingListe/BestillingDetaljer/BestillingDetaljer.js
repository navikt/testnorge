import React, { PureComponent, Fragment } from 'react'
import Icon from '~/components/icon/Icon'
import Button from '~/components/button/Button'
import './BestillingDetaljer.less'
import Formatters from '~/utils/DataFormatter'
import StaticValue from '~/components/fields/StaticValue/StaticValue'

export default class BestillingDetaljer extends PureComponent {
	render() {
		const { id, successEnvs, failedEnvs, errorMsgs } = this.props.miljoeStatusObj

		// TODO: Reverse Map detail data here. Alex
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
			<div className="flexbox--align-center error-header">
				<Icon size={'16px'} kind={'report-problem-triangle'} />
				<h3>Feilmeldinger</h3>
			</div>
			<div className={'flexbox--align-center info-block'}>
				{errorMsgs.map((error, i) => {
					return (
						<p className="" key={i}>
							{error.split('%').join(' ')
							// .substring(0, error.length - 1)
							}
						</p>
					)
				})}
			</div>
		</Fragment>
	)

	_renderMiljoeStatus = (successEnvs, failedEnvs) => {
		const successEnvsStr = Formatters.arrayToString(successEnvs)
		const failedEnvsStr = Formatters.arrayToString(failedEnvs)

		return (
			<Fragment>
				<h3>Miljøstatus</h3>

				<div className={'flexbox--align-center info-block'}>
					{successEnvsStr.length > 0 && <StaticValue header="Suksess" value={successEnvsStr} />}
					{failedEnvsStr.length > 0 && <StaticValue header="Feilet" value={failedEnvsStr} />}
				</div>
			</Fragment>
		)
	}
}
