import DataFormatter from '~/utils/DataFormatter'
import DataSourceMapper from '~/utils/DataSourceMapper'
import _groupBy from 'lodash/groupBy'
import _set from 'lodash/set'

// TODO: Kan getValues og transformAttributt merges?
export const getValues = (attributeList, values) => {
	return attributeList.reduce((accumulator, attribute) => {
		let value = _transformAttributt(attribute, attributeList, values[attribute.id])
		const pathPrefix = DataSourceMapper(attribute.dataSource)
		if (pathPrefix == DataSourceMapper('SIGRUN')) {
			const groupByTjeneste = _groupBy(value, 'tjeneste')
			let tjenester = Object.keys(groupByTjeneste)
			let dataArr = []
			tjenester.forEach(tjeneste => {
				const groupedByInntektsaar = _groupBy(groupByTjeneste[tjeneste], 'inntektsaar')
				const keys = Object.keys(groupedByInntektsaar)
				keys.forEach(key => {
					const current = groupedByInntektsaar[key]
					dataArr.push({
						grunnlag: current.map(temp => ({
							tekniskNavn: temp.typeinntekt,
							verdi: temp.beloep
						})),
						inntektsaar: key,
						tjeneste: tjeneste
					})
				})
			})

			return _set(accumulator, pathPrefix, dataArr)
		}

		if (pathPrefix === DataSourceMapper('AAREG')) {
			// TODO: Alex, construct a json-object before array
			let dataArr = []

			value.forEach(element => {
				let aaregObj = {}
				attribute.items.forEach(item => {
					const path = item.path
					const id = item.id
					let keyValue = element[id]

					Object.assign(aaregObj, { [path]: { ...aaregObj[path], [id]: keyValue } })
				})

				// aktorID = PERS
				// TODO: bytt heller inputfelte som ny attribute
				if (aaregObj.arbeidsgiver.aktoertype == 'PERS') {
					const aktoertype = aaregObj.arbeidsgiver.aktoertype
					const arbeidsgiverIdent = aaregObj.arbeidsgiver.ident
					Object.assign(aaregObj, {
						arbeidsgiver: { aktoertype, ident: arbeidsgiverIdent }
					})
				}

				// Spesifikk ekstra felter for AAREG, ikke-sett av UI
				Object.assign(aaregObj, {
					arbeidsforholdstype: 'ordinaertArbeidsforhold',
					arbeidsavtale: {
						...aaregObj.arbeidsavtale,
						arbeidstidsordning: 'ikkeSkift',
						avtaltArbeidstimerPerUke: 37.5
					}
				})
				dataArr.push(aaregObj)
			})
			return _set(accumulator, pathPrefix, dataArr)
		}

		if (pathPrefix === DataSourceMapper('KRR')) {
			return _set(accumulator, pathPrefix, value[0])
		}

		return _set(accumulator, `${pathPrefix}.${attribute.path || attribute.id}`, value)
	}, {})
}

// Transform attributes before order is sent
// Date, boolean...
const _transformAttributt = (attribute, attributes, value) => {
	// console.log('attribute, attributes, value :', attribute, attributes, value)
	if (attribute.dataSource === 'SIGRUN') {
		return value
	} else if (attribute.dataSource === 'AAREG') {
		let valueDeepCopy = JSON.parse(JSON.stringify(value))
		attribute.items.forEach(item => {
			if (item.inputType === 'date') {
				valueDeepCopy.forEach((element, i) => {
					const transformedElement = {
						...element,
						[item.id]: DataFormatter.parseDate(element[item.id])
					}

					valueDeepCopy[i] = transformedElement
				})
			}
		})

		return valueDeepCopy
	} else if (attribute.items) {
		// TODO: Single and multiple items

		let attributeList = attribute.items.reduce((res, acc) => ({ ...res, [acc.id]: acc }), {})

		if (attribute.isMultiple) {
			return value.map(val =>
				Object.assign(
					{},
					...Object.entries(val).map(([key, value]) => {
						if (!attributeList[key]) return
						let pathId = attributeList[key].path.split('.')
						return {
							//  Hente kun siste key, f.eks barn.kjønn => kjønn
							[pathId[pathId.length - 1]]: _transformAttributt(
								attributeList[key],
								attributes,
								value
							)
						}
					})
				)
			)
		} else {
			return value.map(val =>
				Object.assign(
					{},
					...Object.entries(val).map(([key, value]) => {
						return {
							[key]: _transformAttributt(attributeList[key], attributes, value)
						}
					})
				)
			)
		}
	} else if (attribute.transform) {
		// Only affects attributes that has property "transform: (value: any, attributter: Attributt[]) => any"
		value = attribute.transform(value, attributes)
	}
	if (attribute.inputType === 'date') {
		value = DataFormatter.parseDate(value)
	}

	return value
}

// Filter attributes included in filter argument
// Removes all children if attribute is a parent and dependencies
export const _filterAttributes = (values, filter, attribute, dependencies) => {
	return attribute
		.filter(
			attr =>
				!filter.includes(attr.id) &&
				!filter.includes(attr.parent) &&
				!dependencies[attr.id] &&
				(attr.items == null || values[attr.id] != null) // Values are not set before step 2
		)
		.map(attr => {
			return [
				attr.id,
				attr.items
					? values[attr.id].map(val => _filterAttributes(val, filter, attr.items, dependencies))
					: values[attr.id]
			]
		})
		.reduce((res, [key, val]) => ({ ...res, [key]: val }), {})
}

// Remove all values with ids in filter at index
// If last element in array is removed, then remove children and dependencies
export const _filterArrayAttributes = (values, selectedIds, filter, index) => {
	let copy = JSON.parse(JSON.stringify(values))
	let attributeIds = selectedIds.slice()
	let deletedIds = []
	attributeIds.filter(key => filter.includes(key)).forEach(key => {
		copy[key].splice(index, 1)
		if (copy[key].length == 0) {
			let ind = attributeIds.indexOf(key)
			deletedIds.push(attributeIds[ind])
			attributeIds.splice(ind, 1)
			delete copy[key]
		}
	})
	let dependencies = AttributtManagerInstance.listDependencies(deletedIds)
	return {
		values:
			Object.keys(dependencies).length > 0
				? _filterAttributes(
						copy,
						deletedIds,
						AttributtManagerInstance.listAllSelected(attributeIds),
						dependencies
				  )
				: copy,
		attributeIds: attributeIds.filter(attr => !dependencies[attr])
	}
}
