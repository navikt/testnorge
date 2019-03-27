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
import SendOpenAmConnector from '~/pages/gruppe/SendOpenAm/SendOpenAmConnector'
import OpenAmStatusConnector from '~/pages/gruppe/OpenAmStatus/OpenAmStatusConnector'
import DollyModal from '~/components/modal/DollyModal'
import Feilmelding from '~/components/Feilmelding/Feilmelding'

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
		const {
			successEnvs,
			failedEnvs,
			avvikEnvs,
			bestilling,
			finnesFeilmelding
		} = this.props.miljoeStatusObj
		const bestillingId = this.props.bestilling.id
		const { openAm, openAmState } = this.props
		const { modalOpen } = this.state

		let openAmRes
		if (openAmState.responses.length > 0) {
			openAmRes = openAmState.responses.find(response => response.id == bestillingId)
		}

		// TODO: Reverse Map detail data here. Alex
		return (
			<div className="bestilling-detaljer">
				{this._renderBestillingsDetaljer()}
				{this._renderMiljoeStatus(successEnvs, failedEnvs, avvikEnvs)}
				{finnesFeilmelding && this._renderErrorMessage(bestilling)}
				{/* <div className="flexbox--align-center--justify-end"> */}
				{openAm ? (
					<div className="bestilling-detaljer">
						<h3>Jira-lenker</h3>
						<div className={'jira-link'}>{this._renderJiraLinks(openAm)}</div>
					</div>
				) : (
					openAmRes && (
						<OpenAmStatusConnector
							id={bestillingId}
							lukket={openAmRes.lukket}
							responses={this._renderOpenAmStateResponses(openAmRes)}
							className="open-am-status"
						/>
					)
				)}
				<div className="flexbox--align-center--justify-end info-block">
					<div className="flexbox--align-center--justify-end">
						{!openAm &&
							(!openAmRes && (
								<SendOpenAmConnector
									bestillingId={bestillingId}
									className="flexbox--align-center"
								/>
							))}
					</div>
					<div className="flexbox--align-center--justify-end">
						{this._erIdentOpprettet() && (
							<Button onClick={this.openModal} className="flexbox--align-center" kind="synchronize">
								GJENOPPRETT
							</Button>
						)}
						<DollyModal
							isOpen={modalOpen}
							onRequestClose={this.closeModal}
							closeModal={this.closeModal}
							content={this._renderGjenopprettModal()}
						/>
					</div>
				</div>
			</div>
			// </div>
		)
	}

	_renderOpenAmStateResponses = openAmState => {
		const responses = []
		openAmState.data.forEach(response => {
			responses.push(response.message)
		})
		return responses
	}

	_erIdentOpprettet = () => {
		const { bestilling } = this.props
		let temp = false

		{
			bestilling.tpsfStatus &&
				bestilling.tpsfStatus.map(status => {
					if (status.environmentIdents) temp = true
				})
		}
		return temp
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
				</div>
			</Fragment>
		)
	}

	_renderJiraLinks = openAm => {
		const data = openAm.split(',')
		const linkArray = data.map((respons, i) => {
			const link = respons
			return (
				<Fragment key={i}>
					<a href={link} target="_blank">
						{link.substring(28, link.length)}
					</a>
					{i !== data.length - 1 && <p>, </p>}
				</Fragment>
			)
		})
		return linkArray
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

	_onToggleModal = () => {
		this.setState({ modalOpen: !this.state.modalOpen })
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
}
