import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Formatters from '@/utils/DataFormatter'

type HistarkDokument = {
	dokument: {
		antallSider: number
		enhetsNr: string
		enhetsNavn: string
		temaKodeSet: string[]
		fnrEllerOrgnr: string
		startaar: 2019
		sluttaar: 2023
		skanningstidspunkt: Date
		filnavn: string
		skanner: string
		skannerSted: string
		inneholderKlage: boolean
		sjekksum: string
	}
}

export default ({ dokument }: HistarkDokument) => (
	<div className="person-visning_content">
		<TitleValue title="Enhetsnummer" value={dokument.enhetsNr} />
		<TitleValue title="Enhetsnavn" value={dokument.enhetsNavn} />
		<TitleValue
			title="Temakoder"
			value={dokument?.temaKodeSet && Formatters.arrayToString(dokument?.temaKodeSet)}
		/>
		<TitleValue title="Startår" value={dokument.startaar} />
		<TitleValue title="Sluttår" value={dokument.sluttaar} />
		<TitleValue
			title="Skanningstidspunkt"
			value={dokument?.skanningstidspunkt && Formatters.formatDate(dokument.skanningstidspunkt)}
		/>
		<TitleValue title="Filnavn" value={dokument.filnavn} />
		<TitleValue title="Skanner" value={dokument.skanner} />
		<TitleValue title="Skannested" value={dokument.skannerSted} />
	</div>
)
