import { connect } from 'react-redux'
import { push } from 'connected-react-router'
import SearchView from '~/pages/testnorgePage/search/SearchView'

const mapStateToProps = (state: any, ownProps: any) => ({
	items: ownProps.items,
	valgtePersoner: ownProps.valgtePersoner,
	setValgtePersoner: ownProps.setValgtePersoner,
	loading: ownProps.loading,
})

const mapDispatchToProps = (dispatch: any) => ({
	importerPersoner: (valgtePersoner: string[]) => {
		return dispatch(
			push(`/importer`, {
				importPersoner: valgtePersoner,
			})
		)
	},
})

export default connect(mapStateToProps, mapDispatchToProps)(SearchView)
