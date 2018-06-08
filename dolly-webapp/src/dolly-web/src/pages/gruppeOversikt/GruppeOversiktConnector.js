import { connect } from 'react-redux'
import GruppeOversikt from './GruppeOversikt'

const mapStateToProps = state => ({
	grupper: state.grupper
})

const mapDispatchToProps = dispatch => ({})

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(GruppeOversikt)
