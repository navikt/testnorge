import { LOCATION_CHANGE } from 'connected-react-router'
import { handleActions, createActions, combineActions } from 'redux-actions'
import { DollyApi } from '~/service/Api'
import _xor from 'lodash/fp/xor'
import _get from 'lodash/get'
import _set from 'lodash/set'
import _union from 'lodash/union'
import _difference from 'lodash/difference'
import BestillingMapper from '~/utils/BestillingMapper'
import { onSuccess } from '~/ducks/utils/requestActions'
import { AttributtManager } from '~/service/Kodeverk'
import { getValues, _filterAttributes, _filterArrayAttributes } from './BestillingRequestUtils'
import Formatters from '~/utils/DataFormatter'

const AttributtManagerInstance = new AttributtManager()

export const actions = createActions(
	{
		POST_BESTILLING_FRA_EKSISTERENDE_IDENTER: (gruppeId, value) =>
			DollyApi.createBestillingFraEksisterendeIdenter(gruppeId, value),
		POST_BESTILLING: (gruppeId, values) => DollyApi.createBestilling(gruppeId, values),
		GET_BESTILLING_MALER: () => DollyApi.getBestillingMaler()
	},
	'NEXT_PAGE',
	'PREV_PAGE',
	'TOGGLE_ATTRIBUTE',
	'UNCHECK_ALL_ATTRIBUTES',
	'CHECK_ATTRIBUTE_ARRAY',
	'UNCHECK_ATTRIBUTE_ARRAY',
	'SET_ENVIRONMENTS',
	'SET_VALUES',
	'DELETE_VALUES',
	'DELETE_VALUES_ARRAY',
	'START_BESTILLING',
	'SET_IDENT_OPPRETTES_FRA',
	'SET_IDENT_LISTER',
	'SET_BESTILLING_FRA_MAL',
	'CREATE_BESTILLING_MAL',
	'ABORT_BESTILLING'
)

const initialState = {
	antall: 1,
	page: 0,
	attributeIds: [],
	environments: [],
	identtype: '',
	values: {},
	identOpprettesFra: BestillingMapper(),
	eksisterendeIdentListe: [],
	ugyldigIdentListe: [],
	maler: [],
	malBestillingNavn: '',
	currentMal: ''
}

// const initialState = {
// 	antall: 1,
// 	page: 1,
// 	attributeIds: ['foedtFoer', 'foedtEtter', 'kjonn'],
// 	environments: [],
// 	identtype: '',
// 	values: {},
// 	identOpprettesFra: BestillingMapper(),
// 	eksisterendeIdentListe: [],
// 	ugyldigIdentListe: [],
// 	maler: [],
// 	malBestillingNavn: '',
// 	currentMal: ''
// }

export default handleActions(
	{
		[actions.nextPage](state, action) {
			return { ...state, page: state.page + 1 }
		},
		[actions.prevPage](state, action) {
			return { ...state, page: state.page - 1 }
		},
		[actions.toggleAttribute](state, action) {
			let attributeIds = _xor(state.attributeIds, [action.payload])
			return {
				...state,
				values:
					attributeIds.length > state.attributeIds.length
						? state.values
						: _filterAttributes(
								state.values,
								[action.payload],
								AttributtManagerInstance.listAllSelected(state.attributeIds),
								AttributtManagerInstance.listDependencies([action.payload])
						  ),
				attributeIds: attributeIds
			}
		},
		[actions.uncheckAllAttributes](state, action) {
			return { ...state, attributeIds: [] }
		},
		[actions.checkAttributeArray](state, action) {
			return { ...state, attributeIds: _union(state.attributeIds, action.payload) }
		},
		[actions.uncheckAttributeArray](state, action) {
			return { ...state, attributeIds: _difference(state.attributeIds, action.payload) }
		},
		[actions.startBestilling](state, action) {
			return {
				...state,
				identtype: action.payload.identtype,
				antall: action.payload.antall,
				page: state.page + 1
			}
		},
		[onSuccess(actions.getBestillingMaler)](state, action) {
			return { ...state, maler: action.payload.data }
		},
		[actions.setEnvironments](state, action) {
			return {
				...state,
				environments: action.payload.values,
				...(action.payload.goBack && { page: state.page - 1 })
			}
		},
		[actions.setValues](state, action) {
			return {
				...state,
				values: action.payload.values,
				page: action.payload.keepPage ? state.page : (state.page += action.payload.goBack ? -1 : 1)
			}
		},
		[actions.deleteValues](state, action) {
			return {
				...state,
				values: _filterAttributes(
					state.values,
					action.payload.values,
					AttributtManagerInstance.listAllSelected(state.attributeIds),
					AttributtManagerInstance.listDependencies(action.payload.values)
				),
				attributeIds: state.attributeIds.filter(key => !action.payload.values.includes(key))
			}
		},
		[actions.deleteValuesArray](state, action) {
			return {
				...state,
				..._filterArrayAttributes(
					state.values,
					state.attributeIds,
					action.payload.values,
					action.payload.index
				)
			}
		},
		[actions.setIdentOpprettesFra](state, action) {
			return { ...state, identOpprettesFra: action.payload }
		},
		[actions.setIdentLister](state, action) {
			return {
				...state,
				eksisterendeIdentListe: action.payload.gyldigIdentListe,
				ugyldigIdentListe: action.payload.ugyldigIdentListe
			}
		},
		[actions.setBestillingFraMal](state, action) {
			return {
				...state,
				antall: action.payload.antallIdenter,
				attributeIds: action.payload.attributeIds,
				environments: action.payload.environments,
				identtype: action.payload.identtype,
				values: action.payload.values,
				currentMal: action.payload.currentMal
			}
		},
		[actions.createBestillingMal](state, action) {
			return {
				...state,
				malBestillingNavn: action.payload
			}
		},
		[combineActions(
			actions.abortBestilling,
			LOCATION_CHANGE,
			onSuccess(actions.postBestilling),
			onSuccess(actions.postBestillingFraEksisterendeIdenter)
		)](state, action) {
			return initialState
		}
	},
	initialState
)

// TODO: Denne må ryddes opp i og gjøres bedre
// - kanskje flyttes ut til egen fil (er jo bare en formatter og ikke thunk)
// - kan dette være mer generisk? bruke datasource nodene i AttributtManager?
// - CNN: LAGT TIL TPSF HARDKODET FOR NÅ FOR TESTING. FINN GENERISK LØSNING
// - AAL: Denne bør flyttes ut til egen fil
const bestillingFormatter = (bestillingState, oppslag) => {
	const {
		attributeIds,
		antall,
		environments,
		identtype,
		values,
		identOpprettesFra,
		eksisterendeIdentListe,
		malBestillingNavn
	} = bestillingState

	const AttributtListe = AttributtManagerInstance.listAllSelected(attributeIds)
	let final_values = []
	identOpprettesFra === BestillingMapper()
		? (final_values = {
				antall: antall,
				environments: environments,
				...getValues(AttributtListe, values)
		  })
		: (final_values = {
				opprettFraIdenter: eksisterendeIdentListe,
				environments: environments,
				...getValues(AttributtListe, values)
		  })

	// mandatory
	final_values = _set(final_values, 'tpsf.regdato', new Date())
	identOpprettesFra === BestillingMapper() && (final_values.tpsf.identtype = identtype)
	// ? Vi trenger ikke nødvendigvis generisk løsning når det er så veldig mange spesiall tilfeller
	// ? Forslag: lage en hjelpeklasse
	if (_get(final_values, 'tpsf.boadresse.gateadresse')) {
		final_values.tpsf.boadresse.adressetype = 'GATE'
		final_values.tpsf.boadresse.gatekode = values.boadresse_gatekode
	}
	if (_get(final_values, 'tpsf.matrikkeladresse')) {
		final_values = _set(final_values, 'tpsf.boadresse', final_values.tpsf.matrikkeladresse[0])
		final_values.tpsf.boadresse.adressetype = 'MATR'
		values.boadresse_flyttedato &&
			(final_values.tpsf.boadresse.flyttedato = Formatters.parseDate(values.boadresse_flyttedato))
		delete final_values.tpsf.matrikkeladresse
	}
	if (_get(final_values, 'tpsf.ufb_kommunenr')) {
		final_values = _set(final_values, 'tpsf.boadresse.adressetype', 'GATE')
		final_values = _set(final_values, 'tpsf.boadresse.kommunenr', values.ufb_kommunenr)
		delete final_values['tpsf']['ufb_kommunenr']
	}
	if (_get(final_values, 'tpsf.postadresse.postLand')) {
		final_values.tpsf.postadresse = [
			{
				postLand: values.postLand,
				postLinje1: values.postLinje1,
				postLinje2: values.postLinje2,
				postLinje3: values.postLinje3
			}
		]
	}

	if (_get(final_values, 'arenaforvalter')) {
		if (_get(final_values, 'arenaforvalter.arenaBrukertype') !== 'MED_SERVICEBEHOV') {
			final_values.arenaforvalter = {
				inaktiveringDato: final_values.arenaforvalter.inaktiveringDato,
				arenaBrukertype: 'UTEN_SERVICEBEHOV'
			}
		}
		if (_get(final_values, 'arenaforvalter.aap115') === true) {
			final_values.arenaforvalter.aap115 = [
				{
					fraDato: final_values.arenaforvalter.aap115_fraDato
				}
			]
		} else delete final_values.arenaforvalter.aap115

		if (_get(final_values, 'arenaforvalter.aap') === true) {
			final_values.arenaforvalter.aap = [
				{
					fraDato: final_values.arenaforvalter.aap_fraDato,
					tilDato: final_values.arenaforvalter.aap_tilDato
				}
			]
		} else delete final_values.arenaforvalter.aap
		delete final_values.arenaforvalter.aap115_fraDato
		delete final_values.arenaforvalter.aap_fraDato
		delete final_values.arenaforvalter.aap_tilDato
	}

	if (malBestillingNavn !== '') {
		final_values = _set(final_values, 'malBestillingNavn', malBestillingNavn)
	}

	if (_get(final_values, 'pdlforvalter.kontaktinformasjonForDoedsbo.postnummer')) {
		oppslag.Postnummer.koder.forEach(postnummer => {
			if (postnummer.value === final_values.pdlforvalter.kontaktinformasjonForDoedsbo.postnummer) {
				final_values = _set(
					final_values,
					'pdlforvalter.kontaktinformasjonForDoedsbo.poststedsnavn',
					postnummer.label
				)
			}
		})
	}

	return final_values
}

export const sendBestilling = gruppeId => async (dispatch, getState) => {
	const { currentBestilling, oppslag } = getState()
	const values = bestillingFormatter(currentBestilling, oppslag)
	if (currentBestilling.identOpprettesFra === BestillingMapper('EKSIDENT')) {
		return dispatch(actions.postBestillingFraEksisterendeIdenter(gruppeId, values))
	} else {
		return dispatch(actions.postBestilling(gruppeId, values))
	}
}
