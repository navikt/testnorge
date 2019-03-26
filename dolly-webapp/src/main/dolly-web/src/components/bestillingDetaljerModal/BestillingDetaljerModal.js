import React, { PureComponent, Fragment } from 'react'
import cn from 'classnames'
import Icon from '~/components/icon/Icon'
import '~/components/modal/DollyModal.less'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import { mapBestillingData } from '~/pages/gruppe/BestillingListe/BestillingDetaljer/BestillingDataMapper'
import Feilmelding from '~/components/Feilmelding/Feilmelding'

export default class BestillingDetaljerModal extends PureComponent {
	// TODO: Vi bør scrape bort denne klassen, og gjenbruke BestillingDetaljer komponent på modalen.
	_renderFeilmelding = bestilling => {
		return (
			<Fragment>
				<span className="flexbox--align-center--justify-start dollymodal feilcontainer">
					<Icon size={'16px'} kind={'report-problem-triangle'} />
					<h4 className="dollymodal feilmelding">Feilmeldinger</h4>
				</span>
				<Feilmelding bestilling={bestilling} />
			</Fragment>
		)
	}

	_finnesFeilmelding = bestilling => {
		if (bestilling.feil) {
			return true
		}

		let temp = false
		{
			bestilling.sigrunStubStatus &&
				bestilling.sigrunStubStatus.map(status => {
					if (status.statusMelding !== 'OK') temp = true
				})
		}

		{
			bestilling.krrStubStatus &&
				bestilling.krrStubStatus.map(status => {
					if (status.statusMelding !== 'OK') temp = true
				})
		}

		{
			bestilling.tpsfStatus &&
				bestilling.tpsfStatus.map(status => {
					if (status.statusMelding !== 'OK') temp = true
				})
		}

		{
			bestilling.aaregStatus &&
				bestilling.aaregStatus.map(status => {
					if (status.statusMelding !== 'OK') temp = true
				})
		}

		return temp
	}

	render() {
		const { bestilling } = this.props
		const data = mapBestillingData(bestilling)

		return (
			<Fragment>
				<div className="dollymodal" style={{ paddingLeft: 20, paddingRight: 20 }}>
					<h1>Bestilling #{bestilling.id}</h1>
					<div className={'bestilling-modal'}>
						{data ? (
							data.map((kategori, j) => {
								let bottomBorder
								{
									this._finnesFeilmelding(bestilling)
										? (bottomBorder = true)
										: (bottomBorder = j != data.length - 1)
								}
								const cssClass = cn('flexbox--align-center bestilling-details', {
									'bottom-border': bottomBorder
								})
								if (kategori.header) {
									return (
										<Fragment key={j}>
											<h4>{kategori.header} </h4>
											<div className={cssClass}>
												{kategori.items.map((attributt, i) => {
													if (attributt.value) {
														return (
															<StaticValue
																header={attributt.label}
																size="small"
																value={attributt.value}
																key={i}
															/>
														)
													}
												})}
											</div>
										</Fragment>
									)
								}
							})
						) : (
							<p>Kunne ikke hente bestillingsdata</p>
						)}
						{this._finnesFeilmelding(bestilling) && this._renderFeilmelding(bestilling)}
					</div>
				</div>
			</Fragment>
		)
	}
}
