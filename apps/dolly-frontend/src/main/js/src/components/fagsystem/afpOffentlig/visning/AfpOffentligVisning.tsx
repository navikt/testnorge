import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'
import Loading from '@/components/ui/loading/Loading'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { Alert } from '@navikt/ds-react'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { useTpOrdning } from '@/utils/hooks/usePensjon'

export const sjekkManglerAfpOffentligData = (afpOffentligData) => {
	return (
		afpOffentligData?.length < 1 ||
		afpOffentligData?.every(
			(miljoData) => !miljoData.data || Object.keys(miljoData?.data)?.length < 1,
		)
	)
}

export const showTpNavn = (tpId) => {
	const { tpOrdningData } = useTpOrdning()
	const tpOrdning = tpOrdningData?.find((tpOrdning) => tpOrdning.value === tpId)
	if (tpOrdning) {
		return tpOrdning.label
	}
	return tpId
}

const DataVisning = ({ data }) => {
	return (
		<>
			<div className="person-visning_content">
				<TitleValue
					title="Direktekall"
					value={data?.direktekall?.map((tpId) => showTpNavn(tpId))?.join(', ')}
					size="full-width"
				/>
				<DollyFieldArray data={data?.mocksvar} header="AFP offentlig">
					{(mocksvar, idx) => (
						<React.Fragment key={idx}>
							<TitleValue title="TP-ordning" value={showTpNavn(mocksvar?.tpId)} />
							<TitleValue title="Status AFP" value={showLabel('statusAfp', mocksvar?.statusAfp)} />
							<TitleValue title="Virkningsdato" value={formatDate(mocksvar?.virkningsDato)} />
							<TitleValue title="Sist benyttet G" value={mocksvar?.sistBenyttetG} />
							<DollyFieldArray data={mocksvar?.belopsListe} header="Beløp" nested>
								{(belop, idy) => (
									<React.Fragment key={idx + idy}>
										<TitleValue title="F.o.m. dato" value={formatDate(belop?.fomDato)} />
										<TitleValue title="Beløp" value={belop?.belop} />
									</React.Fragment>
								)}
							</DollyFieldArray>
						</React.Fragment>
					)}
				</DollyFieldArray>
			</div>
		</>
	)
}

export const AfpOffentligVisning = ({ data, loading, bestillingIdListe, tilgjengeligMiljoe }) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'PEN_AFP_OFFENTLIG')

	if (loading) {
		return <Loading label="Laster AFP offentlig-data" />
	}

	if (!data) {
		return null
	}

	const manglerFagsystemdata = sjekkManglerAfpOffentligData(data)

	const miljoerMedData = data?.map((miljoData) => miljoData.data && miljoData.miljo)
	const errorMiljoer = bestilteMiljoer?.filter((miljo) => !miljoerMedData?.includes(miljo))

	const forsteMiljo = data.find((miljoData) => miljoData?.data)?.miljo

	const filteredData =
		tilgjengeligMiljoe && data.filter((item) => tilgjengeligMiljoe.includes(item.miljo))

	return (
		<ErrorBoundary>
			<SubOverskrift label="AFP offentlig" iconKind="pensjon" isWarning={manglerFagsystemdata} />
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke AFP offentlig-data på person
				</Alert>
			) : (
				<MiljoTabs
					bestilteMiljoer={bestilteMiljoer}
					errorMiljoer={errorMiljoer}
					forsteMiljo={forsteMiljo}
					data={filteredData ? filteredData : data}
				>
					<DataVisning />
				</MiljoTabs>
			)}
		</ErrorBoundary>
	)
}
