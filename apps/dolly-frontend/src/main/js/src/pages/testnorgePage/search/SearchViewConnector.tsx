import { connect } from 'react-redux'
import SearchView, { ImportPerson } from '~/pages/testnorgePage/search/SearchView'

const mapStateToProps = (state: any, ownProps: any) => ({
	items: ownProps.items,
	valgtePersoner: ownProps.valgtePersoner,
	setValgtePersoner: ownProps.setValgtePersoner,
	loading: ownProps.loading,
})

const mapDispatchToProps = () => ({
	importerPersoner: (valgtePersoner: ImportPerson[], navigate: Function) => {
		return navigate(`/importer`, {
			state: {
				importPersoner: valgtePersoner,
			},
		})
	},
})

export default connect(mapStateToProps, mapDispatchToProps)(SearchView)
