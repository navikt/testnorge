export type Bestillingsdata = {
	antall: number
	environments: Array<string>
	beskrivelse: string
	tpsf?: {
		egenAnsattDatoFom?: string
		egenAnsattDatoTom?: string
		spesreg?: string
		relasjoner?: Relasjoner
	}
	pensjonforvalter?: any
	// TODO: Lage types for alle fagsystemer
}

export type Relasjoner = {
	barn?: Array<Barn>
	partnere?: Array<Partnere>
	foreldre?: Array<Foreldre>
}

export type Barn = {
	spesreg?: string
}

export type Partnere = {
	spesreg?: string
}

export type Foreldre = {
	spesreg?: string
}
