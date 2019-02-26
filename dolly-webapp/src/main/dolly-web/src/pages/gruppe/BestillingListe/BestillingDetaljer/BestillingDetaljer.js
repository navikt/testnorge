import React, { PureComponent, Fragment } from 'react'
import Icon from '~/components/icon/Icon'
import Button from '~/components/button/Button'
import './BestillingDetaljer.less'
import Formatters from '~/utils/DataFormatter'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Knapp from 'nav-frontend-knapper'
import { Formik, FieldArray } from 'formik'
import MiljoVelgerConnector from '~/components/miljoVelger/MiljoVelgerConnector'
import * as yup from 'yup'
import { mapBestillingData } from './BestillingDataMapper'
import cn from 'classnames'
import DollyModal from '~/components/modal/DollyModal'

export default class BestillingDetaljer extends PureComponent {
	constructor(props) {
		super(props)

		this.EnvValidation = yup.object().shape({
			environments: yup.array().required('Velg minst ett miljø')
		})
		this.state = {
			modalOpen: false
		}
	}

	openModal = () => {
		this.setState({ modalOpen: true })
	}
	closeModal = () => {
		this.setState({ modalOpen: false })
	}

	render() {
		const { successEnvs, failedEnvs, errorMsgs } = this.props.miljoeStatusObj
		const { modalOpen } = this.state

		// TODO: Reverse Map detail data here. Alex
		return (
			<div className="bestilling-detaljer">
				{this._renderBestillingsDetaljer()}
				{this._renderMiljoeStatus(successEnvs, failedEnvs)}
				{errorMsgs.length > 0 && this._renderErrorMessage(errorMsgs)}
				<div className="flexbox--align-center--justify-end">
					<Button onClick={this.openModal} className="flexbox--align-center" kind="synchronize">
						GJENOPPRETT I TPS
					</Button>
					<DollyModal
						isOpen={modalOpen}
						onRequestClose={this.closeModal}
						closeModal={this.closeModal}
						content={this._renderGjenopprettModal()}
					/>
				</div>
			</div>
		)
	}

	_renderBestillingsDetaljer = () => {
		const { bestilling } = this.props
		const data = mapBestillingData(bestilling)
		return (
			<Fragment>
				<h3>Bestillingsdetaljer</h3>
				<div className={'info-block'}>
					{/* Husk å lage en sjekk om verdi finnes */}
					{data ? (
						data.map((kategori, j) => {
							const bottomBorder = j != data.length - 1
							const cssClass = cn('flexbox--align-center info-text', {
								'bottom-border': bottomBorder
							})
							if (kategori.header) {
								return (
									<Fragment key={j}>
										<h4>{kategori.header} </h4>
										<div className={cssClass} key={j}>
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
				</div>
			</Fragment>
		)
	}

	_renderGjenopprettModal = () => {
		const { environments, id } = this.props.bestilling // miljø som ble bestilt i en bestilling

		return (
			<Fragment>
				<div className="dollymodal">
					<div style={{ paddingLeft: 20, paddingRight: 20 }}>
						<h1>Bestilling #{id}</h1>
						<StaticValue header="Bestilt miljø" value={Formatters.arrayToString(environments)} />
						<br />
						<hr />
					</div>
					<Formik
						initialValues={{
							environments
						}}
						onSubmit={this._submitFormik}
						validationSchema={this.EnvValidation}
						render={formikProps => {
							return (
								<Fragment>
									<FieldArray
										name="environments"
										render={arrayHelpers => (
											<MiljoVelgerConnector
												heading={'Velg miljø å gjenopprett'}
												arrayHelpers={arrayHelpers}
												arrayValues={formikProps.values.environments}
											/>
										)}
									/>
									<div className="dollymodal_buttons">
										<Knapp autoFocus type="standard" onClick={this.closeModal}>
											Avbryt
										</Knapp>
										<Knapp type="hoved" onClick={formikProps.submitForm}>
											Utfør
										</Knapp>
									</div>
								</Fragment>
							)
						}}
					/>
				</div>
			</Fragment>
		)
	}

	_submitFormik = async values => {
		const envsQuery = Formatters.arrayToString(values.environments)
			.replace(/ /g, '')
			.toLowerCase()
		await this.props.gjenopprettBestilling(envsQuery)
		await this.props.getBestillinger()
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
							{error.split('%').join(' ')}
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
					{successEnvsStr.length > 0 ? (
						<StaticValue size={'medium'} header="Suksess" value={successEnvsStr} />
					) : (
						<StaticValue size={'medium'} header="Suksess" value={'Ingen'} />
					)}
					{failedEnvsStr.length > 0 && (
						<StaticValue size={'medium'} header="Feilet" value={failedEnvsStr} />
					)}
				</div>
			</Fragment>
		)
	}
}
