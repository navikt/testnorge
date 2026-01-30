export type Ytelse = {
	type: string
	datoInnmeldtYtelseFom: string
	datoYtelseIverksattFom: string
	datoYtelseIverksattTom: string
}

export type TpTypes = {
	ordning: string
	ytelser: Array<Ytelse>
}
