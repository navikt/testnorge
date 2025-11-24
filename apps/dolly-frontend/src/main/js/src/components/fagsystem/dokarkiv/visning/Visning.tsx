import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import DokarkivVisning from './DokarkivVisning'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'
import Loading from '@/components/ui/loading/Loading'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Journalpost } from '@/utils/hooks/useDokumenter'

interface Form {
	data?: Array<MiljoDataListe>
	bestillingIdListe: Array<string>
	loading: boolean
	tilgjengeligMiljoe?: string
}

type MiljoDataListe = {
	miljo: string
	data: Array<Journalpost>
}

const Dokarkiv = ({ data, miljo }: MiljoDataListe) => {
	if (!data || data?.length < 1) {
		return null
	}
	if (data.length === 1) {
		return <DokarkivVisning journalpost={data[0]} miljoe={miljo} />
	}
	return (
		<DollyFieldArray header={'Dokument'} data={data} expandable={data?.length > 2}>
			{(dokument: Journalpost) => {
				return <DokarkivVisning journalpost={dokument} miljoe={miljo} />
			}}
		</DollyFieldArray>
	)
}

export default ({ data, bestillingIdListe, loading, tilgjengeligMiljoe }: Form) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'DOKARKIV')

	if (loading) {
		return <Loading label="Laster dokument-data" />
	}

	if (!data) {
		return null
	}

	const miljoerMedData = data?.map((miljoData) => miljoData.data && miljoData.miljo)
	const errorMiljoer = bestilteMiljoer.filter((miljo) => !miljoerMedData?.includes(miljo))

	const forsteMiljo = data.find((miljoData) => miljoData?.data)?.miljo

	const mergeData = () => {
		const mergeMiljo = []
		data.forEach((item) => {
			const indexOfMiljo = mergeMiljo.findIndex((dok) => dok?.miljo === item?.miljo)
			if (indexOfMiljo >= 0) {
				mergeMiljo[indexOfMiljo].data?.push(item.data)
			} else {
				mergeMiljo.push({
					data: item.data && [item.data],
					miljo: item.miljo,
				})
			}
		})
		return mergeMiljo
	}

	const mergedData = mergeData()

	const filteredData =
		tilgjengeligMiljoe && mergedData?.filter((item) => tilgjengeligMiljoe.includes(item?.miljo))

	return (
		<>
			<SubOverskrift label="Dokumenter" iconKind="dokarkiv" />
			<MiljoTabs
				bestilteMiljoer={bestilteMiljoer}
				errorMiljoer={errorMiljoer}
				forsteMiljo={forsteMiljo}
				data={filteredData || mergedData}
			>
				<Dokarkiv />
			</MiljoTabs>
		</>
	)
}
