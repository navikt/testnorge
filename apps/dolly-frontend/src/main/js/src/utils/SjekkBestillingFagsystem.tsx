export const harAaregBestilling = (bestillingerFagsystemer) => {
	let aareg = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i.aareg) {
			aareg = true
		}
	})
	return aareg
}

export const harTpBestilling = (bestillingerFagsystemer) => {
	let tp = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i.pensjonforvalter?.tp?.length > 0) {
			tp = true
		}
	})
	return tp
}

export const harPoppBestilling = (bestillingerFagsystemer) => {
	let popp = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i.pensjonforvalter?.inntekt) {
			popp = true
		}
	})
	return popp
}

export const harInstBestilling = (bestillingerFagsystemer) => {
	let inst = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i.instdata) {
			inst = true
		}
	})
	return inst
}
