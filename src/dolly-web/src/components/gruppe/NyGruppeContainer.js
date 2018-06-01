import React, { PureComponent } from 'react'
import NyGruppeForm from './NyGruppeForm'
import axios from 'axios'
import ContentApi from '../../ContentApi'
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import { createGruppeSuccess } from '~/ducks/grupper'

export class NyGruppe extends PureComponent {
	onClickSave = gruppe => {
		let gruppe_obj = Object.assign({}, gruppe)
		this.opprettGruppe(gruppe_obj).catch(error => {
			console.log(error)
		})
	}

	opprettGruppe = async gruppe => {
		const response = await axios.post(ContentApi.postGrupper(), gruppe)
		this.props.createGruppeSuccess(response.data)
	}

	render() {
		return <NyGruppeForm onClickSave={this.onClickSave} onChangeInput={this.onInputChange} />
	}
}

// Todo: Flyttes til connector
const mapDispatchToProps = dispatch => ({
	createGruppeSuccess: res => dispatch(createGruppeSuccess(res))
})

export default connect(
	null,
	mapDispatchToProps
)(NyGruppe)
