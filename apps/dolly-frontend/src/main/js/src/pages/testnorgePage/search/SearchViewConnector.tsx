import { connect } from 'react-redux'
import SearchView, { ImportPerson } from '~/pages/testnorgePage/search/SearchView'
import { Gruppe } from '~/utils/hooks/useGruppe'

const mapStateToProps = (state: any, ownProps: any) => ({
	items: ownProps.items,
	valgtePersoner: ownProps.valgtePersoner,
	setValgtePersoner: ownProps.setValgtePersoner,
	loading: ownProps.loading,
	sidetall: state.finnPerson.sidetall,
	gruppe: ownProps.gruppe,
})

const mapDispatchToProps = () => ({
	importerPersoner: (
		valgtePersoner: ImportPerson[],
		mal: any,
		navigate: Function,
		gruppe?: Gruppe
	) => {
		return navigate(`/importer`, {
			state: {
				importPersoner: valgtePersoner,
				mal: mal,
				gruppe: gruppe,
			},
		})
	},
})

export default connect(mapStateToProps, mapDispatchToProps)(SearchView)
