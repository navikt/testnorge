// @ts-ignore
import { connect } from 'react-redux'
import Organisasjoner from './Organisasjoner'

const mapStateToProps = (state: any) => ({
	search: state.search,
	sidetall: state.finnPerson.sidetall,
})

export default connect(mapStateToProps)(Organisasjoner)
