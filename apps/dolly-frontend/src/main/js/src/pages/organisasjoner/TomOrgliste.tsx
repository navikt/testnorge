import ContentContainer from '@/components/ui/contentContainer/ContentContainer'
import NavButton from '@/components/ui/button/NavButton/NavButton'

type Props = {
	startBestilling: (bestillingstype: string) => void
	values: any
	bestillingstype: string
}

export default ({ startBestilling, values, bestillingstype }: Props) => (
	<ContentContainer>
		<p>
			Du har for øyeblikket ingen organisasjoner. Trykk på knappen under for å opprette en
			organisasjon med standard oppsett.
		</p>
		<NavButton
			onClick={() => startBestilling(values, bestillingstype)}
			style={{ marginTop: '10px' }}
		>
			Opprett standard organisasjon
		</NavButton>
	</ContentContainer>
)
