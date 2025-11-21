export type ArenaTypes = {
	arenaBrukertype: string
	automatiskInnsendingAvMeldekort: boolean
	kvalifiseringsgruppe: string
	aktiveringDato: string
	inaktiveringDato: string
	aap115: Array<AAP115>
	aap: Array<AAP>
	dagpenger: Array<Dagpenger>
}

export type AAP115 = {
	fraDato: string
	tilDato: string
}

export type AAP = {
	fraDato: string
	tilDato: string
}

export type Dagpenger = {
	rettighetKode: string
	fraDato: string
	tilDato: string
	mottattDato: string
}
