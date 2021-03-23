import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'
import { Historikk } from '~/components/ui/historikk/Historikk'
import { KrrApi } from '~/service/Api'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const Visning = ({ data }) => {
	return (
		<>
			<LoadableComponent
				onFetch={() =>
					data.sdpLeverandoer
						? KrrApi.getSdpLeverandoer(data.sdpLeverandoer).then(leverandoer => {
								return leverandoer.data
						  })
						: Promise.resolve()
				}
				render={response =>
					data && (
						<>
							<TitleValue title="Mobilnummer" value={data.mobil} />
							<TitleValue title="E-post" value={data.epost} />
							<TitleValue
								title="Språk"
								value={
									data.spraak &&
									Formatters.showLabel('spraaktype', data.spraak.toLowerCase().replace(' ', ''))
								}
							/>
							<TitleValue
								title="Reservert mot digitalkommunikasjon"
								value={Formatters.oversettBoolean(data.reservert)}
							/>
							<TitleValue title="Gyldig fra" value={Formatters.formatDate(data.gyldigFra)} />
							<TitleValue
								title="Registrert i DKIF"
								value={Formatters.oversettBoolean(data.registrert)}
							/>
							<TitleValue title="SDP Adresse" value={data.sdpAdresse} />
							<TitleValue
								title="SDP Leverandør"
								value={response ? response.navn : data.sdpLeverandoer}
							/>
						</>
					)
				}
			/>
		</>
	)
}

export const KrrVisning = ({ data, loading }) => {
	if (loading) return <Loading label="laster krr data" />
	if (!data) return false

	const sortedData = Array.isArray(data) ? data.slice().reverse() : data

	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift label="Kontaktinformasjon og reservasjon" iconKind="krr" />
				<div className="person-visning_content">
					<Historikk component={Visning} data={sortedData} />
				</div>
			</div>
		</ErrorBoundary>
	)
}
