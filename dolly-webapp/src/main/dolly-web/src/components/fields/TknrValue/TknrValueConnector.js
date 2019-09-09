import { connect } from 'react-redux'
import TknrValue from './TknrValue'
import { getEnhetByTknr, oppslagLabelSelector } from '~/ducks/oppslag'

const mapStateToProps = (state, ownProps) => ({
	tknrObject: oppslagLabelSelector(state, 'tknr', ownProps.tknr)
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	fetchTknr: () => dispatch(getEnhetByTknr(ownProps.tknr))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(TknrValue)
