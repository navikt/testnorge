import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import HistarkVisning from './HistarkVisning'
import Loading from '@/components/ui/loading/Loading'
import { Journalpost } from '@/service/services/JoarkDokumentService'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { useTransaksjonsid } from '@/utils/hooks/useTransaksjonsid'

interface Form {
	data?: Array<MiljoDataListe>
	ident: string
	loading: boolean
}

type MiljoDataListe = {
	miljo: string
	data: Array<Journalpost>
}

const Histark = ({ data, transaksjon }) => {
	if (!data) return null

	return <HistarkVisning dokument={data} transaksjon={transaksjon} />
}

export default ({ data, loading, ident }: Form) => {
	const { transaksjonsid, loading: loadingTransaksjon } = useTransaksjonsid('HISTARK', ident)
	const transaksjoner = transaksjonsid?.[0]?.transaksjonId
	if (loading || loadingTransaksjon) {
		return <Loading label="Laster dokument-data" />
	}

	if (!data) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Dokumenter (Histark)" iconKind="dokarkiv" />
			{data.length === 1 ? (
				<Histark data={data[0]} transaksjon={transaksjoner?.[0]} />
			) : (
				<DollyFieldArray header={'Dokument'} data={data} expandable={data?.length > 2}>
					{(dokument, idx) => {
						return (
							<Histark
								data={dokument}
								transaksjon={transaksjoner?.[transaksjoner.length - 1 - idx]}
							/>
						)
					}}
				</DollyFieldArray>
			)}
		</>
	)
}
