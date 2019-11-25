import { connect } from 'react-redux'
import PersonIBrukButton from './PersonIBrukButton'
import { updateIdentAttributter } from '~/ducks/gruppe'

const mapStateToProps = (state, ownProps) => ({
	erIBruk: state.gruppe.data[0].identer.find(v => v.ident === ownProps.personId).ibruk,
	Id: state.gruppe.data[0].id
})

const mapDispatchToProps = (dispatch, ownProps) => ({
	updateIdentAttributter: (id, values) => dispatch(updateIdentAttributter(id, values))
})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(PersonIBrukButton)
