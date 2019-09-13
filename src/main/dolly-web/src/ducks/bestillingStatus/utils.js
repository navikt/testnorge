import Formatters from '~/utils/DataFormatter'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'

export const getSuccessEnv = statusArray => {
	let envs = []
	if (!statusArray) return envs

	statusArray.length > 0 &&
		statusArray.forEach(status => {
			if (status.statusMelding === 'OK') {
				envs = Object.keys(status.environmentIdentsForhold)
			}
		})

	return envs
}

export const getPdlforvalterStatusOK = pdlforvalterStatus => {
	let totalStatus = false
	Object.keys(pdlforvalterStatus).map(pdlAttr => {
		pdlforvalterStatus[pdlAttr].map(status => {
			if (status.statusMelding === 'OK') {
				totalStatus = true
			}
		})
	})
	return totalStatus
}

export const sokSelector = (items, searchStr) => {
	if (!items) return null
	const mappedItems = mapItems(items)

	if (!searchStr) return mappedItems

	const query = searchStr.toLowerCase()
	return mappedItems.filter(item => {
		const searchValues = [
			_get(item, 'id'),
			_get(item, 'antallIdenter'),
			_get(item, 'sistOppdatert'),
			_get(item, 'environments'),
			_get(item, 'ferdig')
		]
			.filter(v => !_isNil(v))
			.map(v => v.toString().toLowerCase())

		return searchValues.some(v => v.includes(query))
	})
}

const mapItems = items => {
	if (!items) return null
	return items.map(item => {
		return {
			...item,
			id: item.id.toString(),
			antallIdenter: item.antallIdenter.toString(),
			sistOppdatert: Formatters.formatDate(item.sistOppdatert),
			ferdig: item.stoppet
				? 'Stoppet'
				: bestillingIkkeFerdig(item)
					? 'Pågår'
					: harIkkeIdenter(item)
						? 'Feilet'
						: avvikStatus(item)
							? 'Avvik'
							: 'Ferdig'
		}
	})
}
const bestillingIkkeFerdig = item => !item.ferdig

const avvikStatus = item => {
	let avvik = false
	item.tpsfStatus &&
		item.tpsfStatus.map(status => {
			status.statusMelding !== 'OK' && (avvik = true)
		})
	item.aaregStatus &&
		item.aaregStatus.map(status => {
			status.statusMelding !== 'OK' && (avvik = true)
		})
	item.krrStubStatus &&
		item.krrStubStatus.map(status => {
			status.statusMelding !== 'OK' && (avvik = true)
		})
	item.sigrunStubStatus &&
		item.sigrunStubStatus.map(status => {
			status.statusMelding !== 'OK' && (avvik = true)
		})
	item.pdlforvalterStatus &&
		Object.keys(item.pdlforvalterStatus).map(pdlAttr => {
			// Looper gjennom pdl-attributter. F.eks. generell pdl, dødsbo, utenlandsid
			item.pdlforvalterStatus[pdlAttr].map(status => {
				status.statusMelding !== 'OK' && (avvik = true)
			})
		})

	item.arenaforvalterStatus &&
		item.arenaforvalterStatus.map(status => {
			status.status !== 'OK' && (avvik = true)
		})

	item.instdataStatus &&
		item.instdataStatus.map(status => {
			status.statusMelding !== 'OK' && (avvik = true)
		})

	item.udiStubStatus &&
		item.udiStubStatus.map(status => {
			status.statusMelding !== 'OK' && (avvik = true)
		})

	item.feil && (avvik = true)
	return avvik
}

const harIkkeIdenter = item => {
	let feilet = true
	item.tpsfStatus && (feilet = false)
	return feilet
}
