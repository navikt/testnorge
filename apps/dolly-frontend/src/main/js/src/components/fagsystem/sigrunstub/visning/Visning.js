import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import Loading from '~/components/ui/loading/Loading'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Panel from '~/components/ui/panel/Panel'

const FastlandVisning = ({ data }) => {
	if (!data || data.length === 0) return false
	return (
		<DollyFieldArray header="Fastlands-Norge" data={data} nested expandable>
			{(inntekt, idx) => (
				<React.Fragment key={idx}>
					<TitleValue title="Inntektsår" value={inntekt.inntektsaar} />
					<TitleValue title="Tjeneste" value={inntekt.tjeneste} />
					<TitleValue title="Type inntekt" value={inntekt.grunnlag} kodeverk={inntekt.tjeneste} />
					<TitleValue title="Beløp" value={inntekt.verdi} />
				</React.Fragment>
			)}
		</DollyFieldArray>
	)
}

const SvalbardVisning = ({ data }) => {
	if (!data || data.length === 0) return false
	return (
		<DollyFieldArray header="Svalbard" data={data} nested>
			{(inntekt, idx) => (
				<React.Fragment key={idx}>
					<TitleValue title="Inntektsår" value={inntekt.inntektsaar} />
					<TitleValue title="Tjeneste" value={inntekt.tjeneste} />
					<TitleValue title="Type inntekt" value={inntekt.grunnlag} kodeverk={inntekt.tjeneste} />
					<TitleValue title="Beløp" value={inntekt.verdi} />
				</React.Fragment>
			)}
		</DollyFieldArray>
	)
}

const getInntektsperiode = (fastlandsData, svalbardsData) => {
	const fastland = fastlandsData?.map((f) => f.inntektsaar)
	const svalbard = svalbardsData?.map((s) => s.inntektsaar)
	const foersteAar = Math.min(...fastland.concat(...svalbard))
	const sisteAar = Math.max(...fastland.concat(...svalbard))
	return foersteAar === sisteAar ? foersteAar : `${foersteAar} - ${sisteAar}`
}

export const SigrunstubVisning = ({ data, loading, visTittel = true }) => {
	if (loading) return <Loading label="Laster sigrunstub-data" />
	if (!data || data.length === 0) return false
	const grunnlag = data[0].grunnlag
	const svalbardGrunnlag = data[0].svalbardGrunnlag

	const sortedData = (data) => (Array.isArray(data) ? data.slice().reverse() : data)

	const inntektsperiode = getInntektsperiode(grunnlag, svalbardGrunnlag)
	return (
		<div>
			{visTittel && <SubOverskrift label="Skatteoppgjør (Sigrun)" iconKind="sigrun" />}
			<ErrorBoundary>
				{grunnlag?.length + svalbardGrunnlag?.length > 5 ? (
					<Panel heading={`Skatteoppgjør (${inntektsperiode})`}>
						<div className="person-visning_content">
							<FastlandVisning data={sortedData(grunnlag)} />
							<SvalbardVisning data={sortedData(svalbardGrunnlag)} />
						</div>
					</Panel>
				) : (
					<div className="person-visning_content">
						<FastlandVisning data={sortedData(grunnlag)} />
						<SvalbardVisning data={sortedData(svalbardGrunnlag)} />
					</div>
				)}
			</ErrorBoundary>
		</div>
	)
}
