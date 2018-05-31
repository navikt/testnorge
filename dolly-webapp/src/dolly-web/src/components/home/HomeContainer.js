import React, {Component} from 'react';
import { bindActionCreators } from 'redux';
import {connect} from 'react-redux';
import * as gruppeActions from '../../actions/gruppeActions';
import GruppeList from "./GruppeListe";
import Table from '../../components/felles/table/Table'
import InputAutocompleteField from '../felles/fields/InputAutocompleteField';
import InputDatePicker from '../felles/fields/InputDatePicker';
import moment from 'moment';
import './HomeContainer.css';
import KnappBase from 'nav-frontend-knapper';
import Team from '../team/TeamContainer';
import NyGruppe from '../gruppe/NyGruppeContainer';


class Home extends Component{
    constructor(props, context){
        super(props, context);

        this.state = {
            postnummer: {},
            date: moment(),
            date_formatted: "",
            showGruppeForm: false,
            showTeamForm: false
        };

        this.createPerson = this.createPerson.bind(this);
        this.onSelectedValue = this.onSelectedValue.bind(this);
        this.onChangeUpdateDate = this.onChangeUpdateDate.bind(this);
        this.showNyGruppeForm = this.showNyGruppeForm.bind(this);
        this.showTeamForm = this.showTeamForm.bind(this);
    }

    createPerson(){
        //this.props.personsActions.createPerson({});
    }

    onSelectedValue(value){
        this.setState( {postnummer: Object.assign({}, value) });
    }

    onChangeUpdateDate(date) {
        let date_formatted = date.format("DD/MM/YYYY");

        this.setState( {
            date_formatted: date_formatted,
            date: date
        });
    }

    showNyGruppeForm() {
        this.setState({
            showGruppeForm: !this.state.showGruppeForm
        });
    }

    showTeamForm() {
        this.setState({
            showTeamForm: !this.state.showTeamForm
        });
    }

    render(){
        const {grupper} = this.props;

        const kodeverk = [{"navn":"AAP","term":"Arbeidsavklaringspenger"},{"navn":"AAR","term":"Aa-registeret"},{"navn":"AGR","term":"Ajourhold - Grunnopplysninger"},{"navn":"BAR","term":"Barnetrygd"},{"navn":"BID","term":"1 - Bidrag"},{"navn":"BII","term":"2 - Bidrag innkreving"},{"navn":"BIL","term":"Bil"},{"navn":"DAG","term":"Dagpenger"},{"navn":"ENF","term":"Enslig forsørger"},{"navn":"ERS","term":"Erstatning"},{"navn":"FEI","term":"Feilutbetaling"},{"navn":"FOR","term":"Foreldre- og svangerskapspenger"},{"navn":"FOS","term":"Forsikring"},{"navn":"FUL","term":"Fullmakt"},{"navn":"GEN","term":"Generell"},{"navn":"GRA","term":"Gravferdsstønad"},{"navn":"GRU","term":"Grunn- og hjelpestønad"},{"navn":"HEL","term":"Helsetjenester og ort. Hjelpemidler"},{"navn":"HJE","term":"Hjelpemidler"},{"navn":"IAR","term":"Inkluderende Arbeidsliv"},{"navn":"IND","term":"Individstønad"},{"navn":"KLA","term":"Klage/Anke"},{"navn":"KNA","term":"Kontakt NAV"},{"navn":"KOM","term":"Kommunale tjenester"},{"navn":"KON","term":"Kontantstøtte"},{"navn":"KTR","term":"Kontroll"},{"navn":"MED","term":"Medlemskap"},{"navn":"MOB","term":"Mob.stønad"},{"navn":"MOT","term":"3 - Skanning"},{"navn":"OKO","term":"Økonomi"},{"navn":"OMS","term":"Omsorgspenger, Pleiepenger og opplæringspenger"},{"navn":"OPA","term":"Oppfølging - Arbeidsgiver"},{"navn":"OPP","term":"Oppfølging"},{"navn":"OVR","term":"4 - Øvrig"},{"navn":"PEN","term":"Pensjon"},{"navn":"PER","term":"Permittering og masseoppsigelser"},{"navn":"REH","term":"Rehabilitering"},{"navn":"REK","term":"Rekruttering og Stilling"},{"navn":"RPO","term":"Retting av personopplysninger"},{"navn":"RVE","term":"Rettferdsvederlag"},{"navn":"SAA","term":"Sanksjon - Arbeidsgiver"},{"navn":"SAK","term":"Saksomkostning"},{"navn":"SAP","term":"Sanksjon - Person"},{"navn":"SER","term":"Serviceklager"},{"navn":"SIK","term":"Sikkerhetstiltak"},{"navn":"STO","term":"Regnskap/utbetaling"},{"navn":"SUP","term":"Supplerende stønad"},{"navn":"SYK","term":"Sykepenger"},{"navn":"SYM","term":"Sykemeldinger"},{"navn":"TIL","term":"Tiltak"},{"navn":"TRK","term":"Trekkhåndtering"},{"navn":"TRY","term":"Trygdeavgift"},{"navn":"TSO","term":"Tilleggsstønad"},{"navn":"TSR","term":"Tilleggsstønad arbeidssøkere"},{"navn":"UFM","term":"Unntak fra medlemskap"},{"navn":"UFO","term":"Uføretrygd"},{"navn":"UKJ","term":"Ukjent"},{"navn":"VEN","term":"Ventelønn"},{"navn":"YRA","term":"Yrkesrettet attføring"},{"navn":"YRK","term":"Yrkesskade / Menerstatning"}];

        return (
            <div id="home-container">
                <div id="home-container-header" className="dolly-header">
                    <h1 id="home-container-header-title">Mine testdatagrupper</h1>
                    <div id="home-container-header-button" data-flex data-layout="row" data-layout-align="end center">
                        <KnappBase type='standard'>
                            Opprett ny gruppe
                        </KnappBase>
                    </div>
                </div>

                <Table />

                <div id="first-row">
                    <div id="first-column">
                        <GruppeList grupper={grupper}/>

                        <InputDatePicker
                            id="test"
                            dateFormat="DD/MM/YYYY"
                            label="Date"
                            onChange={this.onChangeUpdateDate}
                            statePropToChange={this.state.date}
                        />
                    </div>

                    <div id="second-column">
                            <button onClick={this.showNyGruppeForm}>Legg til ny gruppe</button>
                            {this.state.showGruppeForm ?
                                <div>
                                    <NyGruppe />
                                </div>
                                : null
                            }
                    </div>

                    <div id="third-column">
                        <button onClick={this.showTeamForm}>Legg til nytt team</button>
                        {this.state.showTeamForm ?
                            <div>
                                <Team/>
                            </div>
                            : null
                        }
                    </div>
                </div>
                <div className="dolly-header">
                    <h1>Søk etter testdatagrupper</h1>
                    <InputAutocompleteField label={"Postnummer"}
                                            id={"postnummer-id"}
                                            onSelectedValue={this.onSelectedValue}
                                            kodeverk={kodeverk}
                    />
                </div>
            </div>
        )

    }
}

function mapStateToProps(stateFrom, ownProps){
    return {
        grupper: stateFrom.gruppeReducer
    };
}

function mapDispatchToProps(dispatch){
    return {
        gruppeActions: bindActionCreators(gruppeActions, dispatch)
    }
}

const function_connectReduxAndComponent = connect(mapStateToProps, mapDispatchToProps);

export default function_connectReduxAndComponent(Home);
