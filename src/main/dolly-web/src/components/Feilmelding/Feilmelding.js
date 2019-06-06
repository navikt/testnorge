import React, { Component, Fragment } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'
import '~/pages/gruppe/BestillingListe/BestillingDetaljer/BestillingDetaljer.less'
import Formatters from '~/utils/DataFormatter'

import './Feilmelding.less'

export default class Feilmelding extends Component {
	render() {
		const { bestilling } = this.props
		let cssClass = 'feil-container feil-container_border'
		const stubStatus = this._finnStubStatus(bestilling)
		// TODO: Refaktor
		const finnesTPSFEllerStub =
			(bestilling.tpsfStatus && this._finnTpsfFeilStatus(bestilling.tpsfStatus).length > 0) ||
			stubStatus.length > 0 ||
			(bestilling.aaregStatus && this._finnTpsfFeilStatus(bestilling.aaregStatus).length > 0) ||
			(bestilling.arenaforvalterStatus &&
				this._finnTpsfFeilStatus(bestilling.arenaforvalterStatus).length > 0)
		return (
			<div className="feil-melding">
				{/*Generelle feilmeldinger */}
				{bestilling.feil && this._renderGenerelleFeil(bestilling, cssClass, finnesTPSFEllerStub)}
				{finnesTPSFEllerStub && (
					<span className="feil-container">
						<h2 className="feil-header feil-header_stor">Feilmelding</h2>
						<span className="feil-kolonne_header">
							<h2 className="feil-header feil-header_liten">Miljø</h2>
							<h2 className="feil-header feil-header_stor">Ident</h2>
						</span>
					</span>
				)}
				{bestilling.tpsfStatus &&
					this._finnTpsfFeilStatus(bestilling.tpsfStatus).map((feil, i) => {
						//Feilmeldinger fra tpsf
						if (stubStatus.length < 1) {
							const bottomBorder = i != this._finnTpsfFeilStatus(bestilling.tpsfStatus).length - 1
							cssClass = cn('feil-container', {
								'feil-container feil-container_border': bottomBorder
							})
						}
						return this._renderTPSFStatus(feil, cssClass, i)
					})}
				{/* Gjenbruke tpsf-metodene for AAREG siden de har helt like format */}
				{/* TODO: Lag en generell metode med generelle metodenavn hvis det blir en ny register med samme format */}
				{bestilling.aaregStatus &&
					this._finnTpsfFeilStatus(bestilling.aaregStatus).map((feil, i) => {
						//Feilmeldinger fra tpsf
						if (stubStatus.length < 1) {
							const bottomBorder = i != this._finnTpsfFeilStatus(bestilling.aaregStatus).length - 1
							cssClass = cn('feil-container', {
								'feil-container feil-container_border': bottomBorder
							})
						}
						return this._renderAaregStatus(feil, cssClass, i)
					})}
				{bestilling.arenaforvalterStatus &&
					this._finnTpsfFeilStatus(bestilling.arenaforvalterStatus).map((feil, i) => {
						if (stubStatus.length < 1) {
							const bottomBorder =
								i != this._finnTpsfFeilStatus(bestilling.arenaforvalterStatus).length - 1
							cssClass = cn('feil-container', {
								'feil-container feil-container_border': bottomBorder
							})
						}
						return this._renderArenaStatus(feil, cssClass, i)
					})}
				{/*Feilmeldinger fra Sigrun- og krrStub */}
				{stubStatus && this._renderStubStatus(stubStatus, cssClass)}
			</div>
		)
	}

	_finnTpsfFeilStatus = tpsfStatus => {
		let tpsfFeil = []
		tpsfStatus.map(status => {
			if (status.statusMelding && status.statusMelding !== 'OK') {
				tpsfFeil.push(status)
			}
			if (status.status && status.status !== 'OK') {
				tpsfFeil.push(status)
			}
		})

		return tpsfFeil
	}

	_finnStubStatus = bestilling => {
		let stubStatus = []
		//const tpsfFeilStatus = this.finnTpsfFeilStatus(bestilling.tpsfStatus)
		const krrStubStatus = { navn: 'KRRSTUB', status: bestilling.krrStubStatus }
		const sigrunStubStatus = { navn: 'SIGRUNSTUB', status: bestilling.sigrunStubStatus }

		// Legger til feilmeldinger fra krrStub og sigrunStub i et array
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

		return stubStatus
	}

	_finnArenaStatus = bestilling => {
		let arenaStatus = []
		const arenaforvalterStatus = { navn: 'ARENA', status: bestilling.arenaforvalterStatus }
		arenaforvalterStatus.status &&
			arenaforvalterStatus.status.map(miljo => {
				if (miljo.status !== 'OK') {
					arenaStatus.push(miljo)
				}
			})
		return arenaStatus
	}

	_renderTPSFStatus = (tpsfFeil, cssClass, i) => {
		return (
			<Fragment key={i}>
				<h5>TPSF</h5>
				<div className={cssClass}>
					<span className="feil-kolonne_stor">{tpsfFeil.statusMelding}</span>
					<div className="feil-kolonne_stor" key={i}>
						{Object.keys(tpsfFeil.environmentIdents).map((miljo, idx) => {
							let identerPerMiljo = []
							tpsfFeil.environmentIdents[miljo].map(ident => {
								!identerPerMiljo.includes(ident) && identerPerMiljo.push(ident)
							})

							const miljoUpperCase = miljo.toUpperCase()
							const identerPerMiljoStr = Formatters.arrayToString(identerPerMiljo)
							return (
								<span className="feil-container" key={idx}>
									<span className="feil-kolonne_liten">{miljoUpperCase}</span>
									<span className="feil-kolonne_stor">{identerPerMiljoStr}</span>
								</span>
							)
						})}
					</div>
				</div>
			</Fragment>
		)
	}

	_renderAaregStatus = (aaregFeil, cssClass, i) => {
		return (
			<Fragment key={i}>
				<h5>AAREG</h5>
				<div className={cssClass}>
					<span className="feil-kolonne_stor">{aaregFeil.statusMelding}</span>
					<div className="feil-kolonne_stor" key={i}>
						{Object.keys(aaregFeil.environmentIdentsForhold).map((miljo, idx) => {
							const identerPerMiljo = Object.keys(aaregFeil.environmentIdentsForhold[miljo])
							const miljoUpperCase = miljo.toUpperCase()
							const identerPerMiljoStr = Formatters.arrayToString(identerPerMiljo)
							return (
								<span className="feil-container" key={idx}>
									<span className="feil-kolonne_liten">{miljoUpperCase}</span>
									<span className="feil-kolonne_stor">{identerPerMiljoStr}</span>
								</span>
							)
						})}
					</div>
				</div>
			</Fragment>
		)
	}

	_renderStubStatus = (stubStatus, cssClass) => {
		return stubStatus.map((stub, i) => {
			const stubNavn = stub.navn
			const identer = Formatters.arrayToString(stub.status[0].identer)
			//Ha linje mellom feilmeldingene, men ikke etter den siste
			const bottomBorder = i != stubStatus.length - 1
			cssClass = cn('feil-container', {
				'feil-container feil-container_border': bottomBorder
			})
			return (
				<div className={cssClass} key={i}>
					<div className="feil-kolonne_stor">{stub.status[0].statusMelding}</div>
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

	_renderArenaStatus = (arenaFeil, cssClass, i) => {
		return (
			<Fragment key={i}>
				<h5>ARENA</h5>
				<div className={cssClass}>
					<span className="feil-kolonne_stor">{arenaFeil.status}</span>
					<div className="feil-kolonne_stor" key={i}>
						{Object.keys(arenaFeil.envIdent).map((miljo, idx) => {
							let identerPerMiljo = []
							arenaFeil.envIdent[miljo].map(ident => {
								!identerPerMiljo.includes(ident) && identerPerMiljo.push(ident)
							})

							const miljoUpperCase = miljo.toUpperCase()
							const identerPerMiljoStr = Formatters.arrayToString(identerPerMiljo)
							return (
								<span className="feil-container" key={idx}>
									<span className="feil-kolonne_liten">{miljoUpperCase}</span>
									<span className="feil-kolonne_stor">{identerPerMiljoStr}</span>
								</span>
							)
						})}
					</div>
				</div>
			</Fragment>
		)
	}

	_renderGenerelleFeil = (bestilling, cssClass, finnesTPSFEllerStub) => {
		!finnesTPSFEllerStub && (cssClass = 'feil-container')
		return (
			<div className={cssClass}>
				<div className="feil-kolonne_stor">{bestilling.feil}</div>
				{/* Har foreløpig ikke miljø og identer å skrive inn */}
				<div className="feil-kolonne_stor">
					<span className="feil-container">
						<span className="feil-kolonne_liten" />
						<span className="feil-kolonne_stor">
							{this.antallIdenterOpprettet(bestilling)} av {bestilling.antallIdenter} bestilte
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
}
