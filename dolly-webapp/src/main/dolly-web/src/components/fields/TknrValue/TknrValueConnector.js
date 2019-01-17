import { connect } from 'react-redux'
import TknrValue from './TknrValue'
import { fetchKodeverk, kodeverkLabelSelector } from '~/ducks/kodeverk'
import { getEnhetByTknr, tknrLabelSelector } from '~/ducks/tknr'

const mapStateToProps = (state, ownProps) => {
	const { tknr  } = ownProps
	return {
		tknrObject: tknrLabelSelector(state, tknr)
	}
}

const mapDispatchToProps = (dispatch, ownProps) => {
	const { tknr } = ownProps
	return {
		fetchTknr: () => dispatch(getEnhetByTknr(tknr))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(TknrValue)
