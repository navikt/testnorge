import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'
import { push } from 'connected-react-router'
import SearchView from '~/pages/testnorgePage/search/SearchView'

const getPageing = (pageSize: number, page: number) => {
	return {
		pageSize: pageSize,
		page: page,
	}
}

const mapStateToProps = (state: any, ownProps: any) => ({
	items: ownProps.items,
	valgtePersoner: ownProps.valgtePersoner,
	setValgtePersoner: ownProps.setValgtePersoner,
	loading: ownProps.loading,
	pageing: getPageing(ownProps.pageSize, ownProps.page),
	numberOfItems: ownProps.numberOfItems,
	onChange: ownProps.onChange,
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

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(SearchView))
