import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import HistarkVisning from './HistarkVisning'
import Loading from '@/components/ui/loading/Loading'
import { Journalpost } from '@/service/services/JoarkDokumentService'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'

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

	if (!data) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Dokumenter (Histark)" iconKind="dokarkiv" />
			{data.length === 1 ? (
				<Histark data={data[0]} />
			) : (
				<DollyFieldArray header={'Dokument'} data={data} expandable={data?.length > 2}>
					{(dokument) => {
						return <Histark data={dokument} />
					}}
				</DollyFieldArray>
			)}
		</>
	)
}
