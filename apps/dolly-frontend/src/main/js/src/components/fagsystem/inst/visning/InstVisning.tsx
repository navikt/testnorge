import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatStringDates, showLabel } from '@/utils/DataFormatter'
import Loading from '@/components/ui/loading/Loading'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { Alert } from '@navikt/ds-react'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'

const getSortedData = (data) => {
	return Array.isArray(data)
		? data.slice().sort(function (a, b) {
				const datoA = new Date(a.startdato)
				const datoB = new Date(b.startdato)

				return datoA < datoB ? 1 : datoA > datoB ? -1 : 0
			})
		: data
}

export const sjekkManglerInstData = (instData) => {
	return instData?.length < 1 || instData?.every((miljoData) => miljoData.data?.length < 1)
}

export const InstVisning = ({ data, loading, bestillingIdListe, tilgjengeligMiljoe }) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'INST2')

	if (loading) {
		return <Loading label="Laster inst data" />
	}
	if (!data) {
		return null
	}

	const manglerFagsystemdata = sjekkManglerInstData(data)

	const miljoerMedData = data?.map((miljoData) => miljoData.data?.length > 0 && miljoData.miljo)
	const errorMiljoer = bestilteMiljoer.filter((miljo) => !miljoerMedData?.includes(miljo))

	const forsteMiljo = data.find((miljoData) => miljoData?.data?.length > 0)?.miljo
	const sortedData = data.map((miljoeData) => {
		if (miljoeData.data) {
			miljoeData.data = getSortedData(miljoeData?.data)
		}
		return miljoeData
	})

	const filteredData =
		tilgjengeligMiljoe && sortedData.filter((item) => tilgjengeligMiljoe.includes(item.miljo))

	return (
		<ErrorBoundary>
			<SubOverskrift
				label="Institusjonsopphold"
				iconKind="institusjon"
				isWarning={manglerFagsystemdata}
			/>
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke institusjonsopphold-data på person
				</Alert>
			) : (
				<MiljoTabs
					bestilteMiljoer={bestilteMiljoer}
					errorMiljoer={errorMiljoer}
					forsteMiljo={forsteMiljo}
					data={filteredData || sortedData}
				>
					<DollyFieldArray nested>
						{(opphold, idx) => (
							<div className="person-visning_content" key={idx}>
								<TitleValue
									title="Institusjonstype"
									value={showLabel('institusjonstype', opphold.institusjonstype)}
								/>
								<TitleValue title="Startdato" value={formatStringDates(opphold.startdato)} />
								<TitleValue
									title="Forventet sluttdato"
									value={formatStringDates(opphold.forventetSluttdato)}
								/>
								<TitleValue title="Sluttdato" value={formatStringDates(opphold.sluttdato)} />
							</div>
						)}
					</DollyFieldArray>
				</MiljoTabs>
			)}
		</ErrorBoundary>
	)
}
