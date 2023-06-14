export const harAaregBestilling = (bestillingerFagsystemer) => {
	let aareg = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i.aareg) {
			aareg = true
		}
	})
	return aareg
}

export const harMedlBestilling = (bestillingerFagsystemer) => {
	let medl = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i.medl) {
			medl = true
		}
	})
	return medl
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

export const harApBestilling = (bestillingerFagsystemer) => {
	let alderspensjon = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i.pensjonforvalter?.alderspensjon) {
			alderspensjon = true
		}
	})
	return alderspensjon
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

export const harDokarkivBestilling = (bestillingerFagsystemer) => {
	let dokarkiv = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i.dokarkiv) {
			dokarkiv = true
		}
	})
	return dokarkiv
}

export const harHistarkBestilling = (bestillingerFagsystemer) => {
	let histark = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i.histark) {
			histark = true
		}
	})
	return histark
}

export const harArbeidsplassenBestilling = (bestillingerFagsystemer) => {
	let arbeidsplassen = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i.arbeidsplassenCV) {
			arbeidsplassen = true
		}
	})
	return arbeidsplassen
}
