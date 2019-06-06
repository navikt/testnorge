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
			item.pdlforvalterStatus[pdlAttr].map(status => {
				status.statusMelding === 'OK' && (avvik = true)
				//ingvild
			})
		})
	item.feil && (avvik = true)
	return avvik
}

const antallIdenterOpprettetFunk = bestilling => {
	let identArray = []
	bestilling.tpsfStatus &&
		bestilling.tpsfStatus.map(status => {
			Object.keys(status.environmentIdents).map(miljo => {
				status.environmentIdents[miljo].map(ident => {
					!identArray.includes(ident) && identArray.push(ident)
				})
			})
		})
	return identArray.length
}

const miljoeStatusSelector = bestilling => {
	if (!bestilling) return null

	const bestillingId = bestilling.id
	let successEnvs = []
	let failedEnvs = []
	let avvikEnvs = []
	const finnesFeilmelding = avvikStatus(bestilling)
	const antallIdenterOpprettet = antallIdenterOpprettetFunk(bestilling)

	// TODO: Kan disse 2 loops forenklet?
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
	// bestilling.pdlforvalterStatus &&
	// 	bestilling.pdlforvalterStatus.map(status => {
	// 		status.statusMelding === 'OK'
	// 			? !successEnvs.includes('Pdl-forvalter') && successEnvs.push('Pdl-forvalter')
	// 			: !failedEnvs.includes('Pdl-forvalter') && failedEnvs.push('Pdl-forvalter')
	// 	})

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
