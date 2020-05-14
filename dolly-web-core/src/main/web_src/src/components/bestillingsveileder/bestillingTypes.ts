export type Bestillingsdata = {
	antall: number
	environments: Array<String>
	tpsf?: {
		egenAnsattDatoFom?: string
		spesreg?: string
	}
	// TODO: Lage types for alle fagsystemer
}
