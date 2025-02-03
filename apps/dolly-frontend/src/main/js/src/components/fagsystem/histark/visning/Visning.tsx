import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import HistarkVisning from './HistarkVisning'
import Loading from '@/components/ui/loading/Loading'
import { Journalpost } from '@/service/services/JoarkDokumentService'

interface Form {
	data?: Array<MiljoDataListe>
	loading: boolean
}

type MiljoDataListe = {
	miljo: string
	data: Array<Journalpost>
}

const Histark = ({ data }) => {
	if (!data) return null

	return <HistarkVisning dokument={data} />
}

export default ({ data, loading }: Form) => {
	if (loading) {
		return <Loading label="Laster dokument-data" />
	}
	console.log('data: ', data) //TODO - SLETT MEG
	if (!data) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Dokumenter (Histark)" iconKind="dokarkiv" />
			<Histark data={data} />
		</>
	)
}
