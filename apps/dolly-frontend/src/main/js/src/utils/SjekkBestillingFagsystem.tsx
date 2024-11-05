export const harAaregBestilling = (bestillingerFagsystemer) => {
	let aareg = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.aareg) {
			aareg = true
		}
	})
	return aareg
}

export const harSkattekortBestilling = (bestillingerFagsystemer) => {
	let skattekort = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.skattekort) {
			skattekort = true
		}
	})
	return skattekort
}

export const harMedlBestilling = (bestillingerFagsystemer) => {
	let medl = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.medl) {
			medl = true
		}
	})
	return medl
}

export const harUdistubBestilling = (bestillingerFagsystemer) => {
	let udistub = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.udistub) {
			udistub = true
		}
	})
	return udistub
}

export const harTpBestilling = (bestillingerFagsystemer) => {
	let tp = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.pensjonforvalter?.tp?.length > 0) {
			tp = true
		}
	})
	return tp
}

export const harPoppBestilling = (bestillingerFagsystemer) => {
	let popp = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.pensjonforvalter?.inntekt || i?.pensjonforvalter?.generertInntekt) {
			popp = true
		}
	})
	return popp
}

export const harApBestilling = (bestillingerFagsystemer) => {
	let alderspensjon = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.pensjonforvalter?.alderspensjon) {
			alderspensjon = true
		}
	})
	return alderspensjon
}

export const harPensjonavtaleBestilling = (bestillingerFagsystemer) => {
	let pensjonavtale = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.pensjonforvalter?.pensjonsavtale) {
			pensjonavtale = true
		}
	})
	return pensjonavtale
}

export const harUforetrygdBestilling = (bestillingerFagsystemer) => {
	let uforetrygd = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.pensjonforvalter?.uforetrygd) {
			uforetrygd = true
		}
	})
	return uforetrygd
}

export const harAfpOffentligBestilling = (bestillingerFagsystemer) => {
	let afpOffentlig = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.pensjonforvalter?.afpOffentlig) {
			afpOffentlig = true
		}
	})
	return afpOffentlig
}

export const harInstBestilling = (bestillingerFagsystemer) => {
	let inst = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.instdata) {
			inst = true
		}
	})
	return inst
}

export const harDokarkivBestilling = (bestillingerFagsystemer) => {
	let dokarkiv = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.dokarkiv) {
			dokarkiv = true
		}
	})
	return dokarkiv
}

export const harHistarkBestilling = (bestillingerFagsystemer) => {
	let histark = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.histark) {
			histark = true
		}
	})
	return histark
}

export const harArbeidsplassenBestilling = (bestillingerFagsystemer) => {
	let arbeidsplassen = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.arbeidsplassenCV) {
			arbeidsplassen = true
		}
	})
	return arbeidsplassen
}

export const harArenaBestilling = (bestillingerFagsystemer) => {
	let arena = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.arenaforvalter) {
			arena = true
		}
	})
	return arena
}

export const harSykemeldingBestilling = (bestillingerFagsystemer) => {
	let sykemelding = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.sykemelding) {
			sykemelding = true
		}
	})
	return sykemelding
}

export const harYrkesskaderBestilling = (bestillingerFagsystemer) => {
	let yrkesskader = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.yrkesskader) {
			yrkesskader = true
		}
	})
	return yrkesskader
}

export const harInntektsmeldingBestilling = (bestillingerFagsystemer) => {
	let inntektsmelding = false
	bestillingerFagsystemer?.forEach((i) => {
		if (i?.inntektsmelding) {
			inntektsmelding = true
		}
	})
	return inntektsmelding
}
