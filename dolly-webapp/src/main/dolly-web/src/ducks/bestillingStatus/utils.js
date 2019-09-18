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
