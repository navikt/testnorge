import React, { PureComponent, Fragment } from 'react'
import Icon from '~/components/icon/Icon'
import Button from '~/components/button/Button'
import './BestillingDetaljer.less'
import Formatters from '~/utils/DataFormatter'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Modal from 'react-modal'
import Knapp from 'nav-frontend-knapper'
import Lukknapp from 'nav-frontend-lukknapp'
import { Formik, FieldArray } from 'formik'
import MiljoVelgerConnector from '~/components/miljoVelger/MiljoVelgerConnector'
import * as yup from 'yup'
import { mapBestillingData } from './BestillingDataMapper'
import cn from 'classnames'
import SendOpenAmConnector from '~/pages/gruppe/SendOpenAm/SendOpenAmConnector'
import OpenAmStatus from '~/pages/gruppe/OpenAmStatus/OpenAmStatus'

// TODO: Flytt modal ut som en dumb komponent
const customStyles = {
	content: {
		top: '50%',
		left: '50%',
		right: 'auto',
		bottom: 'auto',
		marginRight: '-50%',
		transform: 'translate(-50%, -50%)',
		width: '25%',
		minWidth: '500px',
		overflow: 'inherit'
	}
}

Modal.setAppElement('#root')

export default class BestillingDetaljer extends PureComponent {
	constructor(props) {
		super(props)

		this.EnvValidation = yup.object().shape({
			environments: yup.array().required('Velg minst ett miljø')
		})

		this.state = {
			modalOpen: false,
			openAmInfoOpen: false
		}
	}

	render() {
		const { successEnvs, failedEnvs, errorMsgs } = this.props.miljoeStatusObj
		const bestillingId = this.props.bestilling.id
		const { openAm, openAmState } = this.props
		let bestillingIdOpenAm = ''
		openAmState.response && (bestillingIdOpenAm = openAmState.response.config.url)

		console.log('bestillingId :', bestillingId)
		console.log('openAm :', openAm)
		console.log('openAmState :', openAmState)
		// TODO: Reverse Map detail data here. Alex
		return (
			<div className="bestilling-detaljer">
				{this._renderBestillingsDetaljer()}
				{this._renderMiljoeStatus(successEnvs, failedEnvs)}
				{errorMsgs.length > 0 && this._renderErrorMessage(errorMsgs)}

				<div>
					{openAm && (
						<div className="bestilling-detaljer">
							<h3>Jira-lenker</h3>
							<div className={'jira-link'}>{this._renderJiraLinks(openAm)}</div>
						</div>
					)}
				</div>

				{bestillingIdOpenAm ===
					'https://dolly-u2.nais.preprod.local/api/v1/openam/bestilling/{bestillingId}?bestillingId=' +
						bestillingId && (
					<OpenAmStatus
						responses={this._renderOpenAmStateResponses(openAmState.response)}
						className="open-am-status"
					/>
					// this.state.openAmInfoOpen = true
				)}

				<div className="flexbox--align-center--justify-end info-block">
					{openAm == undefined && (
						<div className="button">
							{bestillingId && (
								<SendOpenAmConnector
									bestillingId={bestillingId}
									className="flexbox--align-center button"
								/>
							)}
							{this._setOpenAmState()}
						</div>
					)}

					<div className="button">
						<Button
							onClick={this._onToggleModal}
							className="flexbox--align-center button"
							kind="synchronize"
						>
							GJENOPPRETT I TPS
						</Button>
						{this._renderModal()}
					</div>
				</div>
			</div>
		)
	}

	// _setState = () => {

	// }

	_renderOpenAmStateResponses = openAmState => {
		// console.log('openAmState :', openAmState)
		// const responses = openAmState.map((respons, i) => {
		const responses = []
		openAmState.data.forEach(response => {
			responses.push(response.message)
		})
		return responses
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

	//array.join(separator)

	_renderModal = () => {
		const { environments, id } = this.props.bestilling // miljø som ble bestilt i en bestilling

		return (
			<Modal
				isOpen={this.state.modalOpen}
				onRequestClose={this._onToggleModal}
				shouldCloseOnEsc
				style={customStyles}
			>
				<div className="openam-modal">
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
									<div className="openam-modal_buttons">
										<Knapp autoFocus type="standard" onClick={this._onToggleModal}>
											Avbryt
										</Knapp>
										<Knapp type="hoved" onClick={formikProps.submitForm}>
											Utfør
										</Knapp>
									</div>
								</Fragment>
							)
						}}
					/>{' '}
					<Lukknapp onClick={this._onToggleModal} />
				</div>
			</Modal>
		)
	}

	_renderOpenAmInfo = () => {
		const openAm = this.props.openAm
		// console.log('openAm :', openAm)
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

	_onToggleOpenAm = () => {
		this.setState({ openAmInfoOpen: !this.state.openAmInfoOpen })
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
