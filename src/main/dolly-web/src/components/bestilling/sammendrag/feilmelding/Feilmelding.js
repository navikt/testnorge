import React, { Component, Fragment } from 'react'
import cn from 'classnames'
import Icon from '~/components/icon/Icon'
import miljoeStatusSelector from '~/utils/MiljoeStatusSelector'
import Formatters from '~/utils/DataFormatter'

import './Feilmelding.less'

export default class Feilmelding extends Component {
	render() {
		const { bestilling } = this.props
		const bestillingDetaljer = miljoeStatusSelector(bestilling)

		if (!bestillingDetaljer.finnesFeilmelding) return false

		let cssClass = 'feil-container feil-container_border'
		const stubStatus = this._finnStubStatus(bestilling)
		const pdlforvalterStatus =
			bestilling.pdlforvalterStatus && this._finnPdlforvalterStatus(bestilling)
		const finnesIndividuellFeil =
			this._finnFeilStatus(bestilling.tpsfStatus).length > 0 ||
			this._finnFeilStatus(bestilling.aaregStatus).length > 0 ||
			this._finnFeilStatus(bestilling.arenaforvalterStatus).length > 0 ||
			this._finnFeilStatus(bestilling.instdataStatus).length > 0 ||
			(pdlforvalterStatus && pdlforvalterStatus.length > 0) ||
			stubStatus.length > 0

		return (
			<Fragment>
				<div className="flexbox--align-center error-header">
					<Icon size="16px" kind="report-problem-triangle" />
					<h3>Feilmeldinger</h3>
				</div>
				<div className="feil-melding">
					{/*Generelle feilmeldinger */}
					{bestilling.feil &&
						this._renderGenerelleFeil(bestilling, cssClass, finnesIndividuellFeil)}

					{/*Feilmeldinger fra ulike registre */}
					{finnesIndividuellFeil && (
						<span className="feil-container">
							<h2 className="feil-header feil-header_stor">Feilmelding</h2>
							<span className="feil-kolonne_header">
								<h2 className="feil-header feil-header_liten">Miljø</h2>
								<h2 className="feil-header feil-header_stor">Ident</h2>
							</span>
						</span>
					)}

					{/*Individuelle feilmeldinger med miljø */}
					{bestilling.tpsfStatus &&
						this._renderStatusMedMiljoOgIdent(bestilling.tpsfStatus, stubStatus, 'TPSF', cssClass)}
					{bestilling.aaregStatus &&
						this._renderStatusMedMiljoOgIdent(
							bestilling.aaregStatus,
							stubStatus,
							'AAREG',
							cssClass
						)}
					{bestilling.arenaforvalterStatus &&
						this._renderStatusMedMiljoOgIdent(
							bestilling.arenaforvalterStatus,
							stubStatus,
							'ARENA',
							cssClass
						)}
					{bestilling.instdataStatus &&
						this._renderStatusMedMiljoOgIdent(
							bestilling.instdataStatus,
							stubStatus,
							'INST',
							cssClass
						)}

					{/*Individuelle feilmeldinger uavhengig av miljø*/}
					{stubStatus && this._renderStatusUavhengigAvMiljo(stubStatus, cssClass)}
					{pdlforvalterStatus && this._renderStatusUavhengigAvMiljo(pdlforvalterStatus, cssClass)}
				</div>
			</Fragment>
		)
	}

	_finnStubStatus = bestilling => {
		let stubStatus = []
		const krrStubStatus = { navn: 'KRRSTUB', status: bestilling.krrStubStatus }
		const sigrunStubStatus = { navn: 'SIGRUNSTUB', status: bestilling.sigrunStubStatus }
		const udiStubStatus = { navn: 'UDISTUB', status: bestilling.udiStubStatus }

		// Legger til feilmeldinger fra krrStub, sigrunStub og udiStub i et array
		{
			krrStubStatus.status &&
				krrStubStatus.status[0].statusMelding !== 'OK' &&
				stubStatus.push(krrStubStatus)
		}
		{
			sigrunStubStatus.status &&
				sigrunStubStatus.status[0].statusMelding !== 'OK' &&
				stubStatus.push(sigrunStubStatus)
		}
		{
			udiStubStatus.status &&
				udiStubStatus.status[0].statusMelding !== 'OK' &&
				stubStatus.push(udiStubStatus)
		}

		return stubStatus
	}

	_finnPdlforvalterStatus = bestilling => {
		let pdlfStatuser = []

		Object.keys(bestilling.pdlforvalterStatus).map(pdlfAttr => {
			bestilling.pdlforvalterStatus[pdlfAttr].map(status => {
				if (status.statusMelding !== 'OK') {
					pdlfStatuser.push({
						navn: 'PDL-forvalter',
						status:
							pdlfAttr === 'pdlForvalter'
								? status.statusMelding
								: pdlfAttr + ': ' + status.statusMelding,
						identer: status.identer
					})
				}
			})
		})

		return pdlfStatuser
	}

	_finnFeilStatus = statuser => {
		if (!statuser) return []
		let feil = []
		statuser.map(status => {
			const statusMelding = status.statusMelding || status.status
			statusMelding !== 'OK' && feil.push(status)
		})
		return feil
	}

	_renderGenerelleFeil = (bestilling, cssClass, finnesIndividuellFeil) => {
		!finnesIndividuellFeil && (cssClass = 'feil-container')
		const antallOpprettet = this.antallIdenterOpprettet(bestilling)
		return (
			<div className={cssClass}>
				<div className="feil-kolonne_stor">{bestilling.feil}</div>
				<div className="feil-kolonne_stor">
					<span className="feil-container">
						<span className="feil-kolonne_liten" />
						<span className="feil-kolonne_stor">
							{antallOpprettet < 1
								? 'Ingen'
								: `${antallOpprettet} av ${bestilling.antallIdenter} bestilte`}{' '}
							identer ble opprettet i TPSF
						</span>
					</span>
				</div>
			</div>
		)
	}

	antallIdenterOpprettet = bestilling => {
		let identArray = []
		bestilling.tpsfStatus &&
			bestilling.tpsfStatus.map(status => {
				Object.keys(status.environmentIdents).map(miljo => {
					status.environmentIdents[miljo].map(ident => {
						!identArray.includes(ident) && identArray.push(ident)
					})
				})
			})
		return identArray.length
	}

	_renderStatusMedMiljoOgIdent = (bestillingStatus, stubStatus, header, cssClass) => {
		return this._finnFeilStatus(bestillingStatus).map((feil, i) => {
			stubStatus.length < 1 && (cssClass = this.finnCSSklasse(bestillingStatus, i))

			const miljoOgIdenter =
				feil.environmentIdents || feil.environmentIdentsForhold || feil.envIdent
			const statusMelding = feil.statusMelding || feil.status
			return (
				<Fragment key={i}>
					<h5>{header}</h5>
					<div className={cssClass}>
						<span className="feil-kolonne_stor">{statusMelding}</span>
						<div className="feil-kolonne_stor" key={i}>
							{Object.keys(miljoOgIdenter).map((miljo, idx) => {
								return this._renderIdenterOgMiljo(miljoOgIdenter, miljo, idx)
							})}
						</div>
					</div>
				</Fragment>
			)
		})
	}

	finnCSSklasse = (status, i) => {
		const bottomBorder = i != this._finnFeilStatus(status).length - 1
		return cn('feil-container', {
			'feil-container feil-container_border': bottomBorder
		})
	}

	_renderIdenterOgMiljo = (miljoOgIdenter, miljo, idx) => {
		let identerPerMiljo
		if (Array.isArray(miljoOgIdenter[miljo])) {
			identerPerMiljo = []
			miljoOgIdenter[miljo].map(ident => {
				!identerPerMiljo.includes(ident) && identerPerMiljo.push(ident)
			})
		} else {
			identerPerMiljo = Object.keys(miljoOgIdenter[miljo])
		}
		return (
			<span className="feil-container" key={idx}>
				<span className="feil-kolonne_liten">{miljo.toUpperCase()}</span>
				<span className="feil-kolonne_stor">{Formatters.arrayToString(identerPerMiljo)}</span>
			</span>
		)
	}

	_renderStatusUavhengigAvMiljo = (stubStatus, cssClass) => {
		return stubStatus.map((stub, i) => {
			const stubNavn = stub.navn
			const identer = Formatters.arrayToString(stub.status[0].identer || stub.identer)
			const statusmelding = stub.status[0].statusMelding || stub.status

			//Ha linje mellom feilmeldingene, men ikke etter den siste
			const bottomBorder = i != stubStatus.length - 1
			cssClass = cn('feil-container', {
				'feil-container feil-container_border': bottomBorder
			})
			return (
				<div className={cssClass} key={i}>
					<div className="feil-kolonne_stor">{statusmelding}</div>
					<div className="feil-kolonne_stor">
						<span className="feil-container">
							<span className="feil-kolonne_liten">{stubNavn}</span>
							<span className="feil-kolonne_stor">{identer}</span>
						</span>
					</div>
				</div>
			)
		})
	}
}
