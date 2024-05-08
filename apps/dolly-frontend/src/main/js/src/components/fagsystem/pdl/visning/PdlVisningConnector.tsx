import { PdlVisning } from '@/components/fagsystem/pdl/visning/PdlVisning'
import { connect } from 'react-redux'

const mapStateToProps = (state: any) => {
	return {
		tmpPersoner: state?.redigertePersoner,
	}
}

export default connect(mapStateToProps)(PdlVisning)
