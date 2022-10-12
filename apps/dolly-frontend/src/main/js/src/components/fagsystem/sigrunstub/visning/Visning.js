import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import Loading from '~/components/ui/loading/Loading'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Panel from '~/components/ui/panel/Panel'
import { Alert } from '@navikt/ds-react'
import Formatters from '~/utils/DataFormatter'

const Visning = ({ data, header, expandable }) => {
	if (!data || data.length === 0) {
		return false
	}
	return (
		<DollyFieldArray header={header} data={data} expandable={expandable} nested>
			{(inntekt, idx) => (
				<React.Fragment key={idx}>
					<TitleValue title="Inntektsår" value={inntekt.inntektsaar} />
					<TitleValue title="Tjeneste" value={inntekt.tjeneste} />
					<TitleValue title="Type inntekt" value={inntekt.grunnlag} kodeverk={inntekt.tjeneste} />
					<TitleValue
						title={inntekt.grunnlag === 'skatteoppgjoersdato' ? 'Oppgjørsdato' : 'Beløp'}
						value={
							inntekt.grunnlag === 'skatteoppgjoersdato'
								? Formatters.formatDate(inntekt.verdi)
								: inntekt.verdi
						}
					/>
				</React.Fragment>
			)}
		</DollyFieldArray>
	)
}

const FastlandVisning = ({ data }) => {
	return <Visning data={data} header="Fastlands-Norge" expandable={true} />
}
const SvalbardVisning = ({ data }) => {
	return <Visning data={data} header="Svalbard" expandable={false} />
}

const getInntektsperiode = (fastlandsData, svalbardsData) => {
	const fastland = fastlandsData?.map((f) => f.inntektsaar)
	const svalbard = svalbardsData?.map((s) => s.inntektsaar)
	const foersteAar = Math.min(...fastland?.concat(...svalbard))
	const sisteAar = Math.max(...fastland?.concat(...svalbard))
	return foersteAar === sisteAar ? `${foersteAar}` : `${foersteAar} - ${sisteAar}`
}

export const SigrunstubVisning = ({ data, loading, visTittel = true }) => {
	if (loading) return <Loading label="Laster sigrunstub-data" />
	if (!data) {
		return null
	}
	const manglerFagsystemdata = data?.length < 1

	const grunnlag = data[0]?.grunnlag
	const svalbardGrunnlag = data[0]?.svalbardGrunnlag

	const sortedData = (initialData) =>
		Array.isArray(initialData) ? initialData.slice().reverse() : initialData

	const inntektsperiode = !manglerFagsystemdata && getInntektsperiode(grunnlag, svalbardGrunnlag)

	return (
		<>
			{visTittel && (
				<SubOverskrift
					label="Skatteoppgjør (Sigrun)"
					iconKind="sigrun"
					isWarning={manglerFagsystemdata}
				/>
			)}
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Kunne ikke hente skatteoppgjør-data på person
				</Alert>
			) : (
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
			)}
		</>
	)
}
