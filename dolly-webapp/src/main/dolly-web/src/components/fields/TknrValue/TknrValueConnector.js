import { connect } from 'react-redux'
import TknrValue from './TknrValue'
import { getEnhetByTknr, oppslagLabelSelector } from '~/ducks/oppslag'

const mapStateToProps = (state, ownProps) => {
	const { tknr } = ownProps
	return {
		tknrObject: oppslagLabelSelector(state, 'tknr', tknr)
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
