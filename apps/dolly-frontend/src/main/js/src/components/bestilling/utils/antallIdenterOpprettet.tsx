export default function antallIdenterOpprettet(bestilling) {
	const { antallIdenter, antallIdenterOpprettet } = bestilling
	const antallTekstPartial =
		antallIdenterOpprettet < 1 ? 'Ingen' : `${antallIdenterOpprettet} av ${antallIdenter}`
	const tekst = antallIdenterOpprettet ? `${antallTekstPartial} bestilte identer ble opprettet` : ''

	return {
		tekst,
		harMangler: antallIdenterOpprettet < antallIdenter,
	}
}
