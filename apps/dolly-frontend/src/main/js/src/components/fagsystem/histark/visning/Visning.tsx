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

const getHeader = (dokument: any) => {
	return `Dokument (${dokument?.filnavn})`
}

const Histark = ({ data, dokumentInfoId, idx }) => {
	if (!data) return null

	return <HistarkVisning dokument={data} dokumentInfoId={dokumentInfoId} idx={idx} />
}

export default ({ data, loading, ident }: Form) => {
	const { transaksjonsid, loading: loadingTransaksjon } = useTransaksjonsid('HISTARK', ident)
	let dokumentInfoIder: number[] = []
	transaksjonsid?.forEach((transaksjon) =>
		transaksjon?.transaksjonId.forEach((indreTransaksjon) =>
			dokumentInfoIder.push(indreTransaksjon.dokumentInfoId),
		),
	)

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
				<Histark data={data[0]} dokumentInfoId={dokumentInfoIder?.[0]} idx={0} />
			) : (
				<DollyFieldArray
					getHeader={getHeader}
					header={'Dokument'}
					data={data}
					expandable={data?.length > 2}
				>
					{(dokument, idx) => {
						return <Histark data={dokument} dokumentInfoId={dokumentInfoIder?.[idx]} idx={idx} />
					}}
				</DollyFieldArray>
			)}
		</>
	)
}
