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
			modalOpen: false
		}
	}

	render() {
		const { successEnvs, failedEnvs, tpsfStatus, stubStatus, statusmeldingFeil } = this.props.miljoeStatusObj
		// TODO: Reverse Map detail data here. Alex
		return (
			<div className="bestilling-detaljer">
				{this._renderBestillingsDetaljer()}
				{this._renderMiljoeStatus(successEnvs, failedEnvs)}
				{statusmeldingFeil.length > 0 && this._renderErrorMessage(tpsfStatus, stubStatus)}
				<div className="flexbox--align-center--justify-end">
					<Button
						onClick={this._onToggleModal}
						className="flexbox--align-center"
						kind="synchronize"
					>
						GJENOPPRETT I TPS
					</Button>
					{this._renderModal()}
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

	_renderErrorMessage = (tpsfStatus, stubStatus) => {
		return (
			<Fragment>
				<div className="flexbox--align-center error-header">
					<Icon size={'16px'} kind={'report-problem-triangle'} />
					<h3>Feilmeldinger</h3>
				</div>
				<div className="feil-container">
					<div className = 'feil-header feil-header_stor'>Feilmelding</div>
					<div className = 'feil-kolonne_header'>
						<div className = 'feil-header feil-header_liten'>Miljø</div>
						<div className = 'feil-header feil-header_stor'>Ident</div>
					</div>
				</div>
				{tpsfStatus.map((feil, i) => { //feilmeldinger fra tpsf
						if (feil.statusMelding !== 'OK'){
							return (							
								<div className='feil-container feil-container_border' key={i}>
									<div className = 'feil-kolonne_stor' >
										{feil.statusMelding}
									</div>
									<div className = 'feil-kolonne_stor' key={i}>
										{Object.keys(feil.environmentIdents).map((miljo,idx) => {
												let identerPerMiljo = []
												feil.environmentIdents[miljo].map((ident) => {
													!identerPerMiljo.includes(ident) && identerPerMiljo.push(ident)
												})

												const miljoUpperCase = miljo.toUpperCase()
												const identerPerMiljoStr = Formatters.arrayToString(identerPerMiljo)
												return (
													<div className = 'feil-container' key ={idx}>
														<div className="feil-kolonne_liten">{miljoUpperCase}</div>
														<div className="feil-kolonne_stor">{identerPerMiljoStr}</div>
													</div>
												)
										})} 
									</div>
								</div>
							)
						}
					}
				)}
				{stubStatus && stubStatus.map ((stub, idx) => { //feilmeldinger fra sigrun- og krrstub
					const miljoUpperCase = stub.navn
					const identerPerMiljoStr = Formatters.arrayToString(stub.status[0].identer)
					return (
						<div className='feil-container feil-container_border' key={idx}>
							<div className = 'feil-kolonne_stor' >
								{stub.status[0].statusMelding}
							</div>
							<div className = 'feil-kolonne_stor'>
									<div className = 'feil-container'>
										<div className="feil-kolonne_liten">{miljoUpperCase}</div>
										<div className="feil-kolonne_stor">{identerPerMiljoStr}</div>
									</div>
							</div>
						</div>
					)
				})}
			</Fragment>
		)
	}


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
