import { connect } from 'react-redux'
import { actions } from '~/ducks/bestilling'
import FalskIdentitet from './ArrayField'
import { getKodeverk, oppslagLabelSelector } from '../../ducks/oppslag'

const mapStateToProps = (state, ownProps) => {
	let liste = []

	// Burde skrives om generisk!
	ownProps.formikProps.values.falskIdentitet[0].statsborgerskap &&
		ownProps.formikProps.values.falskIdentitet[0].statsborgerskap.map(stat => {
			state.oppslag[ownProps.item.apiKodeverkId] &&
				state.oppslag[ownProps.item.apiKodeverkId].koder.map(statObj => {
					stat === statObj.value && liste.push(statObj)
				})
		})
	return {
		attributeIds: state.currentBestilling.attributeIds,
		values: state.currentBestilling.values,
		valgteStatsborgerskap: ownProps.formikProps.values.falskIdentitet[0].statsborgerskap, // Ikke generisk
		kodeverkObjekt: state.oppslag[ownProps.item.apiKodeverkId],
		fieldListe: liste // Ikke generisk
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		fetchKodeverk: async () => await dispatch(getKodeverk(ownProps.item.apiKodeverkId))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(FalskIdentitet)
