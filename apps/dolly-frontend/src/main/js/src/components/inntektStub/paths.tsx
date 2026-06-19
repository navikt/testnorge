const tilleggsinformasjonPaths = {
	aaretUtbetalingenGjelderFor: 'tilleggsinformasjon.bonusFraForsvaret.aaretUtbetalingenGjelderFor',
	etterbetalingsperiodeStart: 'tilleggsinformasjon.etterbetalingsperiode.startdato',
	etterbetalingsperiodeSlutt: 'tilleggsinformasjon.etterbetalingsperiode.sluttdato',
	grunnpensjonsbeloep: 'tilleggsinformasjon.pensjon.grunnpensjonsbeloep',
	heravEtterlattepensjon: 'tilleggsinformasjon.pensjon.heravEtterlattepensjon',
	pensjonsgrad: 'tilleggsinformasjon.pensjon.pensjonsgrad',
	pensjonTidsromStart: 'tilleggsinformasjon.pensjon.tidsrom.startdato',
	pensjonTidsromSlutt: 'tilleggsinformasjon.pensjon.tidsrom.sluttdato',
	tilleggspensjonsbeloep: 'tilleggsinformasjon.pensjon.tilleggspensjonsbeloep',
	ufoeregrad: 'tilleggsinformasjon.pensjon.ufoeregrad',
	persontype: 'tilleggsinformasjon.reiseKostOgLosji.persontype',
	inntjeningsforhold: 'tilleggsinformasjon.inntjeningsforhold.inntjeningsforhold',
}

export default (key) => (tilleggsinformasjonPaths[key] ? tilleggsinformasjonPaths[key] : key)
