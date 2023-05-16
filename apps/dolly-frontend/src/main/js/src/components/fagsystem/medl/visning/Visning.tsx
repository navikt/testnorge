import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import MedlVisning from './MedlVisning'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'
import Loading from '@/components/ui/loading/Loading'

interface Form {
	data?: Array<MiljoDataListe>
	bestillingIdListe: Array<string>
	loading: boolean
}

type MiljoDataListe = {
	miljo: string
	data: Array<any>
}

type MedlTypes = {
	data?: any
	miljo?: string
}

const Medl = ({ data, miljo }: MedlTypes) => {
	if (!data) return null

	return <MedlVisning journalpost={data} miljoe={miljo} />
}

export default ({ data, bestillingIdListe, loading, tilgjengeligMiljoe }: Form) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'dokarkiv')

	if (loading) {
		return <Loading label="Laster dokument-data" />
	}

	if (!data) {
		return null
	}

	const miljoerMedData = data?.map((miljoData) => miljoData.data && miljoData.miljo)
	const errorMiljoer = bestilteMiljoer.filter((miljo) => !miljoerMedData?.includes(miljo))

	const forsteMiljo = data.find((miljoData) => miljoData?.data)?.miljo

	const filteredData =
		tilgjengeligMiljoe && data.filter((item) => item.miljo === tilgjengeligMiljoe)

	return (
		<>
			<SubOverskrift label="Dokumenter" iconKind="dokarkiv" />
			<MiljoTabs
				bestilteMiljoer={bestilteMiljoer}
				errorMiljoer={errorMiljoer}
				forsteMiljo={forsteMiljo}
				data={filteredData || data}
			>
				<Medl />
			</MiljoTabs>
		</>
	)
}
