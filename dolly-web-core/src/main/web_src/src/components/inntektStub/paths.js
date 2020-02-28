const tilleggsinformasjonPaths = {
	aaretUtbetalingenGjelderFor: 'tilleggsinformasjon.bonusFraForsvaret.aaretUtbetalingenGjelderFor', // Lønnsinntekt
	etterbetalingsperiodeStart: 'tilleggsinformasjon.etterbetalingsperiode.startdato', // Ytelse fra offentlige + Pensjon eller trygd
	etterbetalingsperiodeSlutt: 'tilleggsinformasjon.etterbetalingsperiode.sluttdato', // Ytelse fra offentlige + Pensjon eller trygd
	grunnpensjonsbeloep: 'tilleggsinformasjon.pensjon.grunnpensjonsbeloep', // Ytelse fra offentlige + Pensjon eller trygd
	heravEtterlattepensjon: 'tilleggsinformasjon.pensjon.heravEtterlattepensjon', // Ytelse fra offentlige + Pensjon eller trygd
	pensjonsgrad: 'tilleggsinformasjon.pensjon.pensjonsgrad', // Ytelse fra offentlige + Pensjon eller trygd
	pensjonTidsromStart: 'tilleggsinformasjon.pensjon.tidsrom.startdato', // Ytelse fra offentlige + Pensjon eller trygd
	pensjonTidsromSlutt: 'tilleggsinformasjon.pensjon.tidsrom.sluttdato', // Ytelse fra offentlige + Pensjon eller trygd
	tilleggspensjonsbeloep: 'tilleggsinformasjon.pensjon.tilleggspensjonsbeloep', // Ytelse fra offentlige + Pensjon eller trygd
	ufoeregrad: 'tilleggsinformasjon.pensjon.ufoeregrad', // Ytelse fra offentlige + Pensjon eller trygd
	persontype: 'tilleggsinformasjon.reiseKostOgLosji.persontype', // Lønnsinntekt
	inntjeningsforhold: 'tilleggsinformasjon.inntjeningsforhold.inntjeningsforhold' // Lønnsinntekt
}

export default key => (tilleggsinformasjonPaths[key] ? tilleggsinformasjonPaths[key] : key)
