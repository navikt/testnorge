import { connect } from 'react-redux'
import SearchView, { ImportPerson } from '~/pages/testnorgePage/search/SearchView'

const mapStateToProps = (state: any, ownProps: any) => ({
	items: ownProps.items,
	valgtePersoner: ownProps.valgtePersoner,
	setValgtePersoner: ownProps.setValgtePersoner,
	loading: ownProps.loading,
	sidetall: state.finnPerson.sidetall,
	gruppeId: ownProps.gruppeId,
})

const mapDispatchToProps = () => ({
	importerPersoner: (
		valgtePersoner: ImportPerson[],
		mal: any,
		navigate: Function,
		gruppeId?: number
	) => {
		return navigate(`/importer`, {
			state: {
				importPersoner: valgtePersoner,
				mal: mal,
				gruppe: gruppeId,
			},
		})
	},
})

export default connect(mapStateToProps, mapDispatchToProps)(SearchView)
