import { connect } from 'react-redux'
import BrukerPage from '~/pages/brukerPage/BrukerPage'

const mapStateToProps = (state: any, ownProps: any) => ({
	brukerData: state.bruker.brukerData,
})

export default connect(mapStateToProps)(BrukerPage)
