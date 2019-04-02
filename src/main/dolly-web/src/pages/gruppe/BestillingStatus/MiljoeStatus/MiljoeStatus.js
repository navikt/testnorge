import React, { PureComponent, Fragment } from 'react'
import './MiljoeStatus.less'
import '~/styles/utils.less'
import cn from 'classnames'
import Icon from '~/components/icon/Icon'
import Button from '~/components/button/Button'
import DollyModal from '~/components/modal/DollyModal'
import BestillingDetaljerSammendrag from '~/components/bestillingDetaljerSammendrag/BestillingDetaljerSammendrag';

export default class MiljoeStatus extends PureComponent {
	constructor(props) {
		super(props)
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
			bestillingId,
			successEnvs,
			finnesFeilmelding,
			antallIdenterOpprettet,
		} = this.props.miljoeStatusObj

		const bestilling = this.props.bestilling

		const failed = true && successEnvs.length == 0 && !finnesFeilmelding
		const { modalOpen } = this.state

		return (
			<div className="miljoe-status">
				<div className="status-header">
					<p>Bestilling #{bestillingId}</p> 
					<h3>Bestillingsstatus</h3>
					<div className="remove-button-container">
						<Button kind="remove-circle" onClick={this.props.onCloseButton} />
					</div>
				</div>
				<hr />
				<div className={'miljoe-container miljoe-container-kolonne'}>
					{failed
						? this._renderFailureMessage(bestilling, antallIdenterOpprettet)
						: this._renderStatus(bestilling, antallIdenterOpprettet)}
				</div>
				{/* TODO: Alex - condition for feil i hele bestillingen her */}
				{bestilling.feil && (
					<div className="flexbox--all-center overall-feil-container">
						<Icon size={'16px'} kind={'report-problem-triangle'} />
						<p>{bestilling.feil} </p>
					</div>
				)}
				<div className="flexbox--all-center">
					<Button onClick={this.openModal} className="flexbox--align-center" kind="details">
						BESTILLINGSDETALJER
					</Button>
					<DollyModal
						isOpen={modalOpen}
						onRequestClose={this.closeModal}
						closeModal={this.closeModal}
						content={
							<BestillingDetaljerSammendrag 
								bestilling={bestilling} 
								type = 'modal'
							/>
						}
						width={'60%'}
					/>
				</div>
			</div>
		)
	}

	_renderStatus = (bestilling, antallIdenterOpprettet) => {
		return (
			<Fragment>
				{antallIdenterOpprettet < bestilling.antallIdenter && (
					<span className="miljoe-status error-text">
						{antallIdenterOpprettet} av {bestilling.antallIdenter} bestilte identer ble opprettet i
						TPS.
					</span>
				)}
				<span className="miljoe-container miljoe-container-rad">{this._renderMiljoeStatus()}</span>
			</Fragment>
		)
	}

	_renderMiljoeStatus = () => {
		const { successEnvs, failedEnvs, avvikEnvs } = this.props.miljoeStatusObj

		return (
			<Fragment>
				{successEnvs.map((env, i) => {
					return this._renderMiljoe(env, i, 'success')
				})}

				{failedEnvs.map((env, i) => {
					return this._renderMiljoe(env, i, 'failed')
				})}

				{avvikEnvs.map((env, i) => {
					return this._renderMiljoe(env, i, 'avvik')
				})}
			</Fragment>
		)
	}

	_renderFailureMessage = () => (
		<Fragment>
			<Icon kind={'report-problem-circle'} />
			<p>Bestillingen din ble ikke utført.</p>
		</Fragment>
	)

	_renderMiljoe = (env, key, status) => {
		let iconKind

		switch (status) {
			case 'avvik':
				iconKind = 'report-problem-circle'
				break
			case 'failed':
				iconKind = 'report-problem-triangle'
				break
			default:
				iconKind = 'feedback-check-circle'
				break
		}

		return (
			<div className={'miljoe'} key={key}>
				<Icon size={'24px'} kind={iconKind} />
				<p>{env}</p>
			</div>
		)
	}
}