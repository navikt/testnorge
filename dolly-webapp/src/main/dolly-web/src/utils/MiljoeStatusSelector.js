import _get from 'lodash/get'

export const avvikStatus = bestilling => {
	if (bestilling.feil) return true

	let avvik = false

	const check = v => v.statusMelding !== 'OK'

	if (_get(bestilling, 'tpsfStatus', []).some(check)) avvik = true
	if (_get(bestilling, 'aaregStatus', []).some(check)) avvik = true
	if (_get(bestilling, 'krrStubStatus', []).some(check)) avvik = true
	if (_get(bestilling, 'sigrunStubStatus', []).some(check)) avvik = true
	if (_get(bestilling, 'pdlforvalterStatus.pdlForvalter', []).some(check)) avvik = true
	if (_get(bestilling, 'instdataStatus', []).some(check)) avvik = true
	if (_get(bestilling, 'udiStubStatus', []).some(check)) avvik = true

	// Arena har et annerledes property - 'status'
	if (_get(bestilling, 'arenaforvalterStatus', []).some(o => o.status !== 'OK')) avvik = true

	return avvik
}

export const countAntallIdenterOpprettet = bestilling => {
	let identArray = []

	_get(bestilling, 'tpsfStatus', []).forEach(status => {
		Object.keys(status.environmentIdents).forEach(miljo => {
			status.environmentIdents[miljo].forEach(ident => {
				if (!identArray.includes(ident)) identArray.push(ident)
			})
		})
	})

	return identArray.length
}

const miljoeStatusSelector = bestilling => {
	if (!bestilling) return null

	const bestillingId = bestilling.id
	const successEnvs = []
	const failedEnvs = []
	const avvikEnvs = []
	const finnesFeilmelding = avvikStatus(bestilling)
	const antallIdenterOpprettet = countAntallIdenterOpprettet(bestilling)

	// TODO: Refactor, forenkler disse kodene
	const tpsf = _get(bestilling, 'tpsfStatus', [])
	tpsf.forEach(status => {
		if (status.statusMelding !== 'OK') {
			Object.keys(status.environmentIdents).forEach(miljo => {
				const lowMiljo = miljo.toLowerCase()
				if (!failedEnvs.includes(lowMiljo)) failedEnvs.push(lowMiljo)
			})
		}
	})

	// Går gjennom TPSF-statuser igjen slik at ingen miljø er både suksess og feilet
	// Burde kanskje legge til avvik slik som i arena og inst dersom et miljø feiler for noen, men ikke alle identer
	tpsf.forEach(status => {
		if (status.statusMelding === 'OK') {
			Object.keys(status.environmentIdents).forEach(miljo => {
				const lowMiljo = miljo.toLowerCase()
				if (!failedEnvs.includes(lowMiljo) && !successEnvs.includes(lowMiljo)) {
					successEnvs.push(lowMiljo)
				}
			})
		}
	})

	//Finn feilet og suksess miljø
	_get(bestilling, 'krrStubStatus', []).forEach(status => {
		status.statusMelding === 'OK' ? successEnvs.push('Krr-stub') : failedEnvs.push('Krr-stub')
	})
	_get(bestilling, 'sigrunStubStatus', []).forEach(status => {
		status.statusMelding === 'OK' ? successEnvs.push('Sigrun-stub') : failedEnvs.push('Sigrun-stub')
	})

	//Burde legge inn avvik hvis miljø både er success og fail
	bestilling.pdlforvalterStatus &&
		Object.keys(bestilling.pdlforvalterStatus).forEach(pdlAttr => {
			bestilling.pdlforvalterStatus[pdlAttr].forEach(status => {
				status.statusMelding === 'OK'
					? !successEnvs.includes('Pdl-forvalter') &&
					  !failedEnvs.includes('Pdl-forvalter') &&
					  successEnvs.push('Pdl-forvalter')
					: !failedEnvs.includes('Pdl-forvalter') && failedEnvs.push('Pdl-forvalter')
			})
		})

	const setStatus = (prop, statusPropName, name) => {
		let hasOneSuccessEnv = false
		let hasFailed = false
		const _node = _get(bestilling, prop, [])

		_node.forEach(status => {
			if (status[statusPropName] === 'OK') {
				hasOneSuccessEnv = true
			} else {
				hasFailed = true
			}
		})

		if (_node.length > 0) {
			hasFailed
				? hasOneSuccessEnv
					? avvikEnvs.push(name)
					: failedEnvs.push(name)
				: successEnvs.push(name)
		}
	}

	setStatus('instdataStatus', 'statusMelding', 'Inst')
	setStatus('aaregStatus', 'statusMelding', 'AAREG')
	setStatus('arenaforvalterStatus', 'status', 'Arena')

	bestilling.udiStubStatus &&
		bestilling.udiStubStatus.map(status => {
			status.statusMelding == 'OK'
				? !successEnvs.includes('Udi-stub') && successEnvs.push('Udi-stub')
				: !failedEnvs.includes('Udi-stub') && failedEnvs.push('Udi-stub')
		})

	return {
		bestillingId,
		successEnvs,
		failedEnvs,
		avvikEnvs,
		finnesFeilmelding,
		antallIdenterOpprettet
	}
}

export default miljoeStatusSelector
