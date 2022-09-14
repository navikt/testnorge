import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'
import { KrrApi } from '~/service/Api'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { ArrayHistorikk } from '~/components/ui/historikk/ArrayHistorikk'
import { AlertStripeAdvarsel } from 'nav-frontend-alertstriper'

type KrrVisningProps = {
	data: Array<Data>
	loading: boolean
}

type VisningProps = {
	data: Data
}

type Data = {
	sdpLeverandoer: number
	registrert: boolean
	reservert: boolean
	epost: string
	mobil: string
	spraak: string
	gyldigFra: string
	sdpAdresse: string
}

type SdpLeverandoer = {
	data: {
		navn: string
	}
}

export const Visning = ({ data }: VisningProps) => {
	return (
		<>
			<LoadableComponent
				onFetch={() =>
					data.sdpLeverandoer
						? KrrApi.getSdpLeverandoer(data.sdpLeverandoer).then((leverandoer: SdpLeverandoer) => {
								return leverandoer.data
						  })
						: Promise.resolve()
				}
				render={(response) =>
					data && (
						<>
							<TitleValue title="Gyldig fra" value={Formatters.formatDate(data.gyldigFra)} />
							<TitleValue
								title="Registrert i KRR"
								value={Formatters.oversettBoolean(data.registrert)}
							/>
							<TitleValue
								title="Reservert mot digitalkommunikasjon"
								value={Formatters.oversettBoolean(data.reservert)}
							/>
							<TitleValue title="E-post" value={data.epost} />
							<TitleValue title="Mobilnummer" value={data.mobil} />
							<TitleValue
								title="Språk"
								value={
									data.spraak &&
									Formatters.showLabel('spraaktype', data.spraak.toLowerCase().replace(' ', ''))
								}
							/>
							<TitleValue title="SDP Adresse" value={data.sdpAdresse} />
							<TitleValue
								title="SDP Leverandør"
								value={response ? response.navn : data.sdpLeverandoer}
							/>
						</>
					)
				}
				label="Laster KRR data"
			/>
		</>
	)
}

export const KrrVisning = ({ data, loading }: KrrVisningProps) => {
	if (loading) {
		return <Loading label="Laster KRR data" />
	}
	if (!data) {
		return null
	}

	const sortedData = Array.isArray(data) ? data?.slice().reverse() : data

	const antallKrr = sortedData?.length
	const antallFremtidige = sortedData?.filter(
		(krr) => krr.gyldigFra && new Date(krr.gyldigFra) > new Date()
	).length

	const gyldigeData =
		antallKrr > antallFremtidige ? sortedData?.slice(0, antallFremtidige + 1) : sortedData
	const historiskeData =
		antallKrr > gyldigeData?.length ? sortedData?.slice(gyldigeData?.length) : []

	const manglerFagsystemdata = sortedData?.length < 1

	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift
					label="Kontaktinformasjon og reservasjon"
					iconKind="krr"
					isWarning={manglerFagsystemdata}
				/>
				{manglerFagsystemdata ? (
					<AlertStripeAdvarsel form="inline" style={{ marginBottom: '20px' }}>
						Kunne ikke hente KRR-data på person
					</AlertStripeAdvarsel>
				) : (
					<div className="person-visning_content">
						<ArrayHistorikk
							component={Visning}
							data={gyldigeData}
							historiskData={historiskeData}
							header={''}
						/>
					</div>
				)}
			</div>
		</ErrorBoundary>
	)
}
