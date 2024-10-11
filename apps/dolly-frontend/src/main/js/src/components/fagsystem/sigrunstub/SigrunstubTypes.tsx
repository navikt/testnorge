export type SigrunstubData = {
	inntektsaar: number
	tjeneste: string
	grunnlag: Array<Grunnlag>
	svalbardGrunnlag: Array<Grunnlag>
}

export type Grunnlag = {
	tekniskNavn: string
	verdi: string
}
