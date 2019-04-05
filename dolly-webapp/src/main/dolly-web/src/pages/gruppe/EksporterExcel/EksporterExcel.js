import React, { Component, Fragment } from 'react'
import Button from '~/components/button/Button'
import Loading from '~/components/loading/Loading'
import { TpsfApi } from '~/service/Api'
import Icon from '~/components/icon/Icon'
import ConfirmTooltip from '~/components/confirmTooltip/ConfirmTooltip'
import DollyModal from '~/components/modal/DollyModal'
import Modal from 'react-modal'
import Lukknapp from 'nav-frontend-lukknapp'
import Knapp from 'nav-frontend-knapper'

export default class EksportExcel extends Component {

    constructor(props){
        super(props);
        this.state = {
            loading: false,
            //modalOpen: false,
            //data: []
        };
    }

	// openModal = () => {
	// 	this.setState({ modalOpen: true })
	// }
	// closeModal = () => {
	// 	this.setState({ modalOpen: false })
	// }
    
    onClick = () => {
        if (this.props.testidenter) {
            const identliste = this.getIdentliste()
            return this.setState({loading: true}, async () => {
                try {
                    const data = await TpsfApi.getExcelForIdenter(identliste)                    
                    const href = "data:text/csv,\uFEFF"
                    const dagensDato = this.getDato()
                    const link = document.createElement('a')

                    //Lager en link til nedlasting som aktiveres uten klikk
                    link.href = href+data.data
                    link.download = this.props.gruppeId + "_"+ dagensDato + ".csv" 
                    link.target="_blank"
                    document.body.appendChild(link)
                    link.click()
                    document.body.removeChild(link)

                    return (this.setState({loading: false}))
                }
                catch (err){
                    console.log('error: TpsfApi + nedlasting');
                    return this.setState({loading: false})
                }
            })
        }
    }

    getIdentliste = () => {
        let identliste = []
        this.props.testidenter.map ( ident => {
            identliste.push(ident.ident)
        })
        return identliste
    }

    getDato = () => {
        const dato = new Date()
        let dd = String(dato.getDate())
        if (dd < 10) {dd = '0' + dd}

        let mm = String(dato.getMonth() + 1)
        if (mm < 10) {mm = '0'+ mm}

        return String(dato.getFullYear()) + mm + dd
    }
    
    eksportereData = () => {
        // const filnavn = this.props.gruppeId + ".csv"
        // const modaltekst = "Vil du eksportere testdata for gruppe " + this.props.gruppeId + "?"

        return (
            <Fragment>
                <Button className="flexbox--align-center gruppe-exceleksport" onClick = {() => this.onClick()}>
                    <Icon size={'24px'} kind={'file-new-lines'} className= "excelknapp"/>
                    <span className= "excelknapp">EKSPORTER SOM CSV</span>
                </Button>
                {/* <DollyModal
                    isOpen={this.state.modalOpen}
                    onRequestClose={this.closeModal}
                    closeModal={this.closeModal}
                    content={modaltekst}
                    width={'60%'}
                    children={this.lastNedData(filnavn, this.state.data)}
                 /> */}
            </Fragment>
        )
    }

	// lastNedData = (filnavn, data) => {
    //     const href = "data:application/vnd.ms-excel,"
	// 	return (
    //         <Fragment>
    //             <Lukknapp onClick={this.closeModal} />
	// 		<div className = "lastNed">
    //             <div>
    //                 <h3>Eksporter som CSV</h3>
    //                 <p>Eksporterer TPSF-data for {(this.props.testidenter.length).toString()} testpersoner med relasjoner (partner/barn). </p>
	// 			</div>
    //             <a href={href+data} download={filnavn} target="_blank">
	// 				<Knapp type = "hoved" onClick = {this.closeModal} className = "knapp">EKSPORTER</Knapp>
	// 			</a>
	// 			<Knapp type = "standard" onClick = {this.closeModal} className = "knapp">AVBRYT</Knapp>
	// 		</div>
    //         </Fragment>
	// 	)
	// }

    render () {
        return ( 
                this.state.loading
                ?   <Loading onlySpinner/>
                :   this.eksportereData()
        )
    }
}