import React, { useEffect, useState } from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Loading from '@/components/ui/loading/Loading'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { Alert } from '@navikt/ds-react'
import { fetchTpOrdninger } from '@/components/fagsystem/tjenestepensjon/form/Form'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { useTpDataYtelse } from '@/utils/hooks/useFagsystemer'

export const sjekkManglerTpData = (tpData) => {
	return tpData?.length < 1 || tpData?.every((miljoData) => miljoData.data?.length < 1)
}

const TpYtelse = ({ ordning, ident, miljo }) => {
	const { loading, tpDataYtelse } = useTpDataYtelse(ident, ordning?.ordning, miljo)

	if (loading) {
		return <Loading label="Laster ytelser ..." />
	}

	if (!tpDataYtelse || tpDataYtelse?.length < 1) {
		return null
	}

	return (
		<DollyFieldArray data={tpDataYtelse} header="Ytelser" nested>
			{(ytelse, idx) => (
				<div className="person-visning_content" key={idx}>
					<TitleValue title="Type" value={showLabel('tjenestepensjonYtelseType', ytelse?.type)} />
					<TitleValue
						title="TP-forhold f.o.m. dato"
						value={formatDate(ytelse?.datoInnmeldtYtelseFom)}
					/>
					<TitleValue
						title="Dato iverksatt f.o.m."
						value={formatDate(ytelse?.datoYtelseIverksattFom)}
					/>
					<TitleValue
						title="Dato iverksatt t.o.m."
						value={formatDate(ytelse?.datoYtelseIverksattTom)}
					/>
				</div>
			)}
		</DollyFieldArray>
	)
}

const TpOrdning = ({ data, miljo, ordninger, ident }) => {
	if (!data) return null

	const ordningNavn = (nr) => {
		const ordning = ordninger.find((o) => o.value === nr)
		return ordning ? ordning.label : nr
	}

	return (
		<DollyFieldArray data={data} header="Tjenestepensjonsforhold">
			{(ordning, idx) => (
				<>
					<TitleValue size="large" title="Ordning" value={ordningNavn(ordning.ordning)} />
					<TpYtelse ordning={ordning} ident={ident} miljo={miljo} idx={idx} />
				</>
			)}
		</DollyFieldArray>
	)
}

export const TpVisning = ({ data, loading, bestillingIdListe, tilgjengeligMiljoe, ident }) => {
	const [ordninger, setOrdninger] = useState(Options('tpOrdninger'))

	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'TP_FORVALTER')

	useEffect(() => {
		if (!ordninger.length) {
			fetchTpOrdninger()
		}
	}, [])

	useEffect(() => {
		setOrdninger(Options('tpOrdninger'))
	}, [Options('tpOrdninger')])

	if (loading) {
		return <Loading label="Laster tjenestepensjon-data" />
	}
	if (!data) {
		return null
	}

	const manglerFagsystemdata = sjekkManglerTpData(data)

	const miljoerMedData = data?.map((miljoData) => miljoData.data?.length > 0 && miljoData.miljo)
	const errorMiljoer = bestilteMiljoer.filter((miljo) => !miljoerMedData?.includes(miljo))

	const forsteMiljo = data.find((miljoData) => miljoData?.data?.length > 0)?.miljo

	const filteredData =
		tilgjengeligMiljoe && data.filter((item) => tilgjengeligMiljoe.includes(item.miljo))

	return (
		<ErrorBoundary>
			<SubOverskrift
				label="Tjenestepensjon (TP)"
				iconKind="pensjon"
				isWarning={manglerFagsystemdata}
			/>
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke tjenestepensjon-data på person
				</Alert>
			) : (
				<MiljoTabs
					bestilteMiljoer={bestilteMiljoer}
					errorMiljoer={errorMiljoer}
					forsteMiljo={forsteMiljo}
					data={filteredData || data}
				>
					<TpOrdning ordninger={ordninger} ident={ident} />
				</MiljoTabs>
			)}
		</ErrorBoundary>
	)
}
