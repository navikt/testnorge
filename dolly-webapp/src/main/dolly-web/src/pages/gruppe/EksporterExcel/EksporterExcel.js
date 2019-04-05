import React, { Component, Fragment } from 'react'
import Button from '~/components/button/Button'
import Loading from '~/components/loading/Loading'
import { TpsfApi } from '~/service/Api'
import Icon from '~/components/icon/Icon'

export default class EksportExcel extends Component {

    constructor(props){
        super(props);
        this.state = {
            loading: false,
        };
    }

    onClick = () => {
        const identliste = this.getIdentliste()
       
        return this.setState({loading: true}, async () => {
            try {
                const data = await TpsfApi.getExcelForIdenter(identliste)                    
                const href = "data:text/csv,\uFEFF" //uFEFF fikser æøå
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
                console.log('error: TpsfApi og nedlasting');
                return this.setState({loading: false})
            }
        })
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
        return (
            <Fragment>
                <Button className="flexbox--align-center gruppe-exceleksport" onClick = {() => this.onClick()}>
                    <Icon size={'24px'} kind={'file-new-table'} className= "excelknapp"/>
                    <span className= "excelknapp">EKSPORTER TIL CSV</span>
                </Button>
            </Fragment>
        )
    }

    render () {
        return ( 
                this.state.loading
                ?   <Loading label = "Eksporterer"/>
                :   this.eksportereData()
        )
    }
}