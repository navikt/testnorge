const texts = {
	inntektstype: 'Inntektstype',
	LOENNSINNTEKT: 'Lønnsinntekt',
	YTELSE_FRA_OFFENTLIGE: 'Ytelse fra offentige'
}

export default key => (texts[key] ? texts[key] : key)
