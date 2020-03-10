import { connect } from 'react-redux'
import { getFnrFraFasteData, fetchFasteData } from '~/ducks/fasteData'
import { FasteDataWrapper } from './FasteData'

// Koble til FasteData
const mapStateToProps = (state, ownProps) => ({
    fasteData: getFnrFraFasteData(state)
})

const mapDispatchToProps = { fetchFasteData }

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(FasteDataWrapper)
