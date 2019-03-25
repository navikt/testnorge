import React, { Component, Fragment } from 'react'
import PropTypes from 'prop-types'
import Button from '~/components/button/Button'
import './EksisterendeIdent.less'
import {TpsfApi}  from '~/service/Api'
import Loading from '~/components/loading/Loading'
import Icon from '~/components/icon/Icon'
import AttributtVelgerConnector from '~/components/attributtVelger/AttributtVelgerConnector'


export default class EksisterendeIdent extends Component {

    static propTypes = {
        setIdentLister: PropTypes.func,
        eksisterendeIdentListe: PropTypes.arrayOf(PropTypes.string),
        ugyldigIdentListe: PropTypes.arrayOf(PropTypes.string)
    }
    
    constructor(props){
        super(props);
        this.state = {
            identListe: [],
            statusListe: [],
            loading: false,
            skriveModus: (this.props.eksisterendeIdentListe.length > 0 || this.props.ugyldigIdentListe.length > 0) 
                            ? false 
                            : true  
        };
    }

    handleChange = () => {
        this.setState({identListe: event.target.value})
    }

    onClick = () => {
        const identListe = document.getElementById('ident').value.split(/[\W\s]+/)
        
        return this.setState({loading: true}, async () => {
            try {
                const statusListe = await TpsfApi.checkpersoner(identListe)

                const identLister = {
                    gyldigIdentListe: [],
                    ugyldigIdentListe: []
                }
                statusListe.data.statuser.map (fnr => {
                    fnr.available == true ? identLister.gyldigIdentListe.push(fnr.ident) : identLister.ugyldigIdentListe.push(fnr.ident) //HUSK Å ENDRE TIL ===!
                })

                this.props.setIdentLister(identLister)
                return this.setState({loading: false, skriveModus: false, statusListe: statusListe, identListe: identListe })
            }
            catch (err){
                console.log('error TPSFApi');
                return this.setState({loading: false})
            }
        })
    }

    editAction = () => {
        this.state.identListe.length < 1
        ? this.setState({skriveModus: true, identListe: this.props.eksisterendeIdentListe.concat(this.props.ugyldigIdentListe)})
        : this.setState({skriveModus: true})
    }

    renderSkriveModus = () => {
        return (
            <div className ='eksisterende-fnr'>
                <div className = 'eksisterende-fnr-tekst'>Testidenter</div> 
                    <textarea 
                        className = 'eksisterende-fnr-tekstboks' 
                        id = 'ident' 
                        placeholder ="Skriv inn fnr/dnr" 
                        onChange={this.handleChange}
                        value = {this.state.identListe}
                    ></textarea>
                    <input 
                        className = 'eksisterende-fnr-hent-testident-knapp' 
                        type = 'button' 
                        value = 'Hent testident(er)' 
                        onClick = {this.onClick}
                    />
            </div>
        )
    }

    renderAttributtModus = () => {
        const {
            selectedAttributeIds,
			toggleAttributeSelection,
			uncheckAllAttributes,
			checkAttributeArray,
            uncheckAttributeArray,
            ugyldigIdentListe,
            eksisterendeIdentListe
        } = this.props

        return (
            <Fragment>
                <div className = 'eksisterende-fnr-tekst'>Testidenter</div> 
                <div className='eksisterende-fnr-status-container'>
                    <div className='eksisterende-fnr-plassering'>
                        <div className={eksisterendeIdentListe.length > 0 ? 'eksisterende-fnr-tekst-container' : 'eksisterende-fnr-tekst-container eksisterende-fnr-ugyldig'}>
                            <div className = 'eksisterende-fnr-gyldig-container'> 
                                {eksisterendeIdentListe && eksisterendeIdentListe.length > 0 ?
                                    this.renderIdentliste(eksisterendeIdentListe) 
                                    : 
                                    <div className = 'eksisterende-fnr-ingen-gyldige-identer'>
                                        <Icon size={'14px'} kind={'report-problem-triangle'} /> 
                                        <span className='eksisterende-fnr-liste'>Ingen gyldige identer. Vennligst fyll ut</span>
                                    </div>
                                }
                            </div>
                        </div>
                    </div>
                    <Button onClick={this.editAction} className="eksisterende-fnr-redigerknapp" kind="edit">REDIGER
                    </Button>
                </div>
                {ugyldigIdentListe.length > 0 && eksisterendeIdentListe.length > 0 &&
                <div className='eksisterende-fnr-status-container'> 
                    <div className='eksisterende-fnr-plassering'>
                        <div className = 'eksisterende-fnr-tekst-container eksisterende-fnr-ugyldig'>
                            <div className = 'eksisterende-fnr-ugyldig-container'> 
                                <div className='flexbox'>
                                    <Icon size={'14px'} kind={'report-problem-triangle'} />
                                    {eksisterendeIdentListe && eksisterendeIdentListe.length > 0 && 
                                        <div className = 'eksisterende-fnr-liste'>
                                            Noen av identene som ble lagt inn kan ikke opprettes:
                                        </div>
                                    }    
                                </div>
                                    <div className = 'eksisterende-fnr-gyldig-container'>
                                        {this.renderIdentliste(ugyldigIdentListe)}
                                    </div>
                            </div>
                        </div>
                    </div>
                </div>
                }
            <AttributtVelgerConnector
                onToggle={toggleAttributeSelection}
                uncheckAllAttributes={uncheckAllAttributes}
                checkAttributeArray={checkAttributeArray}
                uncheckAttributeArray={uncheckAttributeArray}
                selectedIds={selectedAttributeIds}
            />
        </Fragment>
        )
    }

    renderIdentliste = identListe => {
        return (
            <Fragment>
                {identListe.map ((fnr, i) => {
                    if (i === identListe.length-1) {
                        return (<span key={i}>{fnr}</span>)
                    } else {
                        return <span key={i}>{fnr + ','} &nbsp;</span>
                    }
                })}
            </Fragment>
        )
    }

    render () {
        const {loading, skriveModus} = this.state      
        
        return (
            <div>
            {   loading 
                    ? <Loading label = 'Henter status på inntastede identer'/> //Error hvis tpsf ikke svarer?
                    : skriveModus 
                        ? this.renderSkriveModus() 
                        : this.renderAttributtModus()
            }
            </div>  
        )
    }
}