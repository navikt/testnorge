import { connect, RootStateOrAny } from 'react-redux'
import { Action } from 'redux-actions'
import { actions } from '~/ducks/gruppe'
import { createLoadingSelector } from '~/ducks/loading'
import OrganisasjonHeader from '~/pages/organisasjoner/OrgansisasjonHeader/OrganisasjonHeader'

const loadingSelectorGetExcel = createLoadingSelector(actions.getOrgExcelFil)

const mapStateToProps = (state: RootStateOrAny) => ({
	isFetchingExcel: loadingSelectorGetExcel(state),
})

const mapDispatchToProps = (dispatch: (arg0: Action<any>) => any) => ({
	getOrgExcelFil: (brukerId: number) => dispatch(actions.getOrgExcelFil(brukerId)),
})

export default connect(mapStateToProps, mapDispatchToProps)(OrganisasjonHeader)
