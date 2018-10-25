import { connect } from 'react-redux'
import PersonDetaljer from './PersonDetaljer'
import DataMapper from '~/service/dataMapper'

const mapStateToProps = (state, ownProps) => ({
	personData: DataMapper.getDetailedData(state, ownProps)
})

export default connect(mapStateToProps)(PersonDetaljer)
