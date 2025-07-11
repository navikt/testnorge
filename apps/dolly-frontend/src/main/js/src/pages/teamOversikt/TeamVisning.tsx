import { formatBrukerNavn, formatDateTime } from '@/utils/DataFormatter'
import { LabelValueColumns } from '@/components/ui/labelValueColumns/LabelValueColumns'

export const TeamVisning = ({ team }) => {
	return (
		<>
			<LabelValueColumns label="Beskrivelse" value={team.beskrivelse} />
			<LabelValueColumns label="Opprettet" value={formatDateTime(team.opprettet)} />
			<LabelValueColumns
				label="Opprettet av"
				value={formatBrukerNavn(team.opprettetAv?.brukernavn)}
			/>
			<LabelValueColumns
				label="Medlemmer"
				value={team.brukere?.map((bruker) => formatBrukerNavn(bruker.brukernavn))}
			/>
		</>
	)
}
