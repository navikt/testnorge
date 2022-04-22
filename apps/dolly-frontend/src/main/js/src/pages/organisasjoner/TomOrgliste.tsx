import React from 'react'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import NavButton from '~/components/ui/button/NavButton/NavButton'

type Props = {
	startBestilling: (bestillingstype: string) => void
	bestillingstype: string
}

export default ({ startBestilling, bestillingstype }: Props) => (
	<ContentContainer>
		<p>
			Du har for øyeblikket ingen organisasjoner. Trykk på knappen under for å opprette en
			organisasjon med standard oppsett.
		</p>
		<NavButton
			type="standard"
			onClick={() => startBestilling(bestillingstype)}
			style={{ marginTop: '10px' }}
		>
			Opprett standard organisasjon
		</NavButton>
	</ContentContainer>
)
