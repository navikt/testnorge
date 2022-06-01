// @ts-ignore
import { connect } from 'react-redux'
import Organisasjoner from './Organisasjoner'

const mapStateToProps = (state: any) => ({
	search: state.search,
	bestillinger: state.organisasjon.bestillinger,
	organisasjoner: state.organisasjon.organisasjoner,
	sidetall: state.finnPerson.sidetall,
})

export default connect(mapStateToProps)(Organisasjoner)
