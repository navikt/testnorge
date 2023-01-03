import { connect, RootStateOrAny } from 'react-redux'
import { PdlfVisning } from '@/components/fagsystem'

const mapStateToProps = (state: RootStateOrAny) => {
	return {
		tmpPersoner: state?.redigertePersoner,
	}
}

export default connect(mapStateToProps)(PdlfVisning)
