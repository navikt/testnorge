import { runningE2ETest } from '@/service/services/Request'

export const initialBeloep = {
	fomDato: runningE2ETest() ? new Date() : null,
	belop: 0,
}

export const initialMocksvar = {
	tpId: null,
	statusAfp: null,
	virkningsDato: null,
	sistBenyttetG: new Date().getFullYear(),
	belopsListe: [initialBeloep],
}

export const initialAfpOffentlig = {
	direktekall: [],
	mocksvar: [initialMocksvar],
}
