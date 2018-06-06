import React, { Component } from 'react'
import axios from 'axios'
import PersonListe from './PersonListe'
import ContentApi from '../../ContentApi'

export default class Gruppe extends Component {
	constructor(props) {
		super(props)

		const gruppeId = parseInt(props.match.params.gruppeId, 10) //TODO når bruker ekte API så vil id være samme type, kan da fjerne denne
		let gruppe = this.props.grupperState.find(gruppe => gruppe.id === gruppeId)

		this.state = {
			personer: [],
			initialPersoner: [],
			gruppe: gruppe
		}
	}

	//TODO Skal hente personer basert på testidenter i gruppen etter hvert.
	componentDidMount() {
		this.fetchPersonsInGroup().catch(err => {
			console.log(err)
		})
	}

	fetchPersonsInGroup = async () => {
		const response = await axios.get(ContentApi.getPersons())
		this.setState({ personer: response.data, initialPersoner: response.date })
	}

	saveChangesInGroup = async () => {
		testperson.gruppeId = this.state.gruppe.id
		const response = await axios.post(ContentApi.postPersons(), testperson)

		this.setState({
			personer: [...this.state.personer, Object.assign({}, response.data)]
		})
	}

	addIdentToGroup = async ident => {
		let oppdatertGruppe = Object.assign({}, this.state.gruppe)
		oppdatertGruppe.navn = 'nyttnavn'

		const response = await axios.put(ContentApi.putGruppe(oppdatertGruppe.id), oppdatertGruppe)
		this.props.updateGruppeSuccess(response.data)

		this.setState({
			gruppe: this.props.grupperState.find(gruppe => gruppe.id === oppdatertGruppe.id)
		})
	}

	onClicker = () => {
		this.addIdentToGroup('bla').catch(err => {
			console.log(err)
		})
	}

	onClickerCreate = () => {
		this.saveChangesInGroup().catch(err => {
			console.log(err)
		})
	}

	render() {
		return (
			<div id="gruppe-container">
				<h1>Gruppe Container</h1>
				<PersonListe personer={this.state.personer} />
				<button onClick={this.onClicker}>Update gruppe</button>
				<button onClick={this.onClickerCreate}>Create person</button>
			</div>
		)
	}
}

const testperson = {
	id: 100,
	fnr: '12345678987',
	kjonn: 'k',
	fornavn: 'testperson',
	etternavn: 'ettertest',
	adresse: 'finAdresse',
	gruppeId: ''
}
