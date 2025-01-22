import { connect } from 'react-redux'
import FinnPersonBestilling from '@/pages/gruppeOversikt/FinnPersonBestilling'

const mapStateToProps = (state: {
	finnPerson: { feilmelding: string; navigerTilGruppe: number }
}) => ({
	feilmelding: state.finnPerson.feilmelding,
	gruppe: state.finnPerson.navigerTilGruppe,
})

export default connect(mapStateToProps)(FinnPersonBestilling)
