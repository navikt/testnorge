export type BeloepTypes = {
	fomDato: Date
	belop: string
}

export type MocksvarTypes = {
	tpId: string
	statusAfp: string
	virkningsDato: Date
	sistBenyttetG: number
	belopsListe: Array<BeloepTypes>
}

export type AfpOffentligTypes = {
	direktekall: Array<string>
	mocksvar: Array<MocksvarTypes>
}
