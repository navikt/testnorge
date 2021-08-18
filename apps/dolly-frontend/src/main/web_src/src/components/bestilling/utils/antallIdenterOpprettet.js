export default function antallIdenterOpprettet(bestilling) {
	const { antallIdenter, antallIdenterOpprettet } = bestilling
	const antallTekstPartial =
		antallIdenterOpprettet < 1 ? 'Ingen' : `${antallIdenterOpprettet} av ${antallIdenter}`
	const tekst = `${antallTekstPartial} bestilte identer ble opprettet i TPSF`

	return {
		tekst,
		harMangler: antallIdenterOpprettet < antallIdenter
	}
}
