import _memoize from 'lodash/memoize'
import _mapValues from 'lodash/mapValues'
import _isNil from 'lodash/isNil'
import _get from 'lodash/get'
import { PANELER, KATEGORIER, ATTRIBUTTER } from './attributtVerdier'

export const getAttributter = () => ATTRIBUTTER

// InitialValues er alle deselected
export const getInitialValues = _memoize(() => _mapValues(groupByValue(), v => false))

export const getAttributterSortert = _memoize(() => {
	const attrs = getAttributter()
	return groupPaneler(attrs).map(v => ({
		...v,
		values: groupKategori(v.values)
	}))
})

export const getAttributterForUtvalgListe = (valgte = {}) => {
	const valgteAttrs = getAttributter().filter(v => valgte[v.name])
	return groupPaneler(valgteAttrs)
}

const createPathAttrs = _memoize(() => ({
	panel: _mapValues(PANELER, (value, key) => {
		return getAttributter()
			.filter(a => a.panel.id === key)
			.map(m => m.path)
	}),
	kategori: _mapValues(KATEGORIER, (value, key) => {
		return getAttributter()
			.filter(a => a.kategori.id === key)
			.map(m => m.path)
	})
}))

export const pathAttrs = createPathAttrs()

const groupByValue = _memoize(() => {
	return getAttributter().reduce((acc, curr) => {
		acc[curr.name] = { ...curr }
		return acc
	}, {})
})

const groupPaneler = attrs => group(attrs, PANELER, 'panel')
const groupKategori = attrs => group(attrs, KATEGORIER, 'kategori')
const group = (attrs, source, key) =>
	Object.values(source)
		.map(obj => ({
			[key]: obj,
			values: attrs.filter(f => f[key].id === obj.id)
		}))
		.filter(f => Boolean(f.values.length))
