import React, { Component, Fragment } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'
import '~/pages/gruppe/BestillingListe/BestillingDetaljer/BestillingDetaljer.less'
import Formatters from '~/utils/DataFormatter'

import './Feilmelding.less'


export default class Feilmelding extends Component {
    _finnTpsfFeilStatus = tpsfStatus => {
        let tpsfFeil = []
        tpsfStatus.map (status => {
            if (status.statusMelding !== 'OK') {
                tpsfFeil.push (status)
            }
        })
        return tpsfFeil
    }

    _finnStubStatus = bestilling => {
       let stubStatus = []
        //const tpsfFeilStatus = this.finnTpsfFeilStatus(bestilling.tpsfStatus)
        const krrStubStatus = {navn: 'KRRSTUB', status: bestilling.krrStubStatus}
        const sigrunStubStatus = {navn: 'SIGRUNSTUB', status: bestilling.sigrunStubStatus}
        
        // Legger til feilmeldinger fra krrStub og sigrunStub i et array
        {(krrStubStatus.status && krrStubStatus.status[0].statusMelding !== 'OK') && stubStatus.push(krrStubStatus)}
        {(sigrunStubStatus.status && sigrunStubStatus.status[0].statusMelding !== 'OK') && stubStatus.push(sigrunStubStatus)}
    
        return stubStatus
    }

    _renderTPSFStatus = (tpsfFeil, cssClass, i) => {
        return (
            <div className={cssClass} key={i}>
                <span className = 'feil-kolonne_stor' >
                    {tpsfFeil.statusMelding}
                </span>
                <div className = 'feil-kolonne_stor' key={i}>
                    {Object.keys(tpsfFeil.environmentIdents).map((miljo,idx) => {
                            let identerPerMiljo = []
                            tpsfFeil.environmentIdents[miljo].map((ident) => {
                                !identerPerMiljo.includes(ident) && identerPerMiljo.push(ident)
                            })

                            const miljoUpperCase = miljo.toUpperCase()
                            const identerPerMiljoStr = Formatters.arrayToString(identerPerMiljo)
                            return (
                                <span className = 'feil-container' key ={idx}>
                                    <span className="feil-kolonne_liten">{miljoUpperCase}</span>
                                    <span className="feil-kolonne_stor">{identerPerMiljoStr}</span>
                                </span>
                            )
                    })} 
                </div>
            </div>
        )
    }

    _renderStubStatus = (bestilling, stubStatus, cssClass) =>  {
        return (stubStatus.map ((stub, i) => {
            const stubNavn = stub.navn
            const identer = Formatters.arrayToString(stub.status[0].identer)          
            //Ha linje mellom feilmeldingene, men ikke etter den siste
            if (!bestilling.feil) {const bottomBorder = i != stubStatus.length - 1
                cssClass = cn('feil-container', 
                {'feil-container feil-container_border': bottomBorder
            })}
            return (
                <div className={cssClass} key={i}>
                    <div className = 'feil-kolonne_stor'>
                        {stub.status[0].statusMelding}
                    </div>
                    <div className = 'feil-kolonne_stor'>
                            <span className = 'feil-container'>
                                <span className="feil-kolonne_liten">{stubNavn}</span>
                                <span className="feil-kolonne_stor">{identer}</span>
                            </span>
                    </div>
                </div>
            )
        }))
    }

    _renderAndreFeil = bestilling => {
        const cssClass = 'feil-container'
        return (
            <div className={cssClass}>
                <div className = 'feil-kolonne_stor'>
                    {bestilling.feil}
                </div>
                {/* Har foreløpig ikke miljø og identer å skrive inn */}
                <div className = 'feil-kolonne_stor'>
                        <span className = 'feil-container'>
                            <span className="feil-kolonne_liten"></span>
                            <span className="feil-kolonne_stor">{this.antallIdenterIkkeOpprettet(bestilling)} av {bestilling.antallIdenter} bestilte identer ble ikke opprettet i TPS</span>
                        </span>
                </div>
            </div>
        )
    }

    antallIdenterIkkeOpprettet = bestilling => {
        let identArray = []
        bestilling.tpsfStatus && bestilling.tpsfStatus.map (status => {
            Object.keys(status.environmentIdents).map((miljo) => {
                status.environmentIdents[miljo].map((ident) => {
                    !identArray.includes(ident) && identArray.push(ident)
                })
            })
        })
        return (bestilling.antallIdenter - identArray.length)
    }

    render(){
        const {bestilling} = this.props
        let cssClass = 'feil-container feil-container_border'
        const stubStatus = this._finnStubStatus(bestilling)

        //TODO: Samle tpsf, stub og generelle feil. Finere løsning på linjer mellom + rendering
        return (
            <Fragment>
                <span className="feil-container">
                    <h2 className = 'feil-header feil-header_stor'>Feilmelding</h2>
                    <span className = 'feil-kolonne_header'>
                        <h2 className = 'feil-header feil-header_liten'>Miljø</h2>
                        <h2 className = 'feil-header feil-header_stor'>Ident</h2>
                    </span>
                </span>
                {bestilling.tpsfStatus && this._finnTpsfFeilStatus(bestilling.tpsfStatus).map((feil, i) => { //Feilmeldinger fra tpsf
                    if (stubStatus.length < 1 && !bestilling.feil){
                        const bottomBorder = i != this._finnTpsfFeilStatus(bestilling.tpsfStatus).length - 1
                        cssClass = cn('feil-container', {'feil-container feil-container_border': bottomBorder})} 
                    return this._renderTPSFStatus(feil, cssClass, i)})
                }
                {stubStatus && this._renderStubStatus(bestilling, stubStatus, cssClass)} {/*Feilmeldinger fra Sigrun- og krrStub */}
                {bestilling.feil && this._renderAndreFeil(bestilling)} {/*Generelle feilmeldinger */}
            </Fragment>
        )
    }
}