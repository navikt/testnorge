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
	bestilling.tpsfStatus &&
		bestilling.tpsfStatus.map(status => {
			status.statusMelding !== 'OK' &&
				Object.keys(status.environmentIdents).map(miljo => {
					const lowMiljo = miljo.toLowerCase()
					!failedEnvs.includes(lowMiljo) && failedEnvs.push(lowMiljo)
				})
		})

	//Går gjennom TPSF-statuser igjen slik at ingen miljø er både suksess og feilet
	bestilling.tpsfStatus &&
		bestilling.tpsfStatus.map(status => {
			status.statusMelding == 'OK' &&
				Object.keys(status.environmentIdents).map(miljo => {
					const lowMiljo = miljo.toLowerCase()
					!failedEnvs.includes(lowMiljo) &&
						(!successEnvs.includes(lowMiljo) && successEnvs.push(lowMiljo))
				})
		})

	//Finn feilet og suksess miljø
	bestilling.krrStubStatus &&
		bestilling.krrStubStatus.map(status => {
			status.statusMelding == 'OK'
				? !successEnvs.includes('Krr-stub') && successEnvs.push('Krr-stub')
				: !failedEnvs.includes('Krr-stub') && failedEnvs.push('Krr-stub')
		})
	bestilling.sigrunStubStatus &&
		bestilling.sigrunStubStatus.map(status => {
			if (status.statusMelding == 'OK') {
				!successEnvs.includes('Sigrun-stub') && successEnvs.push('Sigrun-stub')
			} else {
				!failedEnvs.includes('Sigrun-stub') && failedEnvs.push('Sigrun-stub')
			}
		})
	bestilling.pdlforvalterStatus &&
		Object.keys(bestilling.pdlforvalterStatus).map(pdlAttr => {
			bestilling.pdlforvalterStatus[pdlAttr].map(status => {
				status.statusMelding === 'OK'
					? !successEnvs.includes('Pdl-forvalter') &&
					  !failedEnvs.includes('Pdl-forvalter') &&
					  successEnvs.push('Pdl-forvalter')
					: !failedEnvs.includes('Pdl-forvalter') && failedEnvs.push('Pdl-forvalter')
			})
		})

	let instHasOneSuccessEnv = false
	let instFailed = false

	bestilling.instdataStatus &&
		bestilling.instdataStatus.length > 0 &&
		bestilling.instdataStatus.map(status => {
			if (status.statusMelding == 'OK') {
				instHasOneSuccessEnv = true
			} else {
				instFailed = true
			}
		})

	if (bestilling.instdataStatus && bestilling.instdataStatus.length > 0) {
		instFailed
			? instHasOneSuccessEnv
				? avvikEnvs.push('Inst')
				: failedEnvs.push('Inst')
			: successEnvs.push('Inst')
	}

	let aaregHasOneSuccessEnv = false
	let aaregFailed = false
	bestilling.aaregStatus &&
		bestilling.aaregStatus.length > 0 &&
		bestilling.aaregStatus.map(status => {
			if (status.statusMelding == 'OK') {
				aaregHasOneSuccessEnv = true
			} else {
				aaregFailed = true
			}
		})

	if (bestilling.aaregStatus && bestilling.aaregStatus.length > 0) {
		aaregFailed
			? aaregHasOneSuccessEnv
				? avvikEnvs.push('AAREG')
				: failedEnvs.push('AAREG')
			: successEnvs.push('AAREG')
	}

	let arenaHasOneSuccessEnv = false
	let arenaFailed = false
	bestilling.arenaforvalterStatus &&
		bestilling.arenaforvalterStatus.length > 0 &&
		bestilling.arenaforvalterStatus.map(status => {
			if (status.status == 'OK') {
				arenaHasOneSuccessEnv = true
			} else {
				arenaFailed = true
			}
		})

	if (bestilling.arenaforvalterStatus && bestilling.arenaforvalterStatus.length > 0) {
		arenaFailed
			? arenaHasOneSuccessEnv
				? avvikEnvs.push('Arena')
				: failedEnvs.push('Arena')
			: successEnvs.push('Arena')
	}

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
