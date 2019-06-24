import React, { PureComponent, Fragment } from 'react'
import cn from 'classnames'
import Icon from '~/components/icon/Icon'
import '~/components/modal/DollyModal.less'
import '~/pages/gruppe/BestillingListe/BestillingDetaljer/BestillingDetaljer.less'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import { mapBestillingData } from './BestillingDataMapper'
import Feilmelding from '~/components/Feilmelding/Feilmelding'
import Formatters from '~/utils/DataFormatter'
import miljoeStatusSelector from '~/utils/MiljoeStatusSelector'
import KodeverkValueConnector from '~/components/fields/KodeverkValue/KodeverkValueConnector'

export default class BestillingDetaljerSammendrag extends PureComponent {
	render() {
		const { bestilling, type } = this.props
		const bestillingDetaljer = miljoeStatusSelector(bestilling)
		const data = mapBestillingData(bestilling)
		const modal = type === 'modal'
		console.log('data :', data)
		return (
			<div className="bestilling-detaljer">
				<div
					className="dollymodal"
					style={
						modal ? { paddingLeft: 20, paddingRight: 20 } : { paddingLeft: 0, paddingRight: 0 }
					}
				>
					{modal && <h1>Bestilling #{bestilling.id}</h1>}
					<h3>Bestillingskriterier</h3>
					<div className="bestilling-detaljer">
						{this._renderBestillingsDetaljer(data)}
						{this._renderMiljoeStatus(
							bestillingDetaljer.successEnvs,
							bestillingDetaljer.failedEnvs,
							bestillingDetaljer.avvikEnvs
						)}
						{bestillingDetaljer.finnesFeilmelding && this._renderErrorMessage(bestilling)}
					</div>
				</div>
			</div>
		)
	}

	_renderBestillingsDetaljer = data => {
		return data ? (
			data.map((kategori, j) => {
				const bottomBorder = j != data.length - 1
				const cssClass = cn('flexbox--align-center info-text', {
					'bottom-border': bottomBorder
				})
				if (kategori.header) {
					return (
						<Fragment key={j}>
							<h4>{kategori.header} </h4>
							{kategori.items && (
								<div className={cssClass}>
									{kategori.items.map((attributt, i) => {
										if (attributt.value) {
											return this._renderStaticValue(attributt, i)
										}
									})}
								</div>
							)}
							{kategori.itemRows && (
								<div className={cn('info-text', { 'bottom-border': bottomBorder })}>
									{kategori.itemRows.map((row, i) => {
										return (
											<div className={'flexbox--align-start flexbox--wrap'} key={i}>
												{row.map((attributt, j) => {
													if (attributt.value) {
														return this._renderStaticValue(attributt, j)
													}
												})}
											</div>
										)
									})}
								</div>
							)}
						</Fragment>
					)
				}
			})
		) : (
			<p>Kunne ikke hente bestillingsdata</p>
		)
	}

	_renderStaticValue = (attributt, key) => {
		if (attributt.apiKodeverkId) {
			return (
				<KodeverkValueConnector
					apiKodeverkId={attributt.apiKodeverkId}
					showValue={attributt.showKodeverkValue}
					header={attributt.label}
					size={attributt.width ? attributt.width : 'small'}
					value={attributt.value}
					key={key}
				/>
			)
		}
		return (
			<StaticValue
				header={attributt.label}
				size={attributt.width ? attributt.width : 'small'}
				value={attributt.value}
				key={key}
			/>
		)
	}

	_renderMiljoeStatus = (successEnvs, failedEnvs, avvikEnvs) => {
		const successEnvsStr = Formatters.arrayToString(successEnvs)
		const failedEnvsStr = Formatters.arrayToString(failedEnvs)
		const avvikEnvsStr = Formatters.arrayToString(avvikEnvs)

		return (
			<Fragment>
				<h3>Miljøstatus</h3>
				<div className={'flexbox--align-center info-block'}>
					{successEnvsStr.length > 0 ? (
						<StaticValue size={'medium'} header="Suksess" value={successEnvsStr} />
					) : (
						<StaticValue size={'medium'} header="Suksess" value={'Ingen'} />
					)}
					{failedEnvsStr.length > 0 && (
						<StaticValue size={'medium'} header="Feilet" value={failedEnvsStr} />
					)}
					{avvikEnvsStr.length > 0 && (
						<StaticValue size={'medium'} header="Avvik" value={avvikEnvsStr} />
					)}
				</div>
			</Fragment>
		)
	}

	_renderErrorMessage = bestilling => {
		return (
			<Fragment>
				<div className="flexbox--align-center error-header">
					<Icon size={'16px'} kind={'report-problem-triangle'} />
					<h3>Feilmeldinger</h3>
				</div>
				<Feilmelding bestilling={bestilling} />
			</Fragment>
		)
	}
}
